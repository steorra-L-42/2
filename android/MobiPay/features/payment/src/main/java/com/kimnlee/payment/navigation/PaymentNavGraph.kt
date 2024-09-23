package com.kimnlee.payment.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.payment.data.Merchant
import com.kimnlee.payment.data.MerchantTransaction
import com.kimnlee.payment.data.dummyMerchants
import com.kimnlee.payment.data.dummyTransactions
import com.kimnlee.payment.presentation.screen.PaymentDetailScreen
import com.kimnlee.payment.presentation.screen.PaymentScreen

fun NavGraphBuilder.paymentNavGraph(navController: NavHostController) {
    navigation(startDestination = "payment_main", route = "payment") {
        composable("payment_main") {
            BottomNavigation(navController) {
                PaymentScreen(
                    transactions = dummyTransactions,
                    merchants = dummyMerchants,
                    onNavigateToDetail = { transaction ->
                        navController.navigate("payment_detail/${transaction.transaction_unique_no}")
                    },
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
        composable("payment_detail/{transactionUniqueNo}") {
            val transactionUniqueNo = it.arguments?.getString("transactionUniqueNo")?.toLongOrNull() ?: 0L
            BottomNavigation(navController) {
                PaymentDetailScreen(
                    transaction =  dummyTransactions.find { it.transaction_unique_no == transactionUniqueNo} ?: MerchantTransaction(
                        transaction_unique_no = 0,
                        transaction_date = "",
                        transaction_time = "",
                        payment_balance = 0,
                        info = "",
                        cancelled = false,
                        merchant_id = 0,
                        mobi_user_id = 0,
                        owned_card_id = 0
                    ),
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}