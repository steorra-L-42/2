package com.kimnlee.payment.data.model

data class PaymentApprovalData(
    val approvalWaitingId: String,
    val merchantId: String,
    val paymentBalance: String,
    val cardNo: String,
    val info: String,
    val approved: Boolean
)