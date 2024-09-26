package com.kimnlee.mobipay.navigation

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kimnlee.auth.navigation.authNavGraph
import com.kimnlee.auth.presentation.screen.PaymentScreen
import com.kimnlee.auth.presentation.viewmodel.BiometricViewModel
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.cardmanagement.navigation.cardManagementNavGraph
import com.kimnlee.cardmanagement.presentation.screen.CardManagementScreen
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.memberinvitation.navigation.memberInvitationNavGraph
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.mobipay.presentation.screen.HomeScreen
import com.kimnlee.mobipay.presentation.screen.SettingScreen
import com.kimnlee.payment.navigation.paymentNavGraph
import com.kimnlee.vehiclemanagement.navigation.vehicleManagementNavGraph
import com.kimnlee.vehiclemanagement.presentation.screen.VehicleManagementScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authManager: AuthManager,
    context: Context
) {
    val application = context as Application
    val biometricViewModel = BiometricViewModel(application)
    val loginViewModel = LoginViewModel(authManager)
    val cardManagementViewModel = CardManagementViewModel(authManager)
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("home") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {
        authNavGraph(navController, authManager)

        // 홈 화면
        composable("home") {
            BottomNavigation(navController = navController) {
                HomeScreen(
                    viewModel = loginViewModel,
                    navController = navController,
                    context = context
                )
            }
        }

        // 설정 화면
        composable("settings") {
            BottomNavigation(navController = navController) {
                SettingScreen(
                    authManager = authManager,
                    navController = navController
                )
            }
        }

        paymentNavGraph(navController)
        cardManagementNavGraph(navController, authManager)
        vehicleManagementNavGraph(navController)
        memberInvitationNavGraph(navController)
    }
}