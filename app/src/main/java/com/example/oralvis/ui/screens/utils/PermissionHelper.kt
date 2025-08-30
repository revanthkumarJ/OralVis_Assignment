package com.example.oralvis.ui.screens.utils

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext


@Composable
fun EnsureCameraAndReadPermissions(content: @Composable () -> Unit) {
    val cameraGranted = remember { mutableStateOf(false) }
    val readGranted = remember { mutableStateOf(false) }

    val readPermission = if (Build.VERSION.SDK_INT >= 33) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else Manifest.permission.READ_EXTERNAL_STORAGE

    val readLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        readGranted.value = granted
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraGranted.value = granted
        if (granted && !readGranted.value) {
            readLauncher.launch(readPermission)
        }
    }

    // Launch camera permission request once
    LaunchedEffect(Unit) {
        if (!cameraGranted.value) {
            cameraLauncher.launch(Manifest.permission.CAMERA)
        } else if (!readGranted.value) {
            readLauncher.launch(readPermission)
        }
    }

    if (cameraGranted.value && readGranted.value) {
        content()
    } else {
        // Show message if any permission is not granted
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Give permission to continue")
        }
    }
}
