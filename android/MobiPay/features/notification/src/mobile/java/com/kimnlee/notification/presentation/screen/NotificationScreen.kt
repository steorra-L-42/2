package com.kimnlee.notification.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign

@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("전체", "결제", "멤버", "기타")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "뒤로 가기",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Black
                )
            }
            
            Text(
                text = "알림",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        CustomTabRow(
            tabs = tabs,
            selectedTabIndex = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (selectedTab) {
            0 -> AllNotifications()
            1 -> PaymentRequests()
            2 -> MemberInvitations()
            3 -> OtherNotifications()
        }
    }
}

@Composable
fun CustomTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
//            .fillMaxWidth()
            .width(320.dp)
            .height(40.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            CustomTab(
                title = title,
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CustomTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Color.Black else Color.White)
            .border(
                width = 1.dp,
                color = if (selected) Color.Black else Color.LightGray,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) Color.White else Color.Black,
            fontWeight = FontWeight.Medium
        )
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
    LazyColumn {
        items(notifications) { notification ->
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
            .background(Color(0xFF222629), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTime(notification.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
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