package com.example.oralvis

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.oralvis.ui.screens.camera.CameraScreen
import com.example.oralvis.ui.screens.camera.cameraDestination
import com.example.oralvis.ui.screens.camera.navigateToCameraScreen
import com.example.oralvis.ui.screens.home.HomeRoute
import com.example.oralvis.ui.screens.utils.EnsureCameraAndReadPermissions
import com.example.oralvis.ui.screens.home.HomeScreen
import com.example.oralvis.ui.screens.home.homeDestination
import com.example.oralvis.ui.screens.home.navigateToHomeScreen
import com.example.oralvis.ui.screens.session_detail.navigateToSessionDetailScreen
import com.example.oralvis.ui.screens.session_detail.sessionDetailDestination

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomeRoute) {

        homeDestination(
            onComplete = navController::navigateToCameraScreen,
            onOpenSession = navController::navigateToSessionDetailScreen
        )

        cameraDestination(
            onComplete = navController::navigateToHomeScreen
        )

        sessionDetailDestination(
            navController
        )
    }
}
