package com.kimnlee.payment.data.api

import com.kimnlee.payment.data.model.PaymentApprovalData
import com.kimnlee.payment.data.model.Photos
import com.kimnlee.payment.data.model.RegisteredCardListResponse
import retrofit2.Call
import retrofit2.Response
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


    // 등록된 카드 목록 조회
    @GET("api/v1/cards")
    suspend fun getRegistrationCards(): Response<RegisteredCardListResponse>
}