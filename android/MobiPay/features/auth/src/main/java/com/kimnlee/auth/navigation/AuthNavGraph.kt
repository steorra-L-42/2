package com.kimnlee.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.auth.presentation.screen.LoginScreen
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.auth.presentation.screen.SignUpScreen
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    authManager: AuthManager
) {
    navigation(startDestination = "auth_main", route = "auth") {
        composable("auth_main") {
            val viewModel = LoginViewModel(authManager)
            LoginScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            SignUpScreen(
                authManager = authManager,
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onNavigateToBack = { navController.navigateUp() }
            )
        }
    }
}