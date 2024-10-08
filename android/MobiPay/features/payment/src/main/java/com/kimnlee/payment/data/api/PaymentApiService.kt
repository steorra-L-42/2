package com.kimnlee.payment.data.api

import com.kimnlee.payment.data.model.PaymentApprovalData
import com.kimnlee.payment.data.model.PaymentHistoryResponse
import com.kimnlee.payment.data.model.ReceiptResponse
import com.kimnlee.payment.data.model.RegisteredCardListResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApiService {

    @POST("")
    suspend fun approvalPaymentRequest()

    @Headers("Content-Type: application/json")
    @POST("/api/v1/postpayments/approval")
    fun approvePaymentRequest(@Body paymentApprovalData: PaymentApprovalData): Call<Void>

    // 등록된 카드 목록 조회
    @GET("api/v1/cards")
    suspend fun getRegistrationCards(): Response<RegisteredCardListResponse>

    // 결제 내역 조회
    @GET("api/v1/postpayments/history")
    suspend fun getPaymentHistory(): Response<PaymentHistoryResponse>

    // 전자 영수증 출력
    @GET("api/v1/postpayments/receipt/{transaction_unique_no}")
    suspend fun printReceipt(
        @Path("transaction_unique_no") transactionUniqueNo: Int): Response<ReceiptResponse>
}