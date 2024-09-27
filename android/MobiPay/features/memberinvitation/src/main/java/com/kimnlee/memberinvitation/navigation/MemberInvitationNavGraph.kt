package com.kimnlee.memberinvitation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.memberinvitation.presentation.screen.MemberInvitationScreen
import com.kimnlee.memberinvitation.presentation.screen.MemberInvitationConfirmationScreen
import com.kimnlee.memberinvitation.presentation.screen.MemberInvitationViaPhoneScreen


fun NavGraphBuilder.memberInvitationNavGraph(navController: NavHostController) {
    navigation(startDestination = "member_main/{vehicleId}", route = "memberinvitation") {
        composable("member_main/{vehicleId}",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { backStackEntry ->
//            val vehicleId = backStackEntry.arguments?.getString("vehicleId")?.toIntOrNull() ?: -1
            val vehicleId = 5 //임시로 넣어둔 것
            BottomNavigation(navController) {
                MemberInvitationScreen(
                    vehicleId = vehicleId,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToInvitePhone = { navController.navigate("member_phone/$vehicleId") },
                    onNavigateToConfirmation = { navController.navigate("member_confirmation/$vehicleId") }
                )
            }
        }
        composable("member_phone/{vehicleId}",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId")?.toIntOrNull() ?: -1
            MemberInvitationViaPhoneScreen(
                onNavigateBack = { navController.navigateUp() },
                vehicleId = vehicleId,
                onNavigateToConfirmation = { navController.navigate("member_confirmation/$vehicleId") }
            )
        }
        composable("member_confirmation",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
        composable("member_confirmation/{vehicleId}") {backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId")?.toIntOrNull() ?: -1
            MemberInvitationConfirmationScreen(
                onNavigateBack = { navController.navigateUp() },
                        vehicleId = vehicleId,
            )
        }
    }
}}