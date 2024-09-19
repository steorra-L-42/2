package com.kimnlee.freedrive.presentation.screen

import androidx.annotation.UiThread
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.kimnlee.freedrive.R
import com.mapbox.androidauto.MapboxCarContext
import com.mapbox.androidauto.action.MapboxMapActionStrip
import com.mapbox.androidauto.internal.logAndroidAuto
import com.mapbox.androidauto.location.CarLocationRenderer
import com.mapbox.androidauto.navigation.CarCameraMode
import com.mapbox.androidauto.navigation.CarNavigationCamera
import com.mapbox.androidauto.navigation.roadlabel.CarRoadLabelRenderer
import com.mapbox.androidauto.navigation.speedlimit.CarSpeedLimitRenderer
import com.mapbox.androidauto.preview.CarRouteLineRenderer
import com.mapbox.androidauto.screenmanager.MapboxScreen
import com.mapbox.androidauto.screenmanager.MapboxScreenManager
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI

/**
 * LSC
 * When the app is launched from Android Auto
 */
internal class FreeDriveCarScreen @UiThread constructor(
    private val mapboxCarContext: MapboxCarContext
) : Screen(mapboxCarContext.carContext) {

    val carRouteLineRenderer = CarRouteLineRenderer()
    val carLocationRenderer = CarLocationRenderer()
    val carSpeedLimitRenderer = CarSpeedLimitRenderer(mapboxCarContext)
    val carNavigationCamera = CarNavigationCamera(
        initialCarCameraMode = CarCameraMode.FOLLOWING,
        alternativeCarCameraMode = null,
    )
    private val carRoadLabelRenderer = CarRoadLabelRenderer()
    private val mapActionStripBuilder = MapboxMapActionStrip(this, carNavigationCamera)

    init {
        logAndroidAuto("FreeDriveCarScreen constructor")
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                logAndroidAuto("FreeDriveCarScreen onResume")
                mapboxCarContext.mapboxCarMap.registerObserver(carRouteLineRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carLocationRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carRoadLabelRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carSpeedLimitRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carNavigationCamera)
                mapboxCarContext.mapboxCarMap.setGestureHandler(carNavigationCamera.gestureHandler)
            }

            override fun onPause(owner: LifecycleOwner) {
                logAndroidAuto("FreeDriveCarScreen onPause")
                mapboxCarContext.mapboxCarMap.unregisterObserver(carRouteLineRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carLocationRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carRoadLabelRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carSpeedLimitRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carNavigationCamera)
                mapboxCarContext.mapboxCarMap.setGestureHandler(null)
            }
        })
    }

    @OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
    override fun onGetTemplate(): Template {

        val actionStrip1 =
            mapboxCarContext.options.actionStripProvider
                .getActionStrip(this, MapboxScreen.FREE_DRIVE)

        fun customBuildSettingsAction() = Action.Builder()
            .setIcon(
                CarIcon.Builder(
                    IconCompat.createWithResource(
                        carContext,
                        R.drawable.ic_settings
                    )
                ).build()
            )
            .setOnClickListener {
                MapboxScreenManager.push(MapboxScreen.SETTINGS)
            }
            .build()

        fun customBuildSearchAction(): Action = Action.Builder()
            .setIcon(
                CarIcon.Builder(
                    IconCompat.createWithResource(
                        carContext,
                        R.drawable.ic_search_black36dp
                    )
                ).build()
            )
            .setOnClickListener {
                MapboxScreenManager.push(MapboxScreen.SEARCH)
            }
            .build()

        fun customBuildFavoritesAction(): Action = Action.Builder()
            .setTitle("가맹점 전체보기")
            .setOnClickListener {
                MapboxScreenManager.push(MapboxScreen.FAVORITES)
            }
            .build()

        val customFreeDriveActionStrip = ActionStrip.Builder()
            .addAction(customBuildSettingsAction())
            .addAction(customBuildSearchAction())
            .addAction(customBuildFavoritesAction())
            .build()

//        actionStrip1
//            .actions
//            .add(
//                Action.Builder()
//                    .setTitle("근처 모비페이 가맹점: 맥도날드 구미 인동점")
//                    .setOnClickListener {
//                        // Action for Location 1 (e.g., zoom to a point on the map)
////          carCameraController.zoomTo(Point.fromLngLat(37.7749, -122.4194)) // Example: San Francisco
//                    }
//                    .build()
//            )


        return NavigationTemplate.Builder()
            .setBackgroundColor(CarColor.PRIMARY)
            .setActionStrip(
                customFreeDriveActionStrip
            )
            .setMapActionStrip(mapActionStripBuilder.build())
            .build()
    }
}
