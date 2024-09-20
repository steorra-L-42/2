package com.kimnlee.freedrive.presentation.screen

import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.PlaceListNavigationTemplate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mapbox.androidauto.MapboxCarContext
import com.mapbox.androidauto.R
import com.mapbox.androidauto.internal.extensions.addBackPressedHandler
import com.mapbox.androidauto.location.CarLocationRenderer
import com.mapbox.androidauto.navigation.CarLocationsOverviewCamera
import com.mapbox.androidauto.preview.CarRoutePreviewRequestCallback
import com.mapbox.androidauto.screenmanager.MapboxScreen
import com.mapbox.androidauto.screenmanager.MapboxScreenManager
import com.mapbox.androidauto.search.PlaceRecord
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import kotlinx.coroutines.launch

internal class MerchantListScreen @UiThread constructor(
    private val mapboxCarContext: MapboxCarContext,
) : Screen(mapboxCarContext.carContext) {

    private val merchantList = MainCarSession.merchantList

    var itemList = ItemList.Builder().apply {
        merchantList.forEach { place ->
            addItem(
                Row.Builder()
                    .setTitle(place.name)
                    .addText(place.description ?: "주소 없음")
                    .setBrowsable(true)
                    .setOnClickListener {
                        onPlaceRecordSelected(place)
                    }
                    .build()
            )
        }
    }.build()

    private val carNavigationCamera = CarLocationsOverviewCamera()
    private var carLocationRenderer = CarLocationRenderer()

    // 여기서 부터 주석 처리된 코드들은 기존 예제코드에 있던 placesProvider를 구현해야 하는데
    // 그러려면 com.mapbox.common:common의 버전 충돌을 해결해야 함
    // placesProvider는 크게 필요하지 않은 것 같음
    // 그래서 일단 더미 데이터를 MainCarSession에 만들어서 받아오고 나중에 api 따로 만들어서 구현하는 방법으로 가볼까 함
//    private val placesListOnMapManager = PlacesListOnMapManager(placesProvider)

    init {
        addBackPressedHandler {
            mapboxCarContext.mapboxScreenManager.goBack()
        }
//        repeatOnResumed {
//            placesListOnMapManager.placeRecords.collect { placeRecords ->
//                onPlaceRecordsChanged(placeRecords)
//            }
//        }
//        repeatOnResumed {
//            placesListOnMapManager.placeSelected.filterNotNull().collect { placeRecord ->
//                onPlaceRecordSelected(placeRecord)
//            }
//        }
//        repeatOnResumed {
//            placesListOnMapManager.itemList.collect { invalidate() }
//        }
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                MapboxNavigationApp.registerObserver(mapboxCarContext.routePreviewRequest)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                MapboxNavigationApp.unregisterObserver(mapboxCarContext.routePreviewRequest)
            }

            override fun onResume(owner: LifecycleOwner) {
                mapboxCarContext.mapboxCarMap
                    .registerObserver(carNavigationCamera)
                    .registerObserver(carLocationRenderer)
//                    .registerObserver(placesListOnMapManager)
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                mapboxCarContext.mapboxCarMap
                    .unregisterObserver(carNavigationCamera)
                    .unregisterObserver(carLocationRenderer)
//                    .unregisterObserver(placesListOnMapManager)
            }
        })
    }

    @OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
    override fun onGetTemplate(): Template {

        return PlaceListNavigationTemplate.Builder()
            .setItemList(itemList)
            .setHeader(Header.Builder().setTitle("가맹점 목록").setStartHeaderAction(Action.BACK).build())
            .build()
    }

    private fun onPlaceRecordsChanged(placeRecords: List<PlaceRecord>) {
        invalidate()
        val coordinates = placeRecords.mapNotNull { it.coordinate }
        carNavigationCamera.updateWithLocations(coordinates)
    }

    private fun onPlaceRecordSelected(placeRecord: PlaceRecord) {
        val carRouteRequestCallback = object : CarRoutePreviewRequestCallback {
            override fun onRoutesReady(placeRecord: PlaceRecord, routes: List<NavigationRoute>) {
                MapboxScreenManager.push(MapboxScreen.ROUTE_PREVIEW)
            }

            override fun onUnknownCurrentLocation() {
                onErrorItemList(R.string.car_search_unknown_current_location)
            }

            override fun onDestinationLocationUnknown() {
                onErrorItemList(R.string.car_search_unknown_search_location)
            }

            override fun onNoRoutesFound() {
                onErrorItemList(R.string.car_search_no_results)
            }
        }
        mapboxCarContext.routePreviewRequest.request(placeRecord, carRouteRequestCallback)
    }

    private fun onErrorItemList(@StringRes stringRes: Int) {
        itemList = buildErrorItemList(stringRes)
        invalidate()
    }

    private fun buildErrorItemList(@StringRes stringRes: Int) = ItemList.Builder()
        .setNoItemsMessage(carContext.getString(stringRes))
        .build()

    private fun repeatOnResumed(block: suspend () -> Unit) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                block()
            }
        }
    }
}
