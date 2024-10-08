package com.kimnlee.payment.data.api

import com.kimnlee.payment.data.model.PaymentApprovalData
import com.kimnlee.payment.data.model.PaymentHistoryResponse
import com.kimnlee.payment.data.model.ReceiptRequest
import com.kimnlee.payment.data.model.ReceiptResponse
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

    @GET("api/v1/postpayments/history")
    suspend fun getPaymentHistory(): Response<PaymentHistoryResponse>

    @GET("api/v1/postpayments/receipt/{transaction_unique_no}")
    suspend fun printReceipt(
        @Path("transaction_unique_no") transactionUniqueNo: Int,
        @Body receiptRequest: ReceiptRequest
    ): Response<ReceiptResponse>
}