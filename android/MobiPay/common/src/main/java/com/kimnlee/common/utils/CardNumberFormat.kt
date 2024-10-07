package com.kimnlee.common.utils

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

fun nonFormatCardNumber(cardNumber: String): String {
    // 입력된 카드 번호에서 숫자만 추출
    val digitsOnly = cardNumber.filter { it.isDigit() }

    // 카드 번호 가리지않고 포맷팅
    return buildString {
        append(digitsOnly.substring(0, 4))
        append(" ")
        append(digitsOnly.substring(4, 8))
        append(" ")
        append(digitsOnly.substring(8, 12))
        append(" ")
        append(digitsOnly.substring(12, 16))
    }
}