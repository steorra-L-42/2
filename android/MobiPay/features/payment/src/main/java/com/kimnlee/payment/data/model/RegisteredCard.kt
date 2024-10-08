package com.kimnlee.payment.data.model

import com.google.gson.annotations.SerializedName

// 등록된 카드
data class RegisteredCard(
    val mobiUserId: Int,
    val ownedCardId: Int,
    val oneTimeLimit: Int,
    val cardNo: String,
    @SerializedName("cardExpriyDate") val cardExpiryDate: String, // 여기 오타인데 백에서 수정하기 전에 임시로 설정
    val cardName: String,
    val autoPayStatus: Boolean = false,
)

// 등록된 카드 목록
data class RegisteredCardListResponse(
    val items: List<RegisteredCard>       // 카드 목록
)