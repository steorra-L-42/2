package com.kimnlee.cardmanagement.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.cardmanagement.presentation.screen.CardManagementDetailScreen
import com.kimnlee.cardmanagement.presentation.screen.CardManagementDirectRegistrationScreen
import com.kimnlee.cardmanagement.presentation.screen.CardManagementRegistrationOwnedCardScreen
import com.kimnlee.cardmanagement.presentation.screen.CardManagementRegistrationScreen
import com.kimnlee.cardmanagement.presentation.screen.CardManagementScreen
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.common.network.ApiClient

fun NavGraphBuilder.cardManagementNavGraph(
    navController: NavHostController,
    authManager: AuthManager,
    viewModel: CardManagementViewModel,
    apiClient: ApiClient
) {
    navigation(startDestination = "cardmanagement_main", route = "cardmanagement") {
        composable("cardmanagement_main",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                CardManagementScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { navController.navigate("cardmanagement_detail") },
                    onNavigateToRegistration = { navController.navigate("cardmanagement_registration") },
                    onNavigateToOwnedCards = { navController.navigate("cardmanagement_owned") },
                )
            }
        }
        composable("cardmanagement_detail",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                CardManagementDetailScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
        composable("cardmanagement_owned",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                CardManagementRegistrationOwnedCardScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { navController.navigate("cardmanagement_detail") },
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
        composable("cardmanagement_registration",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                CardManagementRegistrationScreen(
                    apiClient = apiClient,
                    viewModel = viewModel,
                    onNavigateToDirectRegistration = { navController.navigate("cardmanagement_direct_registration") },
                    onNavigateBack = { navController.navigateUp() },
                )
            }
        }
        composable("cardmanagement_direct_registration",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            BottomNavigation(navController) {
                CardManagementDirectRegistrationScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}
