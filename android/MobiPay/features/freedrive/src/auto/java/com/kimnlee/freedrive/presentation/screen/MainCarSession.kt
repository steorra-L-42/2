package com.mobi.testnavi.navi.car

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.mapbox.bindgen.Value
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.maps.MAPBOX_ACCESS_TOKEN_RESOURCE_NAME
//import com.mapbox.androidauto.screenmanager.prepareScreens
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.ResourceOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.androidauto.mapboxMapInstaller
import com.mapbox.maps.extension.style.StyleContract
import com.mapbox.maps.extension.style.StyleExtensionImpl
import com.mapbox.maps.extension.style.image.ImageExtensionImpl
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.extension.style.light.generated.light
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.ImageSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.generated.imageSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.logE
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.lifecycle.requireMapboxNavigation
import com.mapbox.navigation.core.trip.session.TripSessionState
import com.mobi.testnavi.R
import com.mobi.testnavi.navi.CarAppSyncComponent
import com.mobi.testnavi.navi.ReplayRouteTripSession
import kotlinx.coroutines.launch
import androidx.car.app.AppManager
import androidx.car.app.model.Action
import androidx.car.app.model.Alert
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarText
import androidx.core.graphics.drawable.IconCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

private const val TAG = "MainCarSession"
//private val customStyleUrl = "mapbox://styles/harveyl/cm13cb05003n201pqdyqn4was"
private val customStyleUrl = "mapbox://styles/harveyl/cm163kfgg019r01q1a4nc9hm3"


@OptIn(MapboxExperimental::class)
class MainCarSession : Session() {

    // The MapboxCarMapLoader will automatically load the map with night and day styles.
//    private val mapboxCarMapLoader = MapboxCarMapLoader()
    private val mapboxCarMapLoader = MapboxCarMapLoader()
//        .setLightStyleOverride(style(customStyleUrl){})
//        .setDarkStyleOverride(style(customStyleUrl){})


    // Use the mapboxMapInstaller for installing the Session lifecycle to a MapboxCarMap.
    // Customizations that you want to be part of any Screen with a Mapbox Map can be done here.
    private val mapboxCarMap = mapboxMapInstaller()
        .onCreated(mapboxCarMapLoader)
        .onResumed(CarLogoRenderer(), CarCompassRenderer())
        .install()

    // Prepare an AndroidAuto experience with MapboxCarContext.
    private val mapboxCarContext = MapboxCarContext(lifecycle, mapboxCarMap)
        .prepareScreens()

//    private val mapboxCarContext = MapboxCarContext(lifecycle, mapboxCarMap)
//    private val mapboxCarContext = MapboxCarContext(lifecycle, mapboxCarMap).mapboxScreenManager.createScreen("1")

    // Many operations and customizations are available through MapboxNavigation.
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
                            // Handle approve action
                        }
                        .build()
                )
