package com.kimnlee.mobipay.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.R

val BackgroundColor = Color(0xFFF2F4F6)
val ButtonColor = Color(0xFFF2F3F5)
val ButtonTextColor = Color(0xFF505967)
val SettingsIconColor = Color(0xFFB1B8C0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowMoreScreen(
    viewModel: LoginViewModel,
    authManager: AuthManager,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("더보기") },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = SettingsIconColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                    titleContentColor = ButtonTextColor
                )
            )
        },
        containerColor = BackgroundColor
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Profile Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            ) {
                                Text(
                                    "프사",
                                    modifier = Modifier.align(Alignment.Center),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("김싸피", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ButtonTextColor)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ProfileMenuButton(
                                text = "프로필 메뉴1",
                                onClick = {},
                                modifier = Modifier.weight(1f)
                            )
                            ProfileMenuButton(
                                text = "프로필 메뉴2",
                                onClick = {},
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                MenuSection(
                    title = "모든 서비스",
                    items = listOf(
                        MenuItem("결제 내역", { navController.navigate("paymenthistory") }, emoji = "💳"),
                        MenuItem("초대 대기", { navController.navigate("memberinvitation_invitationwaiting") }, emoji = "📩"),
                        MenuItem("메뉴 1", { }),
                        MenuItem("메뉴 2", { }),
                        MenuItem("로그아웃", { viewModel.testLogout() })
                    )
                )
            }
        }
    }
}

@Composable
fun ProfileMenuButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonColor,
            contentColor = ButtonTextColor
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.height(40.dp)
    ) {
        Text(text, fontSize = 14.sp)
    }
}

@Composable
fun MenuSection(title: String, items: List<MenuItem>) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ButtonTextColor)
    Spacer(modifier = Modifier.height(8.dp))
    items.forEach { item ->
        MenuItemCard(item = item)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun MenuItemCard(item: MenuItem) {
    val isLogout = item.text == "로그아웃"
    val cardColor = if (isLogout) Color.Transparent else Color.White
    val textColor = if (isLogout) Color.Red else ButtonTextColor
    val borderColor = if (isLogout) Color.LightGray else Color.Transparent

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLogout) 0.dp else 2.dp),
        onClick = item.onClick,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = if (isLogout) BorderStroke(1.dp, borderColor) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isLogout) Arrangement.Center else Arrangement.Start
        ) {
            if (item.emoji != null && !isLogout) {
                Text(
                    text = item.emoji,
                    fontFamily = FontFamily(Font(R.font.emoji)),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text(
                text = item.text,
                color = textColor,
                fontSize = 16.sp,
                textAlign = if (isLogout) TextAlign.Center else TextAlign.Start,
                modifier = if (isLogout) Modifier.fillMaxWidth() else Modifier
            )
        }
    }
}

data class MenuItem(
    val text: String,
    val onClick: () -> Unit,
    val emoji: String? = null
)