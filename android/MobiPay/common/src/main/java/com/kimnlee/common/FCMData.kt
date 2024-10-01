package com.kimnlee.common

import java.io.Serializable

data class FCMData(
    val autoPay: String?,
    val cardNo: String?,
    val approvalWaitingId: String?,
    val merchantId: String?,
    val paymentBalance: String?,
    val merchantName: String?,
    val info: String?,
    val lat: String?,
    val lng: String?,
    val type: String?
) : Serializable
