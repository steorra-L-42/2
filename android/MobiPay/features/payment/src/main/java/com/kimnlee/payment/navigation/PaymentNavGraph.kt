package com.kimnlee.payment.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.gson.Gson
import com.kimnlee.common.FCMData
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.payment.data.dummyMerchants
import com.kimnlee.payment.data.dummyTransactions
import com.kimnlee.payment.data.model.MerchantTransaction
import com.kimnlee.payment.data.repository.PaymentRepository
import com.kimnlee.payment.presentation.screen.ManualPaymentScreen
import com.kimnlee.payment.presentation.screen.PaymentDetailListScreen
import com.kimnlee.payment.presentation.screen.PaymentDetailScreen
import com.kimnlee.payment.presentation.screen.PaymentSuccessfulScreen
import com.kimnlee.payment.presentation.viewmodel.BiometricViewModel


fun NavGraphBuilder.paymentNavGraph(
    navController: NavHostController,
    biometricViewModel: BiometricViewModel,
    paymentRepository: PaymentRepository
) {
    navigation(startDestination = "payment_main", route = "paymenthistory") {
        composable("payment_main",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                PaymentDetailListScreen(
                    transactions = dummyTransactions,
                    merchants = dummyMerchants,
                    onNavigateToDetail = { transaction ->
                        navController.navigate("paymenthistory/${transaction.transaction_unique_no}")
                    },
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
        composable("paymenthistory/{transactionUniqueNo}",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
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
        composable(
            route = "payment_requestmanualpay?fcmData={fcmData}&registeredCards={registeredCards}",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            arguments = listOf(
                navArgument("fcmData") { type = NavType.StringType; nullable = true },
                navArgument("registeredCards") { type = NavType.StringType; nullable = true }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "mobipay://payment_requestmanualpay{?fcmData}&{registeredCards}" })
        ) { backStackEntry ->
            val fcmDataJson = backStackEntry.arguments?.getString("fcmData")
            val fcmData = fcmDataJson?.let { Gson().fromJson(it, FCMData::class.java) }

            val registeredCardsJson = backStackEntry.arguments?.getString("registeredCards")

            ManualPaymentScreen(
                navController = navController,
                viewModel = biometricViewModel,
                fcmData = fcmData,
                paymentRepository = paymentRepository,
                registeredCards = registeredCardsJson!!
            )
        }
        composable(
            route = "payment_successful?fcmData={fcmData}",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            arguments = listOf(
                navArgument("fcmData") { type = NavType.StringType; nullable = true }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "mobipay://payment_successful{?fcmData}" })
        ) { backStackEntry ->
            val fcmDataJson = backStackEntry.arguments?.getString("fcmData")
            val fcmData = fcmDataJson?.let { Gson().fromJson(it, FCMData::class.java) }

            PaymentSuccessfulScreen(
                navController = navController,
                fcmData = fcmData
            )
        }
    }
}