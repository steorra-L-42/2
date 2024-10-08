package com.kimnlee.cardmanagement.data.model

// 소유한 카드
data class OwnedCard(
    val id: Int,                // 카드 pk
    val cardNo: String,         // 카드 번호
    val cvc: String,            // CVC 코드
    val withdrawalDate: String, // 출금일
    val cardExpiryDate: String, // 카드 만료일
    val created: String,        // 생성일
    val mobiUserId: Int,        // 사용자 ID
    val accountId: Int,         // 계좌 ID
    val cardUniqueNo: String       // 카드 고유 번호
)

// 소유한 카드 목록
data class OwnedCardListResponse(
    val items: List<OwnedCard>       // 카드 목록
)

// 등록된 카드
data class RegisteredCard(
    val mobiUserId: Int,
    val ownedCardId: Int,
    val oneTimeLimit: Int,
    val cardNo: String,
    val cardExpriyDate: String, // 여기 오타인데 백에서 수정하기 전에 임시로 설정
    val cardName: String,
    val autoPayStatus: Boolean = false,
)

// 등록된 카드 목록
data class RegisteredCardListResponse(
    val items: List<RegisteredCard>       // 카드 목록
)

// 소유한 카드에서 등록하기
data class RegisterCardRequest(
    val ownedCardId : Int,
    val oneTimeLimit: Int
)

// 카드 정보 넘겨줄때 data
data class CardInfo(
    val cardId: Int,
    val cardNo: String
)

// 카드 등록 후 응답
data class RegisterCardResponse(
    val mobiUserId : Int,
    val ownedCardId : Int,
    val cardNo: String,
    val cardExpiryDate: String,
    val oneTimeLimit : Int
)

// 카드 조회
data class CardDetailResponse(
    val ownedCardId: Int,
    val cardNo: String,
    val cvc: String,
    val cardExpiryDate: String,
    val oneTimeLimit: Int
)

// 자동 결제 등록 요청
data class AutoPaymentCardRequest(
    val ownedCardId: Int,
    val autoPayStatus: Boolean
)

// 자동 결제 등록 응답
data class AutoPaymentCardResponse(
    val mobiUserId: Int,
    val ownedCardId: Int,
    val autoPayStatus: Boolean
)

// 마이 데이타 동의 설정
data class MyDataConsentResponse(
    val mobiUserId: Int,
    val myDataConsent: Boolean
)