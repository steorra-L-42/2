package com.kimnlee.common.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun formatDateTime(date: String, time: String): String {
    val dateTimeString = "$date$time"
    val parsedDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    return parsedDateTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss"))
}

fun formatDateTimeWithHyphens(date: String, time: String): String {
    val dateTimeString = "$date$time"
    val parsedDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    return parsedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}