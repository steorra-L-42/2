package com.kimnlee.vehiclemanagement.data.model

data class PaidParkingLotResponse(
    val parkingLotName: String,
    val parkingId: Int,
    val carNumber: String,
    val entry: String,
    val paymentBalance: Int
)