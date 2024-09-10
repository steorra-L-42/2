package com.kimnlee.memberinvitation.navigation

import androidx.car.app.CarContext
import androidx.car.app.Screen
import com.kimnlee.memberinvitation.presentation.screen.AutoMemberInvitationDetailScreen
import com.kimnlee.memberinvitation.presentation.screen.AutoMemberInvitationScreen

class MemberInvitationAutoNavigation(private val carContext: CarContext) {
    fun mainScreen(): Screen = AutoMemberInvitationScreen(carContext)

    fun navigate(destination: MemberInvitationAutoNavDestination): Screen {
        return when (destination) {
            is MemberInvitationAutoNavDestination.MemberInvitationMain -> AutoMemberInvitationScreen(carContext)
            is MemberInvitationAutoNavDestination.MemberInvitationDetail -> AutoMemberInvitationDetailScreen(carContext)
        }
    }
}