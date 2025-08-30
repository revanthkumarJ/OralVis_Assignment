package com.example.oralvis.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute


fun NavGraphBuilder.homeDestination(
    onComplete: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen {
            onComplete()
        }
    }
}

fun NavController.navigateToHomeScreen() {
    this.navigate(
        HomeRoute,
    )
}