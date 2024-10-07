package com.kimnlee.notification.data

import java.time.LocalDateTime

data class NotificationDetails(
    val merchantName: String?,
    val paymentBalance: String?,
    val info: String?,
    val body: String?,
    val inviterName: String?,
    val inviterPicture: String?,
    val carNumber: String?,
    val carModel: String?
)


data class Notification(
    val details: NotificationDetails,
    val timestamp: LocalDateTime,
    val type: NotificationType
)

enum class NotificationType {
    PAYMENT, MEMBER
}
