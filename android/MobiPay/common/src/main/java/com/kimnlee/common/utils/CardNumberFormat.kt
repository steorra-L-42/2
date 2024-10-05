fun formatCardNumber(cardNumber: String): String {
    // 입력된 카드 번호에서 숫자만 추출
    val digitsOnly = cardNumber.filter { it.isDigit() }

    // 카드 번호 포맷팅
    return buildString {
        append(digitsOnly.substring(0, 4))
        append(" **** **** ")
        append(digitsOnly.substring(12, 16))
    }
}