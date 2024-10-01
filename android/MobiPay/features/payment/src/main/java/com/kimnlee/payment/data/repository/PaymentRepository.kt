package com.kimnlee.payment.data.repository

import android.content.Context
import android.util.Log
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.kimnlee.common.PaymentOperations
import com.kimnlee.payment.data.api.PaymentApiService
import com.kimnlee.payment.data.model.Photos

private const val TAG = "PaymentRepository"
class PaymentRepository(
    private val authenticatedApi: PaymentApiService,
    private val context: Context
) : PaymentOperations {

    private var currentLocation: LatLng? = null
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun getPhotos(): List<Photos> {
        return authenticatedApi.getPhotos().filter { photo -> photo.id <= 5 }
    }

    fun getCurrentLocation(onLocationReceived: (LatLng?) -> Unit) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = LatLng(location.latitude, location.longitude)
                    onLocationReceived(currentLocation)
                } else {
                    onLocationReceived(null)
                    Log.d(TAG, "현재 위치 NULL")
                }
            }.addOnFailureListener {
                Log.e(TAG, "현재 위치 가져오기 실패", it)
                onLocationReceived(null)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "권한 없음", e)
            onLocationReceived(null)
        }
    }

    override fun verifyGPS(latlng: LatLng): Boolean {
        currentLocation?.let { currentLatLng ->
            val distance = FloatArray(1)
            Location.distanceBetween(
                currentLatLng.latitude,
                currentLatLng.longitude,
                latlng.latitude,
                latlng.longitude,
                distance
            )
            return distance[0] <= 100
        }
        return false
    }

    override fun processFCM(lat: String, lng: String) {
        Log.d(TAG, "processFCM: 호출됨1")
        val latLng = LatLng(lat.toDouble(), lng.toDouble())
        Log.d(TAG, "processFCM: 호출됨2")
        getCurrentLocation { currentLocation ->
            if (currentLocation != null) {
                if (verifyGPS(latLng))
                    Log.d(TAG, "processFCM: 성공 100M 이내에 있음")
                else
                    Log.d(TAG, "processFCM: 실패 100M 밖에 있음")
            } else {
                Log.d(TAG, "processFCM: 현재 위치를 가져오지 못했습니다.")
            }
        }
    }

}