package com.kimnlee.mobipay.navigation

import androidx.car.app.CarContext
import androidx.car.app.Screen
import com.kimnlee.cardmanagement.navigation.CardManagementAutoNavigation
import com.kimnlee.memberinvitation.navigation.MemberInvitationAutoNavigation
import com.kimnlee.mobipay.ui.AutoHomeScreen
import com.kimnlee.payment.navigation.PaymentAutoNavigation
import com.kimnlee.vehiclemanagement.navigation.VehicleManagementAutoNavigation

class AutoNavigation(private val carContext: CarContext) {
    private val paymentNavigation = PaymentAutoNavigation(carContext)
    private val cardManagementNavigation = CardManagementAutoNavigation(carContext)
    private val vehicleManagementNavigation = VehicleManagementAutoNavigation(carContext)
    private val memberInvitationNavigation = MemberInvitationAutoNavigation(carContext)

    fun navigate(destination: AutoNavDestination): Screen {
        return when (destination) {
            is AutoNavDestination.AutoHomeScreen -> AutoHomeScreen(carContext)
            is AutoNavDestination.AutoPaymentScreen -> paymentNavigation.mainScreen()
            is AutoNavDestination.AutoCardmanagementScreen -> cardManagementNavigation.mainScreen()
            is AutoNavDestination.AutoVehiclemanagementScreen -> vehicleManagementNavigation.mainScreen()
            is AutoNavDestination.AutoMemberInvitationScreen -> memberInvitationNavigation.mainScreen()
        }
    }
}