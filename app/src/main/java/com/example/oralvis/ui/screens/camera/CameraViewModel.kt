package com.example.oralvis.ui.screens.camera

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStream
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.oralvis.data.rep.SessionRepository
import com.example.oralvis.ui.screens.utils.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext


class CameraViewModel(
    private val repository: SessionRepository
) : BaseViewModel<CameraUiState, CameraEvent, CameraAction>(
    initialState = CameraUiState()
) {

    override fun handleAction(action: CameraAction) {
        when (action) {
            is CameraAction.TakePhoto -> takePhoto(action.context, action.controller)
            CameraAction.EndSessionClick -> showEndSessionDialog()
            is CameraAction.ConfirmEndSession -> endSession(action.context)
            CameraAction.DismissDialog -> dismissDialog()
            is CameraAction.OnSessionAgeChange -> mutableStateFlow.update { it.copy(sessionAge =action.age) }
            is CameraAction.OnSessionIdChange -> mutableStateFlow.update { it.copy(sessionId =action.newId) }
            is CameraAction.OnSessionNameChange -> mutableStateFlow.update { it.copy(sessionName =action.name) }
        }
    }

    private fun takePhoto(context: Context, controller: LifecycleCameraController) {
        mutableStateFlow.update { it.copy(isCapturing = true) }

        controller.takePicture(
            context.mainExecutor(),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            val bitmap = image.toBitmap().rotate(image.imageInfo.rotationDegrees)
                            val updatedPhotos = mutableStateFlow.value.photos.toMutableList()
                            updatedPhotos.add(bitmap)

                            mutableStateFlow.update {
                                it.copy(
                                    photos = updatedPhotos,
                                    photoCount = updatedPhotos.size.toLong()
                                )
                            }

                        } catch (e: Exception) {
                            Log.e("CameraViewModel", "Capture error", e)
                        } finally {
                            mutableStateFlow.update { it.copy(isCapturing = false) }
                            image.close()
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    mutableStateFlow.update { it.copy(isCapturing = false) }
                }
            }
        )
    }

    private fun showEndSessionDialog() {
        mutableStateFlow.update { it.copy(showDialog = true) }
    }

    private fun dismissDialog() {
        mutableStateFlow.update { it.copy(showDialog = false) }
    }

    private fun endSession(context: Context) {

        if(state.sessionId.isEmpty()){
            mutableStateFlow.update {
                it.copy(
                    error = "Session Id cannot be empty"
                )
            }
        }
        else if(state.sessionName.isEmpty()){
            mutableStateFlow.update {
                it.copy(
                    error = "Session Name cannot be empty"
                )
            }
        }
        else if(state.sessionAge==0){
            mutableStateFlow.update {
                it.copy(
                    error = "Session Age cannot be empty"
                )
            }
        }
        else{
            viewModelScope.launch(Dispatchers.IO) {
                mutableStateFlow.update { it.copy(isUploading = true, error = "") }

                try {
                    val currentState = mutableStateFlow.value
                    val sessionId = repository.startSession(
                        name = currentState.sessionName,
                        sessionId = currentState.sessionId,
                        age = currentState.sessionAge
                    )

                    currentState.photos.forEach { bitmap ->
                        try {
                            val uri = saveImageToMediaStore(context, bitmap, currentState.sessionName)
                            if (uri != null) {
                                val repo = repository.addPhoto(sessionId, uri)
                            }
                        } catch (e: Exception) { }
                    }

                    repository.updateSession(
                        id = sessionId,
                        sessionId = currentState.sessionId,
                        name = currentState.sessionName,
                        totalPhotos = currentState.photos.size.toLong(),
                        age = currentState.sessionAge
                    )

                    mutableStateFlow.update { it.copy(showDialog = false, isUploading = false) }
                    sendEvent(CameraEvent.SessionEnded)
                } catch (e: Exception) {
                    mutableStateFlow.update { it.copy(isUploading = false) }
                }
            }
        }


    }

    private fun Bitmap.rotate(degrees: Int): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun saveImageToMediaStore(context: Context, bitmap: Bitmap, sessionName: String) =
        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/OralVis/$sessionName")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outStream: OutputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }
            }
            uri
        } catch (e: Exception) {
            null
        }
}

fun Context.mainExecutor() = androidx.core.content.ContextCompat.getMainExecutor(this)


data class CameraUiState(
    val sessionName: String = "",
    val sessionId: String = "",
    val sessionAge: Int = 0,
    val error:String="",
    val photos: List<Bitmap> = emptyList(),
    val photoCount: Long = 0,
    val isCapturing: Boolean = false,
    val showDialog: Boolean = false,
    val isUploading: Boolean = false
)

sealed interface CameraEvent {
    object SessionEnded : CameraEvent
}

sealed interface CameraAction {
    data class TakePhoto(val context: Context, val controller: LifecycleCameraController) : CameraAction
    object EndSessionClick : CameraAction
    data class ConfirmEndSession(val context: Context) : CameraAction
    object DismissDialog : CameraAction
    data class OnSessionIdChange(val newId:String): CameraAction
    data class OnSessionNameChange(val name:String): CameraAction
    data class OnSessionAgeChange(val age:Int): CameraAction
}