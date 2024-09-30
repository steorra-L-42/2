package com.kimnlee.payment.data.repository

import android.util.Log
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.payment.data.api.PaymentApiService
import com.kimnlee.payment.data.model.Photos

private const val TAG = "PaymentRepository"
class PaymentRepository(authManager: AuthManager) {
    private val apiService = PaymentApiService.create(authManager)

    suspend fun getPhotos(): List<Photos> {
        return apiService.getPhotos().filter { photo -> photo.id <= 5 }
    }

    fun verifyGPS(){

    }

    fun processFCM(lat: String, lng: String){
        Log.d(TAG, "processFCM: 호출되었습니다. @@@@@##### ${lat} / ${lng}")
    }

}