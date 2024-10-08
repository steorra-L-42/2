package com.kimnlee.payment.data.model

data class PaymentHistoryItem(
    val transactionUniqueNo: Int,
    val merchantName: String,
    val transactionDate: String,
    val transactionTime: String,
    val paymentBalance: Int
)

data class PaymentHistoryResponse(
    val items: List<PaymentHistoryItem>
)

data class ReceiptResponse(
    val transactionUniqueNo: Int,
    val transactionDate: String,
    val transactionTime: String,
    val paymentBalance: Int,
    val info: String,
    val merchantName: String,
    val cardNo: String,
    val cardName: String,
    val merchantId: Int,
    val lat: Double,
    val lng: Double
)