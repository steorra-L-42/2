package com.kimnlee.cardmanagement.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.cardmanagement.presentation.screen.CardManagementDetailScreen
import com.kimnlee.cardmanagement.presentation.screen.CardManagementRegistrationScreen
import com.kimnlee.cardmanagement.presentation.screen.CardManagementScreen

fun NavGraphBuilder.cardManagementNavGraph(navController: NavHostController) {
    navigation(startDestination = "card_main", route = "cardmanagement") {
        composable("card_main") {
            CardManagementScreen(
                onNavigateToDetail = { navController.navigate("card_detail") },
                onNavigateToRegistration = { navController.navigate("card_registration") },
                onNavigateToHome = { navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }}
            )
        }
        composable("card_registration") {
            CardManagementRegistrationScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        composable("card_detail") {
            CardManagementDetailScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}