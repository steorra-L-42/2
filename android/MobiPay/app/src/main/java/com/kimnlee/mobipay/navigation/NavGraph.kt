package com.kimnlee.mobipay.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kimnlee.auth.navigation.authNavGraph
import com.kimnlee.cardmanagement.navigation.cardManagementNavGraph
import com.kimnlee.memberinvitation.navigation.memberInvitationNavGraph
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.mobipay.presentation.screen.HomeScreen
import com.kimnlee.payment.navigation.paymentNavGraph
import com.kimnlee.vehiclemanagement.navigation.vehicleManagementNavGraph

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authManager: AuthManager
) {

    val isLoggedIn by authManager.isLoggedIn.collectAsState(initial = false)

    NavHost(
        navController,
        startDestination = if (isLoggedIn) "home" else "auth"
    ) {
        composable("home") {
            HomeScreen(
                navController = navController,
                authManager = authManager
            )
        }
        authNavGraph(navController, authManager)
        paymentNavGraph(navController)
        cardManagementNavGraph(navController)
        vehicleManagementNavGraph(navController)
        memberInvitationNavGraph(navController)
        // 다른 기능 모듈의 내비게이션 그래프 추가
    }
}