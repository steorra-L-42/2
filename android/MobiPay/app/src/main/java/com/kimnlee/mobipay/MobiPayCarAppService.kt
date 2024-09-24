package com.kimnlee.mobipay

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import androidx.car.app.CarAppService
import androidx.car.app.validation.HostValidator
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kimnlee.freedrive.presentation.screen.MainCarSession

private const val TAG = "MobiPayCarAppService"
class MobiPayCarAppService : CarAppService() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }


    override fun onCreateSession() = MainCarSession()

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        getAndSaveLastLocation()
        Log.d(TAG, "안드로이드 오토 분리 감지")
    }


    private fun getAndSaveLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val lastLat = location.latitude
                    val lastLng = location.longitude

                    // Save the retrieved location in SharedPreferences
                    saveLastLocation(lastLat, lastLng)

                    Log.d(TAG, "마지막 위치를 저장합니다. Lat = $lastLat, Lng = $lastLng")
                } else {
                    Log.d(TAG, "마지막 위치가 null 입니다.")
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "마지막 위치를 가져올 수 없어 실패: ${it.message}")
            }
    }


    private fun saveLastLocation(lat: Double, lng: Double) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("last_location", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("last_latitude", lat.toString())
        editor.putString("last_longitude", lng.toString())
        editor.apply()
    }

    /*
     * 기존 Screen 메뉴를 활성화 하려면 주석 풀면 됨
    override fun onCreateSession(): Session {
        return object : Session() {
            override fun onCreateScreen(intent: Intent): Screen {
                return AutoHomeScreen(carContext)
            }
        }
    }
    */
}