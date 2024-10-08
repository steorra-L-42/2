package com.kimnlee.freedrive.presentation.screen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.UiThread
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.kimnlee.freedrive.R
import com.kimnlee.common.utils.AAFocusManager
import com.kimnlee.freedrive.data.WebRTCManager
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

internal class FreeDriveCarScreen @UiThread constructor(
    private val mapboxCarContext: MapboxCarContext
) : Screen(mapboxCarContext.carContext), DefaultLifecycleObserver {

    val carRouteLineRenderer = CarRouteLineRenderer()
    val carLocationRenderer = CarLocationRenderer()
    val carSpeedLimitRenderer = CarSpeedLimitRenderer(mapboxCarContext)
    val carNavigationCamera = CarNavigationCamera(
        initialCarCameraMode = CarCameraMode.FOLLOWING,
        alternativeCarCameraMode = null,
    )
    private val carRoadLabelRenderer = CarRoadLabelRenderer()
    private val mapActionStripBuilder = MapboxMapActionStrip(this, carNavigationCamera)

    // fcm 관련 추가
    private var fcmContent: String? = null
    private var showFcmContent = false
    private var roomId: String? = null
    private var isCallStarted = false
    private lateinit var uiUpdateReceiver: BroadcastReceiver
    private lateinit var webRTCManager: WebRTCManager

    init {
        logAndroidAuto("FreeDriveCarScreen constructor")

        lifecycle.addObserver(this)

        uiUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("uiUpdateReceiver", "Broadcast received: ${intent?.getStringExtra("new_text")}")
                intent?.getStringExtra("new_text")?.let { newText ->
                    fcmContent = newText
                    showFcmContent = true
                    roomId = intent.getStringExtra("room_id") ?: "1"

                    if (!this@FreeDriveCarScreen::webRTCManager.isInitialized) {
                        webRTCManager = WebRTCManager(carContext)
                        webRTCManager.joinRoom(roomId!!)
                    }

                    refreshScreen()
                }
            }
        }

        LocalBroadcastManager.getInstance(carContext).registerReceiver(
            uiUpdateReceiver,
            IntentFilter("com.kimnlee.testmsg.UPDATE_UI")
        )

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                logAndroidAuto("FreeDriveCarScreen onResume")
                AAFocusManager.screenResumed()
                mapboxCarContext.mapboxCarMap.registerObserver(carRouteLineRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carLocationRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carRoadLabelRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carSpeedLimitRenderer)
                mapboxCarContext.mapboxCarMap.registerObserver(carNavigationCamera)
                mapboxCarContext.mapboxCarMap.setGestureHandler(carNavigationCamera.gestureHandler)
            }

            override fun onPause(owner: LifecycleOwner) {
                logAndroidAuto("FreeDriveCarScreen onPause")
                AAFocusManager.screenPaused()
                mapboxCarContext.mapboxCarMap.unregisterObserver(carRouteLineRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carLocationRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carRoadLabelRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carSpeedLimitRenderer)
                mapboxCarContext.mapboxCarMap.unregisterObserver(carNavigationCamera)
                mapboxCarContext.mapboxCarMap.setGestureHandler(null)
            }
        })
    }

    override fun onGetTemplate(): Template {
        return if (showFcmContent) {
            getFcmContentTemplate()
        } else {
            getNavigationTemplate()
        }
    }

    private fun getNavigationTemplate(): Template {
        val customFreeDriveActionStrip = ActionStrip.Builder()
            .addAction(customBuildSearchAction())
            .addAction(customBuildFavoritesAction())
            .build()

        return NavigationTemplate.Builder()
            .setBackgroundColor(CarColor.PRIMARY)
            .setActionStrip(customFreeDriveActionStrip)
            .setMapActionStrip(mapActionStripBuilder.build())
            .build()
    }

    private fun getFcmContentTemplate(): Template {
        val rows = fcmContent?.split("\n")?.map { menuItem ->
            Row.Builder().setTitle(menuItem).build()
        } ?: listOf()

        val paneBuilder = Pane.Builder()
        rows.forEach { paneBuilder.addRow(it) }

        // Add the call button
        val callButton = Action.Builder()
            .setTitle(if (isCallStarted) "End Call" else "Start Call")
            .setOnClickListener {
                if (!isCallStarted) {
                    webRTCManager.startCall()
                    isCallStarted = true
                } else {
                    webRTCManager.hangup()
                    isCallStarted = false
                }
                invalidate()
            }
            .build()

        paneBuilder.addAction(callButton)

        // 뒤로가기 버튼
        val customBackAction = Action.Builder()
            .setTitle("뒤로 가기")
            .setOnClickListener {
                if (isCallStarted) {
                    webRTCManager.hangup()
                    isCallStarted = false
                }
                showFcmContent = false
                invalidate()
            }
            .build()

        paneBuilder.addAction(customBackAction)

        return PaneTemplate.Builder(paneBuilder.build())
            .build()
    }

    private fun customBuildSearchAction(): Action = Action.Builder()
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

    private fun customBuildFavoritesAction(): Action = Action.Builder()
        .setTitle("주변가맹점 보기")
        .setOnClickListener {
            MapboxScreenManager.push(MapboxScreen.FAVORITES)
        }
        .build()

    private fun refreshScreen() {
        Handler(Looper.getMainLooper()).post {
            invalidate()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        LocalBroadcastManager.getInstance(carContext).unregisterReceiver(uiUpdateReceiver)
        if (this::webRTCManager.isInitialized && isCallStarted) {
            webRTCManager.hangup()
        }
    }
}
