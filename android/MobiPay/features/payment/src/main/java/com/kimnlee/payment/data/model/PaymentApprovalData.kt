package com.kimnlee.payment.data.model

data class PaymentApprovalData(
    val approvalWaitingId: Long,
    val merchantId: Long,
    val paymentBalance: Long,
    val cardNo: String,
    val info: String,
    val approved: Boolean
)