package com.example.oralvis.ui.screens.session_detail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class SessionDetailRoute(
    val sessionId: Long=-1
)


fun NavGraphBuilder.sessionDetailDestination(
    navController: NavController
) {
    composable<SessionDetailRoute> {
        SessionDetailScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}

fun NavController.navigateToSessionDetailScreen(id:Long) {
    this.navigate(
        SessionDetailRoute(id),
    )
}