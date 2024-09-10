package com.kimnlee.vehiclemanagement.navigation

import androidx.car.app.CarContext
import androidx.car.app.Screen
import com.kimnlee.vehiclemanagement.presentation.screen.AutoVehicleManagementDetailScreen
import com.kimnlee.vehiclemanagement.presentation.screen.AutoVehicleManagementScreen

class VehicleManagementAutoNavigation(private val carContext: CarContext) {
    fun mainScreen(): Screen = AutoVehicleManagementScreen(carContext)

    fun navigate(destination: VehicleManagementAutoNavDestination): Screen {
        return when (destination) {
            is VehicleManagementAutoNavDestination.VehicleManagementMain -> AutoVehicleManagementScreen(carContext)
            is VehicleManagementAutoNavDestination.VehicleManagementDetail -> AutoVehicleManagementDetailScreen(carContext)
        }
    }
}