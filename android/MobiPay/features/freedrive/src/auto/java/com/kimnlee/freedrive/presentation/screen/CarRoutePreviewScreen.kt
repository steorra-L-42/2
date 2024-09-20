package com.kimnlee.freedrive.presentation.screen

import android.text.SpannableString
import androidx.annotation.UiThread
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.DurationSpan
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.RoutePreviewNavigationTemplate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mapbox.androidauto.MapboxCarContext
import com.mapbox.androidauto.R
import com.mapbox.androidauto.internal.extensions.addBackPressedHandler
import com.mapbox.androidauto.internal.logAndroidAuto
import com.mapbox.androidauto.location.CarLocationRenderer
import com.mapbox.androidauto.navigation.CarActiveGuidanceMarkers
import com.mapbox.androidauto.navigation.CarCameraMode
import com.mapbox.androidauto.navigation.CarDistanceFormatter
import com.mapbox.androidauto.navigation.CarNavigationCamera
import com.mapbox.androidauto.navigation.audioguidance.muteAudioGuidance
import com.mapbox.androidauto.navigation.speedlimit.CarSpeedLimitRenderer
import com.mapbox.androidauto.preview.CarRouteLineRenderer
import com.mapbox.androidauto.preview.PreviewCarRoutesProvider
import com.mapbox.androidauto.screenmanager.MapboxScreen
import com.mapbox.androidauto.screenmanager.MapboxScreenManager
import com.mapbox.androidauto.search.PlaceRecord
import com.mapbox.maps.CameraOptions
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineClearValue

internal class CarRoutePreviewScreen @UiThread constructor(
    private val mapboxCarContext: MapboxCarContext,
    private val placeRecord: PlaceRecord,
    private val navigationRoutes: List<NavigationRoute>,
) : Screen(mapboxCarContext.carContext) {

    private val carRoutesProvider = PreviewCarRoutesProvider(navigationRoutes)
    private var selectedIndex = 0
    private val carRouteLineRenderer = CarRouteLineRenderer(carRoutesProvider)
    private val carLocationRenderer = CarLocationRenderer()
    private val carSpeedLimitRenderer = CarSpeedLimitRenderer(mapboxCarContext)
    private val carNavigationCamera = CarNavigationCamera(
        initialCarCameraMode = CarCameraMode.OVERVIEW,
        alternativeCarCameraMode = CarCameraMode.FOLLOWING,
        carRoutesProvider = carRoutesProvider,
    )
    private val carMarkers = CarActiveGuidanceMarkers(carRoutesProvider)

    init {
        logAndroidAuto("CarRoutePreviewScreen constructor")
        addBackPressedHandler {
            logAndroidAuto("CarRoutePreviewScreen onBackPressed")
            // 뒤로가기 버튼을 눌러 FreeDrive 화면이 출력됐을 때 경로 미리보기 선을 지운다.
            MapboxNavigationApp.current()?.setNavigationRoutes(emptyList())
            mapboxCarContext.mapboxScreenManager.goBack()
        }
        lifecycle.muteAudioGuidance()
        lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onResume(owner: LifecycleOwner) {
                logAndroidAuto("CarRoutePreviewScreen onResume")
                mapboxCarContext.mapboxCarMap.registerObserver(carLocationRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carSpeedLimitRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carNavigationCamera)
                mapboxCarContext.mapboxCarMap.registerObserver(carRouteLineRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carMarkers)
            }

            override fun onPause(owner: LifecycleOwner) {
                logAndroidAuto("CarRoutePreviewScreen onPause")
                mapboxCarContext.mapboxCarMap.unregisterObserver(carLocationRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carSpeedLimitRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carNavigationCamera)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carRouteLineRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carMarkers)
            }
        })
    }

    @OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()
        navigationRoutes.forEach { navigationRoute ->
            val route = navigationRoute.directionsRoute
            val title = route.legs()?.first()?.summary() ?: placeRecord.name
            val routeSpannableString = SpannableString("  $title")
            val span = DurationSpan.create(route.duration().toLong())
            routeSpannableString.setSpan(span, 0, 1, 0)

            val distance = CarDistanceFormatter.formatDistance(route.distance())
            val item = Row.Builder().setTitle(routeSpannableString).addText(distance).build()
            listBuilder.addItem(item)
        }
        if (navigationRoutes.isNotEmpty()) {
            listBuilder.setSelectedIndex(selectedIndex)
            listBuilder.setOnSelectedListener { index ->
                val newRouteOrder = navigationRoutes.toMutableList()
                selectedIndex = index
                if (index > 0) {
                    val swap = newRouteOrder[0]
                    newRouteOrder[0] = newRouteOrder[index]
                    newRouteOrder[index] = swap
                    carRoutesProvider.updateRoutes(newRouteOrder)
                } else {
                    carRoutesProvider.updateRoutes(navigationRoutes)
                }
            }
        }

        return RoutePreviewNavigationTemplate.Builder()
            .setItemList(listBuilder.build())
            .setHeader(Header.Builder().setTitle("추천 경로").setStartHeaderAction(Action.BACK).build())
            .setActionStrip(
                mapboxCarContext.options.actionStripProvider
                    .getActionStrip(this, MapboxScreen.ROUTE_PREVIEW)
            )
            .setNavigateAction(
                Action.Builder()
                    .setTitle("한번 더 터치하여 안내 시작")
                    .setOnClickListener {
                        MapboxNavigationApp.current()!!.setNavigationRoutes(
                            carRoutesProvider.navigationRoutes.value
                        )
                        MapboxScreenManager.replaceTop(MapboxScreen.ACTIVE_GUIDANCE)
                    }
                    .build(),
            )
            .build()
    }
}