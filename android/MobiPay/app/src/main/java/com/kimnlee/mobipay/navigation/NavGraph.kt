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
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.memberinvitation.navigation.memberInvitationNavGraph
import com.kimnlee.mobipay.presentation.screen.HomeScreen
import com.kimnlee.mobipay.presentation.screen.ShowMoreScreen
import com.kimnlee.notification.navigation.notificationNavGraph
import com.kimnlee.payment.navigation.paymentNavGraph
import com.kimnlee.vehiclemanagement.navigation.vehicleManagementNavGraph

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authManager: AuthManager,
    context: Context
) {
    val application = context as Application
    val biometricViewModel = BiometricViewModel(application)
    val loginViewModel = LoginViewModel(authManager)

    LaunchedEffect(loginViewModel) {
        loginViewModel.navigationEvent.collect { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) {
//                    saveState = true
                    inclusive = true
                }
                launchSingleTop = true
                restoreState = false
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "auth"
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
                    authManager = authManager,
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
        cardManagementNavGraph(navController, authManager)
        vehicleManagementNavGraph(navController)
        memberInvitationNavGraph(navController)
        notificationNavGraph(navController)
    }
}