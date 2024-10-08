package com.kimnlee.notification.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.kimnlee.common.event.EventBus
import com.kimnlee.common.event.NewNotificationEvent
import com.kimnlee.common.utils.LocalDateTimeAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NotificationRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val paymentRequestKey = "payment_requests"
    private val invitationMessagesKey = "invitation_messages"

    var paymentRequestMessages: MutableList<Notification>
        get() {
            val json = sharedPreferences.getString(paymentRequestKey, null)
            return if (json != null) {
                gson.fromJson(json, object : TypeToken<MutableList<Notification>>() {}.type)
            } else {
                mutableListOf()
            }
        }
        private set(value) {
            val json = gson.toJson(value)
            sharedPreferences.edit().putString(paymentRequestKey, json).apply()
        }

    var invitationMessages: MutableList<Notification>
        get() {
            val json = sharedPreferences.getString(invitationMessagesKey, null)
            return if (json != null) {
                gson.fromJson(json, object : TypeToken<MutableList<Notification>>() {}.type)
            } else {
                mutableListOf()
            }
        }
        private set(value) {
            val json = gson.toJson(value)
            sharedPreferences.edit().putString(invitationMessagesKey, json).apply()
        }

    fun addPaymentRequestNotification(notification: Notification) {
        val currentList = paymentRequestMessages
        currentList.add(notification)
        paymentRequestMessages = currentList
        emitNewNotificationEvent()
    }

    fun addInvitationNotification(notification: Notification) {
        val currentList = invitationMessages
        currentList.add(notification)
        invitationMessages = currentList
        emitNewNotificationEvent()
    }

    fun clearAllNotifications() {
        paymentRequestMessages = mutableListOf()
        invitationMessages = mutableListOf()
        sharedPreferences.edit().apply {
            remove(paymentRequestKey)
            remove(invitationMessagesKey)
            apply()
        }
        coroutineScope.launch {
            EventBus.emit(NewNotificationEvent(false))  // 알림이 없음을 나타내는 이벤트 발생
        }
    }

    private fun emitNewNotificationEvent() {
        coroutineScope.launch {
            EventBus.emit(NewNotificationEvent(true))
        }
    }
}