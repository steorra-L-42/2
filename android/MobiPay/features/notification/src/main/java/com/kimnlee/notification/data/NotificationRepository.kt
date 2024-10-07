package com.kimnlee.notification.data

object NotificationRepository {
    val paymentRequestMessages = mutableListOf<Notification>()
    val invitationMessages = mutableListOf<Notification>()

    fun addPaymentRequestNotification(notification: Notification) {
        paymentRequestMessages.add(notification)
    }

    fun addInvitationNotification(notification: Notification) {
        invitationMessages.add(notification)
    }
}
