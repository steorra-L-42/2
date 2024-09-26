package com.kimnlee.notification.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("전  체", "결제 요청", "멤버 초대", "기타")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onNavigateBack) {
            Text("뒤로 가기")
        }

        Text(
            text = "알림",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        when (selectedTab) {
            0 -> AllNotifications()
            1 -> PaymentRequests()
            2 -> MemberInvitations()
            3 -> OtherNotifications()
        }
    }
}

@Composable
fun AllNotifications() {
    val allNotifications = listOf(
        *getPaymentRequests().toTypedArray(),
        *getMemberInvitations().toTypedArray(),
        *getOtherNotifications().toTypedArray()
    ).sortedByDescending { it.timestamp }

    NotificationList(allNotifications)
}

@Composable
fun PaymentRequests() {
    NotificationList(getPaymentRequests())
}

@Composable
fun MemberInvitations() {
    NotificationList(getMemberInvitations())
}

@Composable
fun OtherNotifications() {
    NotificationList(getOtherNotifications())
}

fun getPaymentRequests(): List<Notification> {
    return listOf(
        Notification("결제 요청", LocalDateTime.now().minusMinutes(20)),
        Notification("결제 요청", LocalDateTime.now().minusHours(2)),
        Notification("결제 요청", LocalDateTime.now().minusDays(1)),
        Notification("결제 요청", LocalDateTime.now().minusDays(2))
    )
}

fun getMemberInvitations(): List<Notification> {
    return listOf(
        Notification("누군가의 차에 초대됐어요", LocalDateTime.now().minusMinutes(10)),
        Notification("누군가의 차에 초대됐어요", LocalDateTime.now().minusDays(10))
    )
}

fun getOtherNotifications(): List<Notification> {
    return listOf(
        Notification("공지사항 1", LocalDateTime.now().minusDays(1)),
        Notification("공지사항 2", LocalDateTime.now().minusDays(2)),
        Notification("공지사항 3", LocalDateTime.now().minusDays(3)),
        Notification("공지사항 4", LocalDateTime.now().minusDays(4)),
        Notification("공지사항 5", LocalDateTime.now().minusDays(5))
    )
}

@Composable
fun NotificationList(notifications: List<Notification>) {
    Column {
        notifications.forEach { notification ->
            NotificationItem(notification)
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column {
            Text(text = notification.message, style = MaterialTheme.typography.bodyLarge)
            Text(text = formatTime(notification.timestamp), style = MaterialTheme.typography.bodySmall)
        }
    }
}

data class Notification(val message: String, val timestamp: LocalDateTime)

fun formatTime(timestamp: LocalDateTime): String {
    val now = LocalDateTime.now()
    val diff = java.time.Duration.between(timestamp, now)

    return when {
        diff.toMinutes() < 1 -> "방금 전"
        diff.toMinutes() < 60 -> "${diff.toMinutes()}분 전"
        diff.toHours() < 24 && now.dayOfMonth == timestamp.dayOfMonth -> "${diff.toHours()}시간 전"
        else -> timestamp.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    }
}