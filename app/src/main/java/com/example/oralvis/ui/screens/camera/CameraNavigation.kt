package com.example.oralvis.ui.screens.camera

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.oralvis.ui.screens.utils.EnsureCameraAndReadPermissions
import kotlinx.serialization.Serializable

@Serializable
data class CameraRoute(
    val sessionId:String=""
)


fun NavGraphBuilder.cameraDestination(
    onComplete: () -> Unit,
) {
    composable<CameraRoute> {
        EnsureCameraAndReadPermissions {
            CameraScreen(
                sessionId = "default_session",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

fun NavController.navigateToCameraScreen(sessionName:String) {
    this.navigate(
        CameraRoute(sessionName),
    )
}