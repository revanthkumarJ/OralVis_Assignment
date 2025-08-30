package com.example.oralvis.ui.screens.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.oralvis.ui.screens.utils.CameraPreview
import com.example.oralvis.ui.screens.utils.EventsEffect
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    onComplete: () -> Unit,
    viewModel: CameraViewModel = koinViewModel(),
) {
    val context = LocalContext.current

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    // Handle one-time events
    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is CameraEvent.PhotoCaptured -> Log.d("CameraScreen", "Photo captured")
            CameraEvent.SessionEnded -> onComplete()
        }
    }

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.IMAGE_ANALYSIS)
        }
    }

    var flashVisible by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    flashVisible = true
                    viewModel.trySendAction(CameraAction.TakePhoto(context, controller))
                },
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = "Capture")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            CameraPreview(controller, modifier = Modifier.fillMaxSize())

            // Switch camera button
            IconButton(
                onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
                            CameraSelector.DEFAULT_BACK_CAMERA
                        else
                            CameraSelector.DEFAULT_FRONT_CAMERA
                },
                modifier = Modifier.offset(16.dp, 16.dp),
                enabled = !state.isCapturing
            ) {
                Icon(Icons.Default.Cameraswitch, contentDescription = "Switch camera")
            }

            // End Session button
            Button(
                onClick = { viewModel.trySendAction(CameraAction.EndSessionClick) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                enabled = !state.isCapturing && !state.isUploading
            ) {
                Text("End Session (${state.photoCount})")
            }

            // Flash effect
            if (flashVisible) {
                val alpha by animateFloatAsState(targetValue = 1f, animationSpec = tween(100))
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = alpha))
                        .zIndex(1f)
                )
                LaunchedEffect(Unit) {
                    delay(200)
                    flashVisible = false
                }
            }

            // End session dialog
            if (state.showDialog) {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("End Session") },
                    text = {
                        Column {
                            Text("Enter session name:")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = state.sessionId,
                                onValueChange = { viewModel.trySendAction(CameraAction.OnSessionIdChange(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Session Id") },
                                label={Text("Session Id")}
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = state.sessionName,
                                onValueChange = { viewModel.trySendAction(CameraAction.OnSessionNameChange(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Session Name") },
                                label={ Text("Session Name")}
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = state.sessionAge.toString(),
                                onValueChange = {
                                    viewModel.trySendAction(
                                        CameraAction.OnSessionAgeChange(it.toIntOrNull() ?: 0)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text("Session Age") },
                                label={ Text("Session Age")}
                            )
                            if (state.isUploading) {
                                Spacer(modifier = Modifier.height(16.dp))
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                Text(
                                    "Uploading photos...",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { viewModel.trySendAction(CameraAction.ConfirmEndSession(context)) },
                            enabled = !state.isUploading
                        ) {
                            Text("Save & End")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.trySendAction(CameraAction.DismissDialog) },
                            enabled = !state.isUploading
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
