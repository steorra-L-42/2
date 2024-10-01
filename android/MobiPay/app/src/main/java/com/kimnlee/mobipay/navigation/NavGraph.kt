package com.kimnlee.mobipay.navigation

import android.app.Application
import android.content.Context
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kimnlee.auth.navigation.authNavGraph
import com.kimnlee.auth.presentation.screen.PaymentScreen
import com.kimnlee.auth.presentation.viewmodel.BiometricViewModel
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.cardmanagement.navigation.cardManagementNavGraph
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.common.network.ApiClient
import com.kimnlee.firebase.FCMService
import com.kimnlee.memberinvitation.navigation.memberInvitationNavGraph
import com.kimnlee.mobipay.presentation.screen.HomeScreen
import com.kimnlee.mobipay.presentation.screen.ShowMoreScreen
import com.kimnlee.notification.navigation.notificationNavGraph
import com.kimnlee.payment.navigation.paymentNavGraph
import com.kimnlee.vehiclemanagement.navigation.vehicleManagementNavGraph
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authManager: AuthManager,
    context: Context,
    apiClient: ApiClient,
    loginViewModel: LoginViewModel
) {
    val application = context as Application
    val biometricViewModel = BiometricViewModel(application)
    val cardManagementViewModel = CardManagementViewModel(authManager, apiClient)
    val vehicleManagementViewModel = VehicleManagementViewModel(apiClient)
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(loginViewModel) {
        loginViewModel.navigationEvent.collect { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
                restoreState = false
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "home" else "auth"
    ) {

        composable(
            "home",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                HomeScreen(
                    viewModel = loginViewModel,
                    navController = navController,
                    context = context
                )
            }
        }
        composable("showmore",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                ShowMoreScreen(
                    viewModel = loginViewModel,
                    navController = navController
                )
            }
        }
        composable("payment",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                PaymentScreen(
                    navController = navController,
                    viewModel = biometricViewModel
                )
            }
        }

        authNavGraph(navController, authManager, loginViewModel)
        paymentNavGraph(navController)
        cardManagementNavGraph(navController, authManager, cardManagementViewModel)
        vehicleManagementNavGraph(navController, apiClient, vehicleManagementViewModel)
        memberInvitationNavGraph(navController)
        notificationNavGraph(navController)
    }
}