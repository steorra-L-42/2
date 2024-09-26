package com.kimnlee.vehiclemanagement.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.vehiclemanagement.presentation.screen.VehicleManagementDetailScreen
import com.kimnlee.vehiclemanagement.presentation.screen.VehicleManagementScreen
import com.kimnlee.vehiclemanagement.presentation.screen.VehicleRegistrationScreen

fun NavGraphBuilder.vehicleManagementNavGraph(navController: NavHostController) {
    navigation(startDestination = "vehiclemanagement_main", route = "vehiclemanagement") {
        composable("vehiclemanagement_main") {
            BottomNavigation(navController) {
                VehicleManagementScreen(
                    onNavigateToDetail = { vehicleId ->
                        navController.navigate("vehiclemanagement_detail/$vehicleId")
                    },
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onNavigateToRegistration = { navController.navigate("vehiclemanagement_registration") }
                )
            }
        }
        composable("vehiclemanagement_detail/{vehicleId}") { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId")?.toIntOrNull() ?: -1
            BottomNavigation(navController) {
                VehicleManagementDetailScreen(
                    vehicleId = vehicleId,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToMemberInvitation = { navController.navigate("member_main/$vehicleId") },
                    onNavigateToNotification = { navController.navigate("notification_main") }
                )
            }
        }
        composable("vehiclemanagement_registration") {
            BottomNavigation(navController) {
                VehicleRegistrationScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}