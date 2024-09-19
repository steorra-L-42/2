package com.kimnlee.freedrive.presentation.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.util.Log
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.androidauto.MapboxCarContext
import com.mapbox.androidauto.deeplink.GeoDeeplinkNavigateAction
import com.mapbox.androidauto.map.MapboxCarMapLoader
import com.mapbox.androidauto.map.compass.CarCompassRenderer
import com.mapbox.androidauto.map.logo.CarLogoRenderer
import com.mapbox.androidauto.screenmanager.MapboxScreen
import com.mapbox.androidauto.screenmanager.MapboxScreenManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.androidauto.mapboxMapInstaller
import com.mapbox.maps.extension.style.image.ImageExtensionImpl
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.logE
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.core.lifecycle.requireMapboxNavigation
import com.mapbox.navigation.core.trip.session.TripSessionState
import com.kimnlee.freedrive.R
import kotlinx.coroutines.launch
import androidx.car.app.AppManager
import androidx.car.app.model.Action
import androidx.car.app.model.Alert
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarText
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.IconCompat

private const val TAG = "MainCarSession"
private val customStyleUrl = "mapbox://styles/harveyl/cm163kfgg019r01q1a4nc9hm3"


@OptIn(MapboxExperimental::class)
class MainCarSession : Session() {

    private val mapboxCarMapLoader = MapboxCarMapLoader()

    // installer 사용해서 MapboxCarMap에 lifecycle 등록
    // MapboxMap 화면은 여기서 커스텀 하면 되긴 한데..
    private val mapboxCarMap = mapboxMapInstaller()
        .onCreated(mapboxCarMapLoader)
        .onResumed(CarLogoRenderer(), CarCompassRenderer())
        .install()

    private val mapboxCarContext = MapboxCarContext(lifecycle, mapboxCarMap)
        .prepareScreens()

    // 커스텀 가능
    private val mapboxNavigation by requireMapboxNavigation()

    private val alertReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive: Broadcast Received")
            intent?.let {
                val title = it.getStringExtra("title") ?: "모비페이 결제요청"
                val subtitle = it.getStringExtra("content") ?: "가맹점\n00,000원"
                showPurchaseAlert(title, subtitle)
            }
        }
    }
    private fun showPurchaseAlert(title: String, subtitle: String) {
        carContext.getCarService(AppManager::class.java).showAlert(
            Alert.Builder(
                /*alertId*/ 1,
                /*title*/ CarText.create(title),
                /*durationMillis*/ 5000
            )
                .setIcon(
                    CarIcon.Builder(
                        IconCompat.createWithResource(carContext, R.drawable.ic_mobipay)
                    ).build()
                )
                .setSubtitle(CarText.create(subtitle))
                .addAction(
                    Action.Builder()
                        .setTitle("결제 승인")
                        .setOnClickListener {
                            // 결제승인 처리
                        }
                        .build()
                )
//                .addAction(
//                    Action.Builder()
//                        .setTitle("거절")
//                        .setOnClickListener {
//                            // 결제 거절 처리 근데 그냥 timeout으로 두자
//                        }
//                        .build()
//                )
                .build()
        )
    }

    private fun registerAlertReceiver() {
        // carContext 헷갈리면 getCarContext() 사용
        val intentFilter = IntentFilter("com.kimnlee.mobipay.SHOW_ALERT")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            carContext.registerReceiver(alertReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        }else{
            carContext.registerReceiver(alertReceiver, intentFilter)
        }
    }

    private fun unregisterReceiver() {
        carContext.unregisterReceiver(alertReceiver)
    }

    init {

        // 결제 알림 수행을 위한 Receiver 등록
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                registerAlertReceiver() // Register after the session has been created
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                unregisterReceiver()
            }
        })

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                checkLocationPermissions()
                observeAutoDrive()
            }
        })
    }

    fun getResizedBitmapFromVectorDrawable(context: Context, drawableId: Int, width: Int, height: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }


    override fun onCreateScreen(intent: Intent): Screen {

        mapboxCarMapLoader.setLightStyleOverride(style(customStyleUrl){

            // 핀 찍을 좌표 설정
            val pinLocation = Point.fromLngLat(128.42016424518943, 36.104488876950704) // Example coordinates

            // GeoJson으로 변환
            val geoJsonSource = GeoJsonSource.Builder("pin-source")
                .feature(Feature.fromGeometry(pinLocation))
                .build()

            // Unary 연산자(+)로 GeoJson 등록
            +geoJsonSource

            // 핀 아이콘 설정
            val baseWidth = 50
            val baseHeight = 50
            val pinBitmap = getResizedBitmapFromVectorDrawable(carContext, R.drawable.ic_mobipay, baseWidth, baseHeight)

            // 변환 성공일 시
            if (pinBitmap != null) {

                // 지도 style로 핀 이미지 등록
                +ImageExtensionImpl(ImageExtensionImpl.Builder("mobi-pin-icon").bitmap(pinBitmap))

                // 실제로 지도에 핀이 들어갈 layer 작성
                val symbolLayer = SymbolLayer("pin-layer", "pin-source")
                    .iconImage("mobi-pin-icon")
                    .iconAllowOverlap(true)
                    .iconIgnorePlacement(true)
                    .textField("맥도날드 구미 인동점") // 가맹점 명
                    .textSize(12.0)
                    .textColor("black")
                    .iconAnchor(IconAnchor.BOTTOM)
                    .textAnchor(TextAnchor.TOP)

                // Unary 연산자(+)로 symbolLayer를 지도에 추가
                +symbolLayer
            } else {
                logE(TAG, "비트맵 변환 실패!!")
            }

        })

        val screenKey = MapboxScreenManager.current()?.key
        Log.d(TAG, "onCreateScreen: 키: ${screenKey}")
        checkNotNull(screenKey) { "The screen key should be set before the Screen is requested." }

        return mapboxCarContext.mapboxScreenManager.createScreen(screenKey)
    }

    override fun onCarConfigurationChanged(newConfiguration: Configuration) {
        mapboxCarMapLoader.onCarConfigurationChanged(carContext)
    }

    @OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (PermissionsManager.areLocationPermissionsGranted(carContext)) {
            GeoDeeplinkNavigateAction(mapboxCarContext).onNewIntent(intent)
        }
    }

    // 위치 권한 없으면 위치 권한 요청 스크린으로 이동
    private fun checkLocationPermissions() {
        PermissionsManager.areLocationPermissionsGranted(carContext).also { isGranted ->
            val currentKey = MapboxScreenManager.current()?.key
            if (!isGranted) {
                MapboxScreenManager.replaceTop(MapboxScreen.NEEDS_LOCATION_PERMISSION)
            } else if (currentKey == null || currentKey == MapboxScreen.NEEDS_LOCATION_PERMISSION) {
                MapboxScreenManager.replaceTop(MapboxScreen.FREE_DRIVE)
            }
        }
    }

    private fun observeAutoDrive() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapboxCarContext.mapboxNavigationManager.autoDriveEnabledFlow.collect {
                    refreshTripSession()
                }
            }
        }
    }

    private fun refreshTripSession() {
        val isAutoDriveEnabled = mapboxCarContext.mapboxNavigationManager
            .autoDriveEnabledFlow.value
        if (!PermissionsManager.areLocationPermissionsGranted(carContext)) {
            mapboxNavigation.stopTripSession()
            return
        }

        if (!isAutoDriveEnabled) {
            if (mapboxNavigation.getTripSessionState() != TripSessionState.STARTED) {
                if (ActivityCompat.checkSelfPermission(carContext,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(carContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    mapboxNavigation.startTripSession()
                }
            }
        }
    }
}