package com.kimnlee.notification.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.kimnlee.common.ui.theme.*
import com.kimnlee.notification.data.Notification
import com.kimnlee.notification.data.NotificationRepository
import com.kimnlee.notification.data.NotificationType

@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val notificationRepository = remember { NotificationRepository(context) }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("전체", "결제", "멤버")

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

            TextButton(
                onClick = { notificationRepository.clearAllNotifications() },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text("모두 지우기", color = Color.Red)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        CustomTabRow(
            tabs = tabs,
            selectedTabIndex = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (selectedTab) {
            0 -> AllNotifications(notificationRepository)
            1 -> PaymentRequests(notificationRepository)
            2 -> MemberInvitations(notificationRepository)
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
fun AllNotifications(notificationRepository: NotificationRepository) {
    val allNotifications = remember {
        (notificationRepository.paymentRequestMessages + notificationRepository.invitationMessages)
            .sortedByDescending { it.timestamp }
    }

    NotificationList(allNotifications)
}

@Composable
fun PaymentRequests(notificationRepository: NotificationRepository) {
    NotificationList(notificationRepository.paymentRequestMessages)
}

@Composable
fun MemberInvitations(notificationRepository: NotificationRepository) {
    NotificationList(notificationRepository.invitationMessages)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = formatTime(notification.timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp)
                .offset(y = 1.dp)
        )

        val (icon, type) = when (notification.type) {
            NotificationType.PAYMENT -> {
                val details = notification.details
                val paymentMessage = "${details.merchantName}에서 ${details.paymentBalance}원을 결제했습니다"
                Pair(Icons.Outlined.CreditCard, paymentMessage)
            }
            NotificationType.MEMBER -> {
                val details = notification.details
                val invitationMessage = "${details.inviterName}님의 차에 초대되었습니다"
                Pair(Icons.Outlined.Group, invitationMessage)
            }
        }

        Icon(
            imageVector = icon,
            contentDescription = "아이콘",
            tint = MobiTextAlmostBlack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(20.dp)
                .padding(end = 4.dp)
                .offset(y = (-2).dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 32.dp, end = 80.dp)
        ) {
            Column {
                Text(
                    text = type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MobiTextAlmostBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (notification.type == NotificationType.PAYMENT) {
                    Text(
                        text = notification.details.info ?: "정보 없음",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                } else if (notification.type == NotificationType.MEMBER) {
                    val details = notification.details
                    Text(
                        text = "차량 번호: ${details.carNumber ?: "정보 없음"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "차량 모델: ${details.carModel ?: "정보 없음"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

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