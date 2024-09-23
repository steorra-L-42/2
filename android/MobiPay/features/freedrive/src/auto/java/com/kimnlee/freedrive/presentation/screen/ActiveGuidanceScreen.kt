package com.kimnlee.freedrive.presentation.screen

import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.OnClickListener
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mapbox.androidauto.MapboxCarContext
import com.mapbox.androidauto.action.MapboxMapActionStrip
import com.mapbox.androidauto.internal.logAndroidAuto
import com.mapbox.androidauto.location.CarLocationRenderer
import com.mapbox.androidauto.navigation.CarActiveGuidanceMarkers
import com.mapbox.androidauto.navigation.CarArrivalTrigger
import com.mapbox.androidauto.navigation.CarCameraMode
import com.mapbox.androidauto.navigation.CarNavigationCamera
import com.mapbox.androidauto.navigation.audioguidance.CarAudioGuidanceAction
import com.mapbox.androidauto.navigation.roadlabel.CarRoadLabelRenderer
import com.mapbox.androidauto.navigation.speedlimit.CarSpeedLimitRenderer
import com.mapbox.androidauto.preview.CarRouteLineRenderer
import com.mapbox.androidauto.screenmanager.MapboxScreen
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.androidauto.R
import com.mapbox.androidauto.screenmanager.MapboxScreenManager

internal class ActiveGuidanceScreen constructor(
    private val mapboxCarContext: MapboxCarContext
) : Screen(mapboxCarContext.carContext) {

    val carRouteLineRenderer = CarRouteLineRenderer()
    val carLocationRenderer = CarLocationRenderer()
    val carSpeedLimitRenderer = CarSpeedLimitRenderer(mapboxCarContext)
    val carNavigationCamera = CarNavigationCamera(
        initialCarCameraMode = CarCameraMode.FOLLOWING,
        alternativeCarCameraMode = CarCameraMode.OVERVIEW,
    )
    private val carRoadLabelRenderer = CarRoadLabelRenderer()
    private val navigationInfoProvider = CarNavigationInfoProvider()
        .invalidateOnChange(this)
    private val carActiveGuidanceMarkers = CarActiveGuidanceMarkers()
    private val mapActionStripBuilder = MapboxMapActionStrip(this, carNavigationCamera)
    private val carArrivalTrigger = CarArrivalTrigger()

    init {
        logAndroidAuto("ActiveGuidanceScreen constructor")
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                logAndroidAuto("ActiveGuidanceScreen onResume")
                mapboxCarContext.mapboxCarMap.registerObserver(carLocationRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carRoadLabelRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carSpeedLimitRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carNavigationCamera)
                mapboxCarContext.mapboxCarMap.setGestureHandler(carNavigationCamera.gestureHandler)
                mapboxCarContext.mapboxCarMap.registerObserver(carRouteLineRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carActiveGuidanceMarkers)
                mapboxCarContext.mapboxCarMap.registerObserver(navigationInfoProvider)
                MapboxNavigationApp.registerObserver(carArrivalTrigger)
            }

            override fun onPause(owner: LifecycleOwner) {
                logAndroidAuto("ActiveGuidanceScreen onPause")
                mapboxCarContext.mapboxCarMap.unregisterObserver(carRoadLabelRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carLocationRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carSpeedLimitRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carNavigationCamera)
                mapboxCarContext.mapboxCarMap.setGestureHandler(null)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carRouteLineRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carActiveGuidanceMarkers)
                mapboxCarContext.mapboxCarMap.unregisterObserver(navigationInfoProvider)
                MapboxNavigationApp.unregisterObserver(carArrivalTrigger)
            }
        })
    }

    @OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
    override fun onGetTemplate(): Template {

        // ---------------- 커스텀 ActionStrip 코드들 ----------------
        val arrivalOnClickListener = OnClickListener {
            val carArrivalTrigger = MapboxNavigationApp.getObservers(CarArrivalTrigger::class)
                .firstOrNull()
            checkNotNull(carArrivalTrigger) {
                "The CarArrivalTrigger must be attached while in active guidance."
            }
            // 여기에 남은 거리 초기화 시켜주기
            MapboxNavigationApp.current()?.setNavigationRoutes(emptyList())
            navigationInfoProvider.resetNavigationInfo()
            // 경로 안내 취소 후 자유주행 화면으로 이동
            MapboxScreenManager.replaceTop(MapboxScreen.FREE_DRIVE)
        }

        val customActiveGuidanceActionStrip = ActionStrip.Builder()
            .addAction(CarAudioGuidanceAction().getAction(this))
            .addAction(
                Action.Builder()
                    .setTitle("안내중지")
                    .setOnClickListener(arrivalOnClickListener).build()
            )
            .build()
        // ---------------- 커스텀 ActionStrip 코드들 끝 ----------------

        logAndroidAuto("ActiveGuidanceScreen onGetTemplate")
        return NavigationTemplate.Builder()
            .setBackgroundColor(CarColor.PRIMARY)
            .setActionStrip(
                customActiveGuidanceActionStrip
            )
            .setMapActionStrip(mapActionStripBuilder.build())
            .apply { navigationInfoProvider.setNavigationInfo(this) }
            .build()
    }
}
