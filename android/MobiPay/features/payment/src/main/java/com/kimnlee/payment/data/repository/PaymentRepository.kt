package com.kimnlee.payment.data.repository

import android.util.Log
import com.kimnlee.common.PaymentOperations
import com.kimnlee.payment.data.api.PaymentApiService
import com.kimnlee.payment.data.model.Photos

private const val TAG = "PaymentRepository"
class PaymentRepository(private val authenticatedApi: PaymentApiService) : PaymentOperations {

    suspend fun getPhotos(): List<Photos> {
        return authenticatedApi.getPhotos().filter { photo -> photo.id <= 5 }
    }

    fun verifyGPS(){

    }

    override fun processFCM(lat: String, lng: String){
        Log.d(TAG, "processFCM: 호출되었습니다. @@@@@##### ${lat} / ${lng}")
    }

}