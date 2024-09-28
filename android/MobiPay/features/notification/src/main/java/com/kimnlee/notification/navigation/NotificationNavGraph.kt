package com.kimnlee.notification.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.notification.presentation.screen.NotificationScreen
import com.kimnlee.common.components.BottomNavigation

fun NavGraphBuilder.notificationNavGraph(navController: NavHostController) {
    navigation(startDestination = "notification_main", route = "notification") {
        composable("notification_main",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                NotificationScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}
