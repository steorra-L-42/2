package com.kimnlee.freedrive.presentation.screen

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
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
import androidx.car.app.AppManager
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.model.Action
import androidx.car.app.model.Alert
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarText
import androidx.car.app.model.OnClickListener
import androidx.car.app.notification.CarAppExtender
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kimnlee.common.FCMData
import com.kimnlee.common.utils.moneyFormat
import com.kimnlee.freedrive.R
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.androidauto.MapboxCarContext
import com.mapbox.androidauto.deeplink.GeoDeeplinkNavigateAction
import com.mapbox.androidauto.map.MapboxCarMapLoader
import com.mapbox.androidauto.map.compass.CarCompassRenderer
import com.mapbox.androidauto.map.logo.CarLogoRenderer
import com.mapbox.androidauto.screenmanager.MapboxScreen
import com.mapbox.androidauto.screenmanager.MapboxScreenManager
import com.mapbox.androidauto.search.PlaceRecord
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
import kotlinx.coroutines.launch
import java.math.BigInteger

private const val TAG = "MainCarSession"
private val customStyleUrl = "mapbox://styles/harveyl/cm163kfgg019r01q1a4nc9hm3"


@OptIn(MapboxExperimental::class)
class MainCarSession : Session() {

    companion object {
        // 가맹점 목록 데이터 정의
        val merchantList = listOf(
            PlaceRecord(
                id = "1",
                name = "맥도날드 구미 인동점",
                coordinate = Point.fromLngLat(128.42016424518943, 36.104488876950704),
                description = "경상북도 구미시 인동가산로 20",
                categories = listOf("음식점", "패스트푸드")
            ),
            PlaceRecord(
                id = "2",
                name = "스타벅스 구미인동DT점",
                coordinate = Point.fromLngLat(128.41955, 36.10396),
                description = "경상북도 구미시 인동가산로 14",
                categories = listOf("카페", "음료")
            ),
            PlaceRecord(
                id = "3",
                name = "구미역사주차장",
                coordinate = Point.fromLngLat(128.33131, 36.12825),
                description = "경상북도 구미시 구미중앙로 45",
                categories = listOf("교통", "기차역")
            )
        )
    }

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
//            Log.d(TAG, "onReceive: Boroadcast 호출 됨")
            val action = intent?.action ?: ""
            val type = intent?.getStringExtra("type") ?: ""
            val title = intent?.getStringExtra("title") ?: ""
            val body = intent?.getStringExtra("body") ?: ""
//            Log.d(TAG, "onReceive: 타입은 $type")

            if (action == "com.kimnlee.mobipay.SHOW_PLAIN_ALERT"){
                Log.d(TAG, "onReceive: $action")
                showAlert(title, body, null)
            }else if (action == "com.kimnlee.mobipay.SHOW_PLAIN_HUN"){
                Log.d(TAG, "onReceive: $action")
                showHUN(title, body, null)
            }

