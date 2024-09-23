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
                    onNavigateToRegistration = { navController.navigate("cardmanagement_registration") },
//                    onNavigateToHome = {
//                        navController.navigate("home") {
//                            popUpTo("home") { inclusive = true }
//                        }
//                    }
                )
            }
        }
        composable("cardmanagement_detail") {
            BottomNavigation(navController) {
                CardManagementDetailScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
        composable("cardmanagement_registration") {
            BottomNavigation(navController) {
                CardManagementRegistrationScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToDirectRegistration = { navController.navigate("cardmanagement_direct_registration") },
                )
            }
        }
        composable("cardmanagement_direct_registration") {
            BottomNavigation(navController) {
                CardManagementDirectRegistrationScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}
