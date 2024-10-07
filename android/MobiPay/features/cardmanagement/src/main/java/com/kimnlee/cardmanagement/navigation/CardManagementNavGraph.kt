package com.kimnlee.cardmanagement.navigation

import android.net.Uri
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kimnlee.cardmanagement.data.model.CardInfo
import com.kimnlee.cardmanagement.presentation.screen.CardDetailScreen
import com.kimnlee.cardmanagement.presentation.screen.CardManagementOwnedCardListScreen
import com.kimnlee.cardmanagement.presentation.screen.CardManagementScreen
import com.kimnlee.cardmanagement.presentation.screen.CardRegistrationScreen
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.components.BottomNavigation

fun NavGraphBuilder.cardManagementNavGraph(
    navController: NavHostController,
    authManager: AuthManager,
    viewModel: CardManagementViewModel
) {
    navigation(startDestination = "cardmanagement_main", route = "cardmanagement") {
        composable("cardmanagement_main",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                CardManagementScreen(
                    viewModel = viewModel,
                    onNavigateToOwnedCards = { navController.navigate("cardmanagement_owned") },
                    onNavigateToCardDetail = { cardId ->
                        navController.navigate("cardmanagement_detail/$cardId")
                    }
                )
            }
        }
        composable("cardmanagement_owned",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                CardManagementOwnedCardListScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToRegistration = { selectedCards ->
                        val cardInfos = selectedCards.map { CardInfo(it.id, it.cardNo) }
                        val json = Uri.encode(Gson().toJson(cardInfos))
                        navController.navigate("cardmanagement_registration/$json")
                    }
                )
            }
        }
        composable(
            route = "cardmanagement_registration/{cardInfos}",
            arguments = listOf(navArgument("cardInfos") { type = NavType.StringType }),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val json = Uri.decode(backStackEntry.arguments?.getString("cardInfos") ?: "")
            val cardInfos = Gson().fromJson<List<CardInfo>>(json, object : TypeToken<List<CardInfo>>() {}.type)
            BottomNavigation(navController) {
                CardRegistrationScreen(
                    viewModel = viewModel,
                    cardInfos = cardInfos,
                    onNavigateBack = { navController.navigate("cardmanagement_main") }
                )
            }
        }
        composable(
            route = "cardmanagement_detail/{cardId}",
            arguments = listOf(navArgument("cardId") { type = NavType.IntType }),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getInt("cardId") ?: return@composable
            BottomNavigation(navController) {
                CardDetailScreen(
                    viewModel = viewModel,
                    cardId = cardId,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}
