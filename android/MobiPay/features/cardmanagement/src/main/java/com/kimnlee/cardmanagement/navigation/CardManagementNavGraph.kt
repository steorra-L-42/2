package com.kimnlee.cardmanagement.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.cardmanagement.presentation.screen.CardManagementDetailScreen
import com.kimnlee.cardmanagement.presentation.screen.CardManagementDirectRegistrationScreen
import com.kimnlee.cardmanagement.presentation.screen.CardManagementRegistrationScreen
import com.kimnlee.cardmanagement.presentation.screen.CardManagementScreen
import com.kimnlee.common.components.BottomNavigation

fun NavGraphBuilder.cardManagementNavGraph(navController: NavHostController) {
    navigation(startDestination = "cardmanagement_main", route = "cardmanagement") {
        composable("cardmanagement_main") {
            BottomNavigation(navController) {
                CardManagementScreen(
                    onNavigateToDetail = { navController.navigate("cardmanagement_detail") },
                    onNavigateToRegistration = { navController.navigate("card_registration") },
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        }
        composable("cardmanagement_detail") {
            BottomNavigation(navController) {
                CardManagementDetailScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable("card_registration") {
                CardManagementRegistrationScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToDirectRegistration = { navController.navigate("card_direct_registration") },
                )
            }
            composable("card_direct_registration") {
                CardManagementDirectRegistrationScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}