//                .addAction(
//                    Action.Builder()
//                        .setTitle("거절")
//                        .setOnClickListener {
//                            // Handle reject action
//                        }
//                        .build()
//                )
                .build()
        )
    }

    private fun registerAlertReceiver() {
        // Use getCarContext() instead of carContext to ensure you are using the proper context
        val intentFilter = IntentFilter("com.mobi.testnavi.SHOW_ALERT")
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
        // Decide how you want the car and app to interact. In this example, the car and app
        // are kept in sync where they essentially mirror each other.

//        mapboxCarMap.carMapSurface?.mapSurface?.getMapboxMap()?.loadStyle(style(customStyleUrl){})
        // Register the BroadcastReceiver during session lifecycle initialization
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

//        val intentFilter = IntentFilter("com.mobi.testnavi.SHOW_ALERT")

        CarAppSyncComponent.getInstance().setCarSession(this)

        // Add BitmapWidgets to the map that will be shown whenever the map is visible.
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
    // This logic is for you to decide. In this example the MapboxScreenManager.replaceTop is
    // declared in other logical places. At this point the screen key should be already set.
    override fun onCreateScreen(intent: Intent): Screen {

//      mapboxCarMap.carMapSurface?.mapSurface?.getMapboxMap()?.loadStyle(style(customStyleUrl){})
//        mapboxCarMapLoader.setLightStyleOverride(style(customStyleUrl){})

        mapboxCarMapLoader.setLightStyleOverride(style(customStyleUrl){

            // Define the point for the pin location
            val pinLocation = Point.fromLngLat(128.42016424518943, 36.104488876950704) // Example coordinates

            // Add the GeoJsonSource for the pin
            val geoJsonSource = GeoJsonSource.Builder("pin-source")
                .feature(Feature.fromGeometry(pinLocation))
                .build()

            // Add the source using the unary operator
            +geoJsonSource

            // Create a Bitmap for the pin icon (resize as needed)
            val baseWidth = 50
            val baseHeight = 50
            val pinBitmap = getResizedBitmapFromVectorDrawable(carContext, R.drawable.ic_mobipay, baseWidth, baseHeight)

            // Check if bitmap creation was successful
            if (pinBitmap != null) {
                // Add the pin icon image to the style
                +ImageExtensionImpl(ImageExtensionImpl.Builder("my-pin-icon").bitmap(pinBitmap))
//                +ImageExtensionImpl.Builder("my-pin-icon").bitmap(pinBitmap)

                // Create a SymbolLayer for the pin with icon and text
                val symbolLayer = SymbolLayer("pin-layer", "pin-source")
                    .iconImage("my-pin-icon")
                    .iconAllowOverlap(true)
                    .iconIgnorePlacement(true)
                    .textField("맥도날드 구미 인동점") // Add your label text
                    .textSize(12.0)
                    .textColor("black")
                    .iconAnchor(IconAnchor.BOTTOM)
                    .textAnchor(TextAnchor.TOP)

                // Add the SymbolLayer using the unary operator
                +symbolLayer
            } else {
                logE(TAG, "Failed to decode vector drawable to bitmap.")
            }

        })


//        carContext.getCarService(AppManager::class.java).showAlert(
//            Alert.Builder(
//                /*alertId*/ 1,
//                /*title*/ CarText.create("모비페이 결제요청"),
//                /*durationMillis*/ 5000
//            )
//                // The fields below are optional
////                .addAction(firstAction)
////                .addAction(secondAction)
//                .addAction(
//                    Action.Builder()
//                        .setTitle("승인")
//                        .setOnClickListener {
//                        }
//                        .build()
//                )
//                .addAction(
//                    Action.Builder()
//                        .setTitle("거절")
//                        .setOnClickListener {
//                        }
//                        .build()
//                )
//                .setSubtitle(CarText.create("맥도날드 구미 인동점\n13,000원"))
//            .setIcon(CarIcon.APP_ICON)
////            .setCallback(...)
//        .build()
//        )

        val screenKey = MapboxScreenManager.current()?.key
        Log.d(TAG, "onCreateScreen: 키: ${screenKey}")
        checkNotNull(screenKey) { "The screen key should be set before the Screen is requested." }

        return mapboxCarContext.mapboxScreenManager.createScreen(screenKey)
//        return MyScreen(carContext)
    }

    // Forward the CarContext to the MapboxCarMapLoader with the configuration changes.
    override fun onCarConfigurationChanged(newConfiguration: Configuration) {
        mapboxCarMapLoader.onCarConfigurationChanged(carContext)
    }

    // Handle the geo deeplink for voice activated navigation. This will handle the case when
    // you ask the head unit to "Navigate to coffee shop".
    @OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (PermissionsManager.areLocationPermissionsGranted(carContext)) {
            GeoDeeplinkNavigateAction(mapboxCarContext).onNewIntent(intent)
        }
    }

    // Location permissions are required for this example. Check the state and replace the current
    // screen if there is not one already set.
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

    // Enable auto drive. Open the app on the head unit and then execute the following from your
    // computer terminal.
    // adb shell dumpsys activity service com.mapbox.navigation.examples.androidauto.car.MainCarAppService AUTO_DRIVE
    private fun observeAutoDrive() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapboxCarContext.mapboxNavigationManager.autoDriveEnabledFlow.collect {
                    refreshTripSession()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun refreshTripSession() {
        val isAutoDriveEnabled = mapboxCarContext.mapboxNavigationManager
            .autoDriveEnabledFlow.value
        if (!PermissionsManager.areLocationPermissionsGranted(carContext)) {
            mapboxNavigation.stopTripSession()
            return
        }

        if (isAutoDriveEnabled) {
            MapboxNavigationApp.registerObserver(ReplayRouteTripSession)
        } else {
            MapboxNavigationApp.unregisterObserver(ReplayRouteTripSession)
            if (mapboxNavigation.getTripSessionState() != TripSessionState.STARTED) {
                mapboxNavigation.startTripSession()
            }
        }
    }
}
