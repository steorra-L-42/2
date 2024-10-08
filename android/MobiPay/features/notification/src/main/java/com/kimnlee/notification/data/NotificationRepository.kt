package com.kimnlee.notification.data

import com.kimnlee.common.event.EventBus
import com.kimnlee.common.event.NewNotificationEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NotificationRepository {
    val paymentRequestMessages = mutableListOf<Notification>()
    val invitationMessages = mutableListOf<Notification>()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun addPaymentRequestNotification(notification: Notification) {
        paymentRequestMessages.add(notification)
        emitNewNotificationEvent()
    }

    fun addInvitationNotification(notification: Notification) {
        invitationMessages.add(notification)
        emitNewNotificationEvent()
    }

    private fun emitNewNotificationEvent() {
        coroutineScope.launch {
            EventBus.emit(NewNotificationEvent(true))
        }
    }
}
