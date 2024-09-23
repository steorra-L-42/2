package com.kimnlee.freedrive.presentation.screen

import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.navigation.model.TravelEstimate

class CarNavigationInfo internal constructor(

    val navigationInfo: NavigationTemplate.NavigationInfo? = null,

    val destinationTravelEstimate: TravelEstimate? = null,
)
