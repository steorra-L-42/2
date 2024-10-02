package com.kimnlee.vehiclemanagement.data.model

data class VehicleItem(
    val carId: Int,                 // 차량 PK
    val number: String,             // 차량 번호
    val created: String,            // 생성 날짜
    val autoPayStatus: Boolean,     // 자동 결제 여부
    val ownerId: Int,               // 차주 PK
    val carModel: String,           // 차량 모델
)

data class VehicleListResponse(
    val items: List<VehicleItem>    // 차량 목록
)

data class VehicleRegistrationRequest(
    val number: String,             // 차량 번호
    val carModel: String         // 차량 모델
)

data class VehicleRegistrationResponse(
    val carId: Int,
    val number: String,
    val created: String,
    val autoPayStatus: Boolean,
    val ownerId: Int,
    val carModel: String
)