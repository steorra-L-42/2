package com.kimnlee.common.utils

import android.content.Context
import android.content.SharedPreferences

class AutoSaveParkingManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("parking_settings", Context.MODE_PRIVATE)
    private val locationPrefs: SharedPreferences = context.getSharedPreferences("last_location", Context.MODE_PRIVATE)

    var isAutoSaveParkingEnabled: Boolean
        get() = prefs.getBoolean("auto_save_parking", true)
        set(value) {
            prefs.edit().putBoolean("auto_save_parking", value).apply()
            if (!value) {
                clearLastLocation()
            }
        }

    // 마지막 위치 가져오기
    fun getLastLocation(): Pair<Double, Double>? {
        val lat = locationPrefs.getString("last_latitude", null)
        val lng = locationPrefs.getString("last_longitude", null)
        return if (lat != null && lng != null) {
            Pair(lat.toDouble(), lng.toDouble())
        } else {
            null
        }
    }

    // 마지막 위치 저장
    fun saveLastLocation(lat: Double, lng: Double) {
        locationPrefs.edit()
            .putString("last_latitude", lat.toString())
            .putString("last_longitude", lng.toString())
            .apply()
    }

    // 설정이 초기화 됐을때 위치 비우기
    private fun clearLastLocation() {
        locationPrefs.edit().clear().apply()
    }

    fun resetToDefaults() {
        prefs.edit().clear().apply()
        locationPrefs.edit().clear().apply()
        isAutoSaveParkingEnabled = true  // 기본값으로 설정
    }
}