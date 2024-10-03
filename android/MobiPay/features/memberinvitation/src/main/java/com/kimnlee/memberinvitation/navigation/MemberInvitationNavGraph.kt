package com.kimnlee.memberinvitation.navigation

import android.content.Context
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.kimnlee.common.components.BottomNavigation
import com.kimnlee.memberinvitation.presentation.screen.InvitationWaitingScreen
import com.kimnlee.memberinvitation.presentation.screen.InvitedScreen
import com.kimnlee.memberinvitation.presentation.screen.MemberInvitationConfirmationScreen
import com.kimnlee.memberinvitation.presentation.screen.MemberInvitationScreen
import com.kimnlee.memberinvitation.presentation.screen.MemberInvitationViaPhoneScreen
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel


fun NavGraphBuilder.memberInvitationNavGraph(navController: NavHostController, context: Context, memberInvitationViewModel: MemberInvitationViewModel) {
    navigation(startDestination = "member_main/{vehicleId}", route = "memberinvitation") {
        composable("member_main/{vehicleId}",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { backStackEntry ->
//            val vehicleId = backStackEntry.arguments?.getString("vehicleId")?.toIntOrNull() ?: -1
            val vehicleId = 5 //임시로 넣어둔 것
            BottomNavigation(navController) {
                MemberInvitationScreen(
                    context = context,
                    vehicleId = vehicleId,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToInvitePhone = { navController.navigate("memberinvitation_phone/$vehicleId") },
                    onNavigateToConfirmation = { navController.navigate("member_confirmation/$vehicleId") },
                    viewModel = memberInvitationViewModel
                )
            }
        }
        composable("memberinvitation_phone/{vehicleId}",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId")?.toIntOrNull() ?: -1
            MemberInvitationViaPhoneScreen(
                onNavigateBack = { navController.navigateUp() },
                vehicleId = vehicleId,
                onNavigateToConfirmation = { navController.navigate("member_confirmation/$vehicleId") }
            )
        }
        composable("member_confirmation/{vehicleId}",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId")?.toIntOrNull() ?: -1
            MemberInvitationConfirmationScreen(
                onNavigateBack = { navController.navigateUp() },
                vehicleId = vehicleId,
            )
        }
        composable(
        "memberinvitation_invitationwaiting",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            InvitationWaitingScreen(
                memberInvitationViewModel = memberInvitationViewModel,
                navController = navController
            )
        }
        composable(
            "memberinvitation_invited",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            deepLinks = listOf(navDeepLink { uriPattern = "mobipay://youvegotinvited" })
        ) {
            InvitedScreen(
                memberInvitationViewModel = memberInvitationViewModel,
                navController = navController
            )
        }
    }
}