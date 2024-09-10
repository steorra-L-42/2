package com.kimnlee.vehiclemanagement.navigation

sealed class VehicleManagementAutoNavDestination(val route: String) {
    object VehicleManagementMain : VehicleManagementAutoNavDestination("vehiclemanagement_main")
    object VehicleManagementDetail : VehicleManagementAutoNavDestination("vehiclemanagement_detail")
}