package com.kimnlee.payment.data.api

import com.kimnlee.payment.data.model.Photos
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PaymentApiService {
    @GET("photos")
    suspend fun getPhotos(): List<Photos>

    @POST("")
    suspend fun approvalPaymentRequest()

    @POST("/api/v1/postpayments/approval")
    fun approvePaymentRequest(@Body token: String): Call<Void>

}