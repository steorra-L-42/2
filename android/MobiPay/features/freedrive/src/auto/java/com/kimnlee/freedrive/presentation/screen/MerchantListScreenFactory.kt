package com.kimnlee.freedrive.presentation.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import com.mapbox.androidauto.MapboxCarContext
import com.mapbox.androidauto.screenmanager.MapboxScreenFactory

class MerchantListScreenFactory(
    private val mapboxCarContext: MapboxCarContext
) : MapboxScreenFactory {
    override fun create(carContext: CarContext): Screen {
//        val searchDataProvider = ServiceProvider.INSTANCE.favoritesDataProvider()
//        val placesProvider = FavoritesApi(searchDataProvider)

        return MerchantListScreen(
            mapboxCarContext
//            placesProvider,
//            MapboxScreen.FAVORITES
        )
    }
}
