package com.kimnlee.mobipay.presentation.screen

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.gson.Gson
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.common.FCMData
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiBgGray
import com.kimnlee.common.ui.theme.MobiBgWhite
import com.kimnlee.common.ui.theme.MobiBlue
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.ui.theme.MobiTextDarkGray
import com.kimnlee.common.ui.theme.MobiUnselectedButtonGray
import com.kimnlee.mobipay.presentation.viewmodel.ShowMoreViewModel

val SettingsIconColor = Color(0xFFB1B8C0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowMoreScreen(
    loginViewModel: LoginViewModel,
    showMoreViewModel: ShowMoreViewModel,
    navController: NavController
) {

    val userName by showMoreViewModel.userName.collectAsState()
    val userPicture by showMoreViewModel.userPicture.collectAsState()
    val userPhoneNumber by showMoreViewModel.userPhoneNumber.collectAsState()
    val userEmail by showMoreViewModel.userEmail.collectAsState()

    val formattedPhoneNumber = remember(userPhoneNumber) {
        formatPhoneNumber(userPhoneNumber)
    }

    var showSettingsMenu by remember { mutableStateOf(false) }
    var isAutoSaveParking by remember { mutableStateOf(false) }

    val fcmDataTemporary = FCMData(
        paymentBalance="2100",
        approvalWaitingId="25",
        autoPay="false",
        merchantId="1911",
        lat="36.095567",
        lng="128.43126",
        info="초코 도넛 x 1",
        type="transactionRequest",
        merchantName="스타벅스 구미인의DT점",
        cardNo = null
    )
    val fcmDataJson = Uri.encode(Gson().toJson(fcmDataTemporary))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = "더보기",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MobiTextAlmostBlack,
                    fontSize = 24.sp
                ) },
                actions = {
                    IconButton(onClick = { showSettingsMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = SettingsIconColor
                        )
                    }
                    SettingsDropdownMenu(
                        expanded = showSettingsMenu,
                        onDismissRequest = { showSettingsMenu = false },
                        isAutoSaveParking = isAutoSaveParking,
                        onAutoSaveParkingChange = { isAutoSaveParking = it }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MobiBgGray,
                    titleContentColor = MobiTextDarkGray
                )
            )
        },
        containerColor = MobiBgGray
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // 프로필 영역
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        ProfileSection(userName, userPicture, userEmail, formattedPhoneNumber)
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
                        MenuItem("프리오더", { }, emoji = "🍴"),
                        MenuItem("결제화면(임시)", { navController.navigate("payment_requestmanualpay?fcmData=${fcmDataJson}") }),
                        MenuItem("로그아웃", { loginViewModel.logout() })
                    )
                )
            }
        }
    }
}

@Composable
fun ProfileSection(userName: String, userPicture: String, userEmail: String, userPhoneNumber: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        ProfileImage(userPicture)
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MobiTextAlmostBlack,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                color = MobiTextDarkGray,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = userPhoneNumber,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                color = MobiTextDarkGray,
            )
        }
    }
}

@Composable
fun ProfileImage(userPicture: String?) {
    val imageUrl = if (userPicture.isNullOrBlank()) {
        R.drawable.default_profile // 기본 프로필 이미지 리소스
    } else {
        userPicture
    }

    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = imageUrl).apply(block = fun ImageRequest.Builder.() {
                crossfade(true)
                transformations(CircleCropTransformation())
            }).build()
        ),
        contentDescription = "Profile Picture",
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun MenuSection(title: String, items: List<MenuItem>) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = MobiTextDarkGray,
        modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
    )
    items.forEachIndexed { index, item ->
        MenuItemCard(item = item)
        if (index < items.size - 1) {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun MenuItemCard(item: MenuItem) {
    val isLogout = item.text == "로그아웃"
    val cardColor = if (isLogout) Color.Transparent else Color.White
    val textColor = if (isLogout) Color.Red else MobiTextAlmostBlack
    val borderColor = if (isLogout) Color.LightGray else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLogout) 0.dp else 2.dp),
        onClick = item.onClick,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = if (isLogout) BorderStroke(1.dp, borderColor) else null
    ) {
        if (isLogout) {
            // 로그아웃 버튼 레이아웃
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.text,
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
            }
        } else {
            // 다른 버튼들의 레이아웃
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                if (item.emoji != null) {
                    Text(
                        text = item.emoji,
                        fontFamily = FontFamily(Font(R.font.emoji)),
                        fontSize = 22.sp,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        modifier = Modifier
                            .width(40.dp)
                            .padding(8.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.width(40.dp))
                }
                Text(
                    text = item.text,
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SettingsDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    isAutoSaveParking: Boolean,
    onAutoSaveParkingChange: (Boolean) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .background(Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "주차 위치 자동 저장",
                style = MaterialTheme.typography.titleMedium,
                color = MobiTextAlmostBlack,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )
            CustomSwitch(
                checked = isAutoSaveParking,
                onCheckedChange = onAutoSaveParkingChange
            )
        }
    }
}

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MobiBgWhite,
            checkedTrackColor = MobiBlue,
            uncheckedThumbColor = MobiUnselectedButtonGray,
            uncheckedTrackColor = MobiBgGray
        )
    )
}

fun formatPhoneNumber(phoneNumber: String): String {
    return try {
        val cleaned = phoneNumber.replace(Regex("[^\\d]"), "")
        when {
            cleaned.length == 11 -> "${cleaned.substring(0, 3)}-${cleaned.substring(3, 7)}-${cleaned.substring(7)}"
            cleaned.length == 10 -> "${cleaned.substring(0, 3)}-${cleaned.substring(3, 6)}-${cleaned.substring(6)}"
            else -> phoneNumber
        }
    } catch (e: Exception) {
        phoneNumber
    }
}

data class MenuItem(
    val text: String,
    val onClick: () -> Unit,
    val emoji: String? = null
)