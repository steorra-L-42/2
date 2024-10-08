package com.kimnlee.common.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    private val _events = MutableSharedFlow<Any>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    suspend fun emit(event: Any) {
        _events.emit(event)
    }
}

// 새 알림 이벤트
data class NewNotificationEvent(val hasNew: Boolean)