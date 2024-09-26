package com.kimnlee.freedrive.presentation.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import com.mapbox.androidauto.MapboxCarContext
import com.mapbox.androidauto.internal.logAndroidAuto
import com.mapbox.androidauto.screenmanager.MapboxScreenFactory

class RoutePreviewScreenFactory(
    private val mapboxCarContext: MapboxCarContext
) : MapboxScreenFactory {
    override fun create(carContext: CarContext): Screen {
        val repository = mapboxCarContext.routePreviewRequest.repository
        val placeRecord = repository?.placeRecord?.value
        val routes = repository?.routes?.value ?: emptyList()
        return if (placeRecord == null || routes.isEmpty()) {
            logAndroidAuto(
                "Showing free drive screen because route preview can only be shown " +
                        "when there is a route. placeRecord=$placeRecord routes.size=${routes.size}"
            )
            FreeDriveCarScreen(mapboxCarContext)
        } else {
            CarRoutePreviewScreen(mapboxCarContext, placeRecord, routes)
        }
    }
}
