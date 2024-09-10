package com.kimnlee.payment.navigation

sealed class PaymentAutoNavDestination(val route: String) {
    object PaymentMain : PaymentAutoNavDestination("payment_main")
    object PaymentDetail : PaymentAutoNavDestination("payment_detail")
}