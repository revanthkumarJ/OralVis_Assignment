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
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStream

class CameraViewModel : ViewModel() {

    val capturedImages = mutableStateListOf<String>()

    fun takePhoto(
        context: Context,
        controller: LifecycleCameraController,
        sessionId: String,
        onPhotoSaved: (String) -> Unit
    ) {
        controller.takePicture(
            context.mainExecutor(),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            val bitmap = image.toBitmap().rotate(image.imageInfo.rotationDegrees)

                            val uri = saveImageToMediaStore(context, bitmap, sessionId)
                            if (uri != null) {
                                capturedImages.add(uri.toString())
                                onPhotoSaved(uri.toString())
                            }

                            image.close()
                        } catch (e: Exception) {
                            Log.e("Revanth", "Error saving photo", e)
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("Revanth", "Capture failed: ${exception.message}")
                }
            }
        )
    }

    private fun Bitmap.rotate(degrees: Int): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun saveImageToMediaStore(context: Context, bitmap: Bitmap, sessionId: String) =
        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "OralVis/$sessionId")
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
            Log.e("CameraVM", "Failed to save image in MediaStore", e)
            null
        }
}

// Helper to get main executor from context
fun Context.mainExecutor() = androidx.core.content.ContextCompat.getMainExecutor(this)
