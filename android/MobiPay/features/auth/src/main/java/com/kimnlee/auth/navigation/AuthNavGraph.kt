package com.kimnlee.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.auth.presentation.screen.AuthDetailScreen
import com.kimnlee.auth.presentation.screen.AuthScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(startDestination = "auth_main", route = "auth") {
        composable("auth_main") {
            AuthScreen(
                onNavigateToDetail = { navController.navigate("auth_detail") },
                onNavigateToHome = { navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }}
            )
        }
        composable("auth_detail") {
            AuthDetailScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}