package com.kimnlee.memberinvitation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.memberinvitation.presentation.screen.MemberInvitationDetailScreen
import com.kimnlee.memberinvitation.presentation.screen.MemberInvitationScreen
import com.kimnlee.memberinvitation.presentation.screen.MemberInvitationConfirmationScreen

fun NavGraphBuilder.memberInvitationNavGraph(navController: NavHostController) {
    navigation(startDestination = "member_main/{vehicleId}", route = "memberinvitation") {
        composable("member_main/{vehicleId}") { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId")?.toIntOrNull() ?: -1
            BottomNavigation(navController) {
                MemberInvitationScreen(
                    vehicleId = vehicleId,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToDetail = { navController.navigate("member_detail") },
                    onNavigateToHome = { navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }}
                )
            }
        }
        composable("member_detail") {
            MemberInvitationDetailScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToConfirmation = { navController.navigate("member_confirmation") }
            )
        }
        composable("member_confirmation") {
            MemberInvitationConfirmationScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}