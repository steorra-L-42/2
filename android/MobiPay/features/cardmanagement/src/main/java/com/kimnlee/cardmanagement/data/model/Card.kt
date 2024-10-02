// 소유한 카드
data class OwnedCard(
    val id: Int,
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
    val mobiUserId: Long,
    val ownedCardId: Long,
    val oneDayLimit: Int,
    val oneTimeLimit: Int,
    val autoPayStatus: Boolean = false,
)

// 등록된 카드 목록
data class RegisteredCardListResponse(
    val items: List<RegisteredCard>       // 카드 목록
)

// 소유한 카드에서 등록하기
data class RegisterCardRequest(
    val ownedCardId : Long,
    val oneDayLimit: Int,
    val oneTimeLimit: Int,
    val password : String
)

data class RegisterCardResponse(
    val mobiUserId : Long,
    val ownedCardId : Long,
    val oneDayLimit : Int,
    val oneTimeLimit : Int,
    val password : String,
)

// 더미 데이터
data class Photos(
    val albumId: Int,
    val id: Int,
    val title : String,
    val url : String,
    val thumbnailUrl : String
)
