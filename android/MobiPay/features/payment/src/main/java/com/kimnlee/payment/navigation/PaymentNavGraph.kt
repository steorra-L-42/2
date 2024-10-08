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
import com.kimnlee.payment.data.repository.PaymentRepository
import com.kimnlee.payment.presentation.screen.DigitalReceiptScreen
import com.kimnlee.payment.presentation.screen.ManualPaymentScreen
import com.kimnlee.payment.presentation.screen.PaymentHistoryScreen
import com.kimnlee.payment.presentation.screen.PaymentCancelScreen
import com.kimnlee.payment.presentation.screen.PaymentSuccessfulScreen
import com.kimnlee.payment.presentation.viewmodel.BiometricViewModel
import com.kimnlee.payment.presentation.viewmodel.PaymentViewModel


fun NavGraphBuilder.paymentNavGraph(
    navController: NavHostController,
    biometricViewModel: BiometricViewModel,
    paymentRepository: PaymentRepository,
    paymentViewModel: PaymentViewModel
) {
    navigation(startDestination = "payment_main", route = "paymenthistory") {
        composable("payment_main",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                PaymentHistoryScreen(
                    onNavigateBack = { navController.navigateUp() },
                    paymentViewModel = paymentViewModel,
                    onNavigateToDetail = { transactionUniqueNo ->
                        navController.navigate("paymenthistory/$transactionUniqueNo")
                    }
                )
            }
        }
        composable("paymenthistory/{transactionUniqueNo}",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            arguments = listOf(navArgument("transactionUniqueNo") { type = NavType.IntType })
        ) { backStackEntry ->
            val transactionUniqueNo = backStackEntry.arguments?.getInt("transactionUniqueNo") ?: 0
            BottomNavigation(navController) {
                DigitalReceiptScreen(
                    onNavigateBack = { navController.navigateUp() },
                    paymentViewModel = paymentViewModel,
                    transactionUniqueNo = transactionUniqueNo
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
        composable(
            route = "payment_cancelled?fcmData={fcmData}",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            arguments = listOf(
                navArgument("fcmData") { type = NavType.StringType; nullable = true }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "mobipay://payment_cancelled{?fcmData}" })
        ) { backStackEntry ->
            val fcmDataJson = backStackEntry.arguments?.getString("fcmData")
            val fcmData = fcmDataJson?.let { Gson().fromJson(it, FCMData::class.java) }

            PaymentCancelScreen(
                navController = navController,
                fcmData = fcmData
            )
        }
    }
}