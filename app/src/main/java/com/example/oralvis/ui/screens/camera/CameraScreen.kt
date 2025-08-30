package com.example.oralvis.ui.screens.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.oralvis.ui.screens.utils.CameraPreview
import org.koin.androidx.compose.koinViewModel

@Composable
fun CameraScreen(
    sessionId: String,
    modifier: Modifier=Modifier,
    viewModel: CameraViewModel= koinViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // CameraX controller
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS
            )
        }
    }
    var flashVisible by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    flashVisible = true
                    viewModel.takePhoto(
                        context=context,
                        controller=controller,
                        sessionId=sessionId,
                        onPhotoSaved = {
                            Log.d("Revanth",it)
                        }
                    )
                }
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = "Capture")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding)
        ){
            CameraPreview(
                controller,
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = {
                    controller.cameraSelector=if(controller.cameraSelector== CameraSelector.DEFAULT_FRONT_CAMERA){
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }else{
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    }
                },
                modifier = Modifier.offset(
                    16.dp,16.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "switch camera"
                )
            }

            if (flashVisible) {
                val alpha by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 100),
                )

                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = alpha))
                        .zIndex(1f)
                )

                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(200)
                    flashVisible = false
                }
            }
        }

    }
}
