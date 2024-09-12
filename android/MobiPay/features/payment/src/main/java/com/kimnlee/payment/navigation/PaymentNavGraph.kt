package com.kimnlee.payment.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.payment.presentation.screen.PaymentDetailScreen
import com.kimnlee.payment.presentation.screen.PaymentScreen

fun NavGraphBuilder.paymentNavGraph(navController: NavHostController) {
    navigation(startDestination = "payment_main", route = "payment") {
        composable("payment_main") {
            BottomNavigation(navController) {
                PaymentScreen(
                    onNavigateToDetail = { navController.navigate("payment_detail") },
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
        composable("payment_detail") {
            BottomNavigation(navController) {
                PaymentDetailScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}