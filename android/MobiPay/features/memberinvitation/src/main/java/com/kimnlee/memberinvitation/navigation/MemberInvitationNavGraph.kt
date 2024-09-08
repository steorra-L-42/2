package com.kimnlee.memberinvitation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.kimnlee.memberinvitation.presentation.screen.MemberInvitationDetailScreen
import com.kimnlee.memberinvitation.presentation.screen.MemberInvitationScreen

fun NavGraphBuilder.memberInvitationNavGraph(navController: NavHostController) {
    navigation(startDestination = "member_main", route = "memberinvitation") {
        composable("member_main") {
            MemberInvitationScreen(
                onNavigateToDetail = { navController.navigate("member_detail") },
                onNavigateToHome = { navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }}
            )
        }
        composable("member_detail") {
            MemberInvitationDetailScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}