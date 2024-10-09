package com.kimnlee.memberinvitation.navigation

import android.content.Context
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.google.gson.Gson
import com.kimnlee.common.FCMData
import com.kimnlee.common.FCMDataForInvitation
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
            val vehicleId = backStackEntry.arguments?.getString("vehicleId")?.toIntOrNull() ?: -1
//            val vehicleId = 5 //임시로 넣어둔 것
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
                context = context,
                viewModel = memberInvitationViewModel
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
            route = "memberinvitation_invited?fcmDataForInvitation={fcmDataForInvitation}",
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            arguments = listOf(
                navArgument("fcmDataForInvitation") { type = NavType.StringType; nullable = true }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = "mobipay://youvegotinvited/?fcmDataForInvitation={fcmDataForInvitation}" })
        ) { backStackEntry ->
            val fcmDataJson = backStackEntry.arguments?.getString("fcmDataForInvitation")
            val fcmDataForInvitation = fcmDataJson?.let { Gson().fromJson(it, FCMDataForInvitation::class.java) }

            InvitedScreen(
                memberInvitationViewModel = memberInvitationViewModel,
                navController = navController,
                fcmDataForInvitationFromDeeplink = fcmDataForInvitation
            )
        }
//        composable(
//            "memberinvitation_invited",
//            enterTransition = { EnterTransition.None },
//            exitTransition = { ExitTransition.None },
//        ) {
//            InvitedScreen(
//                memberInvitationViewModel = memberInvitationViewModel,
//                navController = navController
//            )
//        }
    }
}