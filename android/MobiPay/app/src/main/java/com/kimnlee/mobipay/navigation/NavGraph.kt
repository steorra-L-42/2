package com.kimnlee.mobipay.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kimnlee.auth.navigation.authNavGraph
import com.kimnlee.cardmanagement.navigation.cardManagementNavGraph
import com.kimnlee.memberinvitation.navigation.memberInvitationNavGraph
import com.kimnlee.mobipay.presentation.screen.HomeScreen
import com.kimnlee.payment.navigation.paymentNavGraph
import com.kimnlee.vehiclemanagement.navigation.vehiclemanagementNavGraph

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController)
        }
        authNavGraph(navController)
        paymentNavGraph(navController)
        cardManagementNavGraph(navController)
        vehiclemanagementNavGraph(navController)
        memberInvitationNavGraph(navController)
        // 다른 기능 모듈의 내비게이션 그래프 추가
    }
}
