package com.kimnlee.auth.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.auth.presentation.screen.LoginScreen
import com.kimnlee.auth.presentation.screen.RegistrationScreen
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.auth.presentation.screen.SignUpScreen
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    authManager: AuthManager,
    loginViewModel: LoginViewModel
) {
    navigation(startDestination = "login", route = "auth") {
        composable(
            "login",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate("signup") }
            )
        }
        composable(
            "signup",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            SignUpScreen(
                authManager = authManager,
                viewModel = loginViewModel,
                onNavigateToBack = { navController.navigateUp() }
            )
        }
        composable(
            "registration",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            RegistrationScreen(
                viewModel = loginViewModel,
                onRegistrationSuccess = { navController.navigate("home") },
                onRegistrationFailed = { navController.navigateUp() },
                onBackPressed = { navController.navigate("login") {
                    popUpTo("auth") { inclusive = true }
                }}
            )
        }
    }
}