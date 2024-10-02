package com.kimnlee.vehiclemanagement.data.model

data class CarMember(
    val mobiUserId: Int,        // 사용자 id
    val name: String,           // 사용자 이름
    val picture: String,        // 썸네일 이미지
    val phoneNumber: String,    // 사용자 전화번호
    val created: String         // 생성일
)

data class CarMembersResponse(
    val items: List<CarMember>  // 사용자 목록
)