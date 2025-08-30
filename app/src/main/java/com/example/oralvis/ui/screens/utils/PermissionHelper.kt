package com.example.oralvis.ui.screens.utils

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun EnsureCameraAndReadPermissions(content: @Composable () -> Unit) {

    val cameraGranted = remember { mutableStateOf(false) }
    val readGranted = remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> cameraGranted.value = granted }


    val readPermission = if (Build.VERSION.SDK_INT >= 33) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else android.Manifest.permission.READ_EXTERNAL_STORAGE


    val readLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> readGranted.value = granted }


    LaunchedEffect(Unit) {
        cameraLauncher.launch(Manifest.permission.CAMERA)
        readLauncher.launch(readPermission)
    }


    if (cameraGranted.value && readGranted.value) content()
}
