package com.kimnlee.mobipay.navigation

sealed class AutoNavDestination(val route: String) {
    object AutoHomeScreen : AutoNavDestination("auto_home")
    object AutoPaymentScreen : AutoNavDestination("auto_payment")
    object AutoCardmanagementScreen : AutoNavDestination("auto_cardmanagement")
    object AutoVehiclemanagementScreen : AutoNavDestination("auto_vehiclemanagement")
    object AutoMemberInvitationScreen : AutoNavDestination("auto_memberinvitation")
}