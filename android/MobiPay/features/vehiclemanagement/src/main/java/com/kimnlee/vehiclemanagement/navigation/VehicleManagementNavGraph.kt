package com.kimnlee.vehiclemanagement.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.vehiclemanagement.presentation.screen.VehicleManagementDetailScreen
import com.kimnlee.vehiclemanagement.presentation.screen.VehiclemanagementScreen

fun NavGraphBuilder.vehicleManagementNavGraph(navController: NavHostController) {
    navigation(startDestination = "vehiclemanagement_main", route = "vehiclemanagement") {
        composable("vehiclemanagement_main") {
            VehiclemanagementScreen(
                onNavigateToDetail = { navController.navigate("vehiclemanagement_detail") },
                onNavigateToHome = { navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }}
            )
        }
        composable("vehiclemanagement_detail") {
            VehicleManagementDetailScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}