package com.kimnlee.memberinvitation.navigation

sealed class MemberInvitationAutoNavDestination(val route: String) {
    object MemberInvitationMain : MemberInvitationAutoNavDestination("memberinvitation_main")
    object MemberInvitationDetail : MemberInvitationAutoNavDestination("memberinvitation_detail")
}