            (intent?.getSerializableExtra("fcmData") as? FCMData)?.let { fcmData ->
                Log.d(TAG, "onReceive: FCM데이터 $fcmData")
                if(action !== "" && type !== ""){
                    if (action == "com.kimnlee.mobipay.SHOW_ALERT"){
                        Log.d(TAG, "onReceive: $action")
                        processAlert(type, fcmData)
                    }else if (action == "com.kimnlee.mobipay.SHOW_HUN"){
                        Log.d(TAG, "onReceive: $action")
                        processHUN(type, fcmData)
                    }
                }
            }
        }
    }

    private fun processAlert(intentType: String, fcmData: FCMData){
        Log.d(TAG, "processAlert: Alert 처리")
        val amount = fcmData.paymentBalance?.let { BigInteger(it) }

        var title = "관리자에게 문의하세요"
        var onClickListener : OnClickListener? = null

        if(intentType == "manual_pay"){
            title = "모비페이 결제요청"
            onClickListener = OnClickListener {
                // 결제 처리
                val paymentApprovalIntent = Intent("com.kimnlee.mobipay.PAYMENT_APPROVAL").apply {
                    putExtra("fcmData", fcmData)
                }
                Log.d(TAG, "processAlert: 승인 intent 날림")
                carContext.sendBroadcast(paymentApprovalIntent)
            }
        }else if(intentType == "manual_pay_complete"){
            title = "모비페이 결제완료"
        }else if(intentType == "auto_pay_complete"){
            title = "모비페이 결제완료"
        }
        val body = fcmData.merchantName+ "\n" + amount?.let { moneyFormat(it) }
        Log.d(TAG, "processAlert: 자동결제 바디 $body")

        showAlert(title, body, onClickListener)
    }


    private fun processHUN(intentType: String, fcmData: FCMData){
        Log.d(TAG, "processHUN: HUN 처리")
        val amount = fcmData.paymentBalance?.let { BigInteger(it) }

        var title = "관리자에게 문의하세요"
//        var isRequest = false


        var paymentApprovalIntent: Intent? = null

        if(intentType == "manual_pay"){
            title = "모비페이 결제요청"
//            isRequest = true
            paymentApprovalIntent = Intent("com.kimnlee.mobipay.PAYMENT_APPROVAL").apply {
                putExtra("fcmData", fcmData)
            }

        }else if(intentType == "manual_pay_complete"){
            title = "모비페이 결제완료"
        }else if(intentType == "auto_pay_complete"){
            title = "모비페이 자동결제 완료"
        }
        val body = fcmData.merchantName+ "   " + amount?.let { moneyFormat(it) }

        showHUN(title, body, paymentApprovalIntent)
    }

    fun showHUN(title: String, content: String, paymentApprovalIntent: Intent?) {
        val notificationId = 1001
        val channelId = "payment_request"

        var paymentApprovalPendingIntent : PendingIntent? = null

        if(paymentApprovalIntent !== null){
            paymentApprovalPendingIntent = PendingIntent.getBroadcast(
                carContext,
                0,
                paymentApprovalIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val notificationBuilder = NotificationCompat.Builder(carContext, channelId)
            .setSmallIcon(R.drawable.ic_mobipay)
            .setContentTitle(title)
            .setContentText(content)
            .extend(
                CarAppExtender.Builder()
                    .setImportance(NotificationManager.IMPORTANCE_HIGH)
                    .build()
            )

        if (paymentApprovalIntent !== null) {
            notificationBuilder
                .setTimeoutAfter(10000)
                .addAction(
                    NotificationCompat.Action.Builder(
                        null,
                        "승인",
                        paymentApprovalPendingIntent
                    ).build()
                )
        } else {
            notificationBuilder.setTimeoutAfter(7000)
        }

        NotificationManagerCompat.from(carContext).notify(notificationId, notificationBuilder.build())
    }

    // 안 되면 롤백
    private fun showAlert(title: String, subtitle: String, onClickListener: OnClickListener?) {
        var duration = 10000L
        if(onClickListener == null)
            duration = 8000L

        val alertBuilder = Alert.Builder(
            kotlin.random.Random.nextInt(),
            CarText.create(title),
            duration // 12초
        )
            .setIcon(
                CarIcon.Builder(
                    IconCompat.createWithResource(carContext, R.drawable.ic_mobipay)
                ).build()
            )
            .setSubtitle(CarText.create(subtitle))

        // onClickListener가 null이 아닌 경우에만 버튼 표시
        onClickListener?.let {
            val actionBuilder = Action.Builder()
                .setTitle("결제 승인")
                .setOnClickListener(it)

            alertBuilder.addAction(actionBuilder.build())
        }

        carContext.getCarService(AppManager::class.java).showAlert(alertBuilder.build())
    }

    private fun registerAlertReceiver() {
        // carContext 헷갈리면 getCarContext() 사용
        val intentFilter = IntentFilter().apply {
            addAction("com.kimnlee.mobipay.SHOW_ALERT")
            addAction("com.kimnlee.mobipay.SHOW_HUN")
            addAction("com.kimnlee.mobipay.SHOW_PLAIN_ALERT")
            addAction("com.kimnlee.mobipay.SHOW_PLAIN_HUN")
        }
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
                registerAlertReceiver()
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

            // 핀 아이콘 설정
            val baseWidth = 50
            val baseHeight = 50
            val pinBitmap = getResizedBitmapFromVectorDrawable(carContext, R.drawable.ic_mobipay, baseWidth, baseHeight)

            // 변환 성공일 시
            if (pinBitmap != null) {

                // 가맹점 마커 추가
                merchantList.forEach { merchant ->
                    val geoJsonSource = GeoJsonSource.Builder("${merchant.id}-source")
                        .feature(Feature.fromGeometry(merchant.coordinate))
                        .build()

                    +geoJsonSource
                    +ImageExtensionImpl(ImageExtensionImpl.Builder("mobi-pin-icon").bitmap(pinBitmap))

                    val symbolLayer = SymbolLayer("${merchant.id}-layer", "${merchant.id}-source")
                        .iconImage("mobi-pin-icon")
                        .iconAllowOverlap(true)
                        .iconIgnorePlacement(true)
                        .textField(merchant.name)
                        .textSize(12.0)
                        .textColor("black")
                        .iconAnchor(IconAnchor.BOTTOM)
                        .textAnchor(TextAnchor.TOP)

                    +symbolLayer
                }
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
