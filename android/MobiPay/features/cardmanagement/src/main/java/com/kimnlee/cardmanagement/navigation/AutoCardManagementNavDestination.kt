package com.kimnlee.cardmanagement.navigation

sealed class CardManagementAutoNavDestination(val route: String) {
    object CardManagementMain : CardManagementAutoNavDestination("cardmanagement_main")
    object CardManagementDetail : CardManagementAutoNavDestination("cardmanagement_detail")
}