package com.kimnlee.common.utils

fun findCardCompanyName(cardNumber: String): String {
    val company = cardNumber.take(4)
    return when (company) {
        "1001" -> "KB카드"
        "1002" -> "삼성카드"
        "1003" -> "롯데카드"
        "1004" -> "우리카드"
        "1006" -> "현대카드"
        "1007" -> "BC바로카드"
        "1008" -> "농협카드"
        "1009" -> "하나카드"
        "1010" -> "IBK카드"
        else -> "싸피카드"
    }
}