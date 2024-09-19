package com.kimnlee.payment.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.payment.data.dummyBillData
import com.kimnlee.payment.presentation.screen.PaymentDetailScreen
import com.kimnlee.payment.presentation.screen.PaymentScreen

fun NavGraphBuilder.paymentNavGraph(navController: NavHostController) {
    navigation(startDestination = "payment_main", route = "payment") {
        composable("payment_main") {
            BottomNavigation(navController) {
                PaymentScreen(
                    onNavigateToDetail = { bill ->
                        val transactionUniqueNo = bill["transaction_unique_no"].toString()
                        navController.navigate("payment_detail/$transactionUniqueNo")
                    },
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
        composable("payment_detail/{id}") {
            val id = it.arguments?.getString("id")?.toIntOrNull()
            BottomNavigation(navController) {
                PaymentDetailScreen(
//                    id = id, // 이거 이렇게 넣어도 되는거 맞나?
                    bill =  dummyBillData[id!!],
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}