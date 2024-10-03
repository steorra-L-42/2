package com.kimnlee.payment.data.api

import com.kimnlee.payment.data.model.PaymentApprovalData
import com.kimnlee.payment.data.model.Photos
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface PaymentApiService {
    @GET("photos")
    suspend fun getPhotos(): List<Photos>

    @POST("")
    suspend fun approvalPaymentRequest()

    @Headers("Content-Type: application/json")
    @POST("/api/v1/postpayments/approval")
    fun approvePaymentRequest(@Body paymentApprovalData: PaymentApprovalData): Call<Void>

}