package com.kimnlee.cardmanagement.navigation

import androidx.car.app.CarContext
import androidx.car.app.Screen
import com.kimnlee.cardmanagement.presentation.screen.AutoCardManagementScreen
import com.kimnlee.cardmanagement.presentation.screen.AutoCardManagementDetailScreen

class CardManagementAutoNavigation(private val carContext: CarContext) {
    fun mainScreen(): Screen = AutoCardManagementScreen(carContext)

    fun navigate(destination: CardManagementAutoNavDestination): Screen {
        return when (destination) {
            is CardManagementAutoNavDestination.CardManagementMain -> AutoCardManagementScreen(carContext)
            is CardManagementAutoNavDestination.CardManagementDetail -> AutoCardManagementDetailScreen(carContext)
        }
    }
}