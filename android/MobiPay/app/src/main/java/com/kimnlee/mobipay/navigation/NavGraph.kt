package com.kimnlee.mobipay.navigation

import android.app.Application
import android.bluetooth.BluetoothManager
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
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.cardmanagement.navigation.cardManagementNavGraph
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.common.network.ApiClient
import com.kimnlee.common.utils.AutoSaveParkingManager
import com.kimnlee.memberinvitation.navigation.memberInvitationNavGraph
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import com.kimnlee.mobipay.presentation.screen.HomeScreen
import com.kimnlee.mobipay.presentation.screen.ShowMoreScreen
import com.kimnlee.mobipay.presentation.viewmodel.HomeViewModel
import com.kimnlee.mobipay.presentation.viewmodel.ShowMoreViewModel
import com.kimnlee.notification.navigation.notificationNavGraph
import com.kimnlee.payment.data.repository.PaymentRepository
import com.kimnlee.payment.navigation.paymentNavGraph
import com.kimnlee.payment.presentation.viewmodel.BiometricViewModel
import com.kimnlee.payment.presentation.screen.ManualPaymentScreen
import com.kimnlee.payment.presentation.viewmodel.PaymentViewModel
import com.kimnlee.vehiclemanagement.navigation.vehicleManagementNavGraph
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authManager: AuthManager,
    context: Context,
    apiClient: ApiClient,
    loginViewModel: LoginViewModel,
    memberInvitationViewModel: MemberInvitationViewModel,
    paymentRepository: PaymentRepository,
    autoSaveParkingManager: AutoSaveParkingManager
) {
    val application = context as Application
    val biometricViewModel = BiometricViewModel(application)
    val cardManagementViewModel = CardManagementViewModel(authManager, apiClient)
    val vehicleManagementViewModel = VehicleManagementViewModel(apiClient, context, authManager)
    val showMoreViewModel = ShowMoreViewModel(authManager)
    val paymentViewModel = PaymentViewModel(authManager, apiClient)
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
    val homeViewModel = HomeViewModel(apiClient, authManager)

    LaunchedEffect(loginViewModel) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        memberInvitationViewModel.initBluetoothAdapter(bluetoothAdapter)

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
                    loginViewModel = loginViewModel,
                    homeViewModel = homeViewModel,
                    memberInvitationViewModel = memberInvitationViewModel,
                    navController = navController,
                    context = context,
                    autoSaveParkingManager = autoSaveParkingManager
                )
            }
        }
        composable("showmore",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                ShowMoreScreen(
                    loginViewModel = loginViewModel,
                    showMoreViewModel = showMoreViewModel,
                    navController = navController,
                    autoSaveParkingManager = autoSaveParkingManager
                )
            }
        }

        authNavGraph(navController, authManager, loginViewModel)
        paymentNavGraph(navController, biometricViewModel, paymentRepository, paymentViewModel)
        cardManagementNavGraph(
            navController = navController,
            authManager = authManager,
            viewModel = cardManagementViewModel
        )
        vehicleManagementNavGraph(navController, context, apiClient, vehicleManagementViewModel, memberInvitationViewModel, cardManagementViewModel)
        memberInvitationNavGraph(navController, context, memberInvitationViewModel)

        notificationNavGraph(navController)
    }
}