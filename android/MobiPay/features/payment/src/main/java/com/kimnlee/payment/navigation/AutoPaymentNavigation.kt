package com.kimnlee.payment.navigation

import androidx.car.app.CarContext
import androidx.car.app.Screen
import com.kimnlee.payment.presentation.screen.AutoPaymentDetailScreen
import com.kimnlee.payment.presentation.screen.AutoPaymentScreen

class PaymentAutoNavigation(private val carContext: CarContext) {
    fun mainScreen(): Screen = AutoPaymentScreen(carContext)

    fun navigate(destination: PaymentAutoNavDestination): Screen {
        return when (destination) {
            is PaymentAutoNavDestination.PaymentMain -> AutoPaymentScreen(carContext)
            is PaymentAutoNavDestination.PaymentDetail -> AutoPaymentDetailScreen(carContext)
        }
    }
}