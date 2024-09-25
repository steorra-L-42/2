package com.kimnlee.payment.data.api

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.kimnlee.api.network.ApiClient
import com.kimnlee.payment.data.model.Photos
import retrofit2.http.GET

interface PaymentApiService {
    @GET("photos")
    suspend fun getPhotos(): List<Photos>

    companion object {
        val instance: PaymentApiService by lazy {
            ApiClient.retrofit.create(PaymentApiService::class.java)
        }
    }
}