package com.kimnlee.mobipay.presentation.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🗂",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily(Font(R.font.emoji)),
                            modifier = Modifier
                                .padding(top = 8.dp, end = 8.dp)
                        )
                        Text(
                            text = "더보기",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack
                        )
                    }
                },
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
                    titleContentColor = MobiTextAlmostBlack
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
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // 프로필 영역
            item {
                ProfileSection(userName, userPicture, userEmail, formattedPhoneNumber)
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                Text(
                    text = "모든 서비스",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MobiTextDarkGray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MenuItem("결제 내역", "💳") { navController.navigate("paymenthistory") }
                    MenuItem("초대 대기", "📩") { navController.navigate("memberinvitation_invitationwaiting") }
                    MenuItem("결제화면(임시)", "💰") { navController.navigate("payment_requestmanualpay?fcmData=${fcmDataJson}") }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                LogoutButton { loginViewModel.logout() }
            }
        }
    }
}

@Composable
fun ProfileSection(userName: String, userPicture: String, userEmail: String, userPhoneNumber: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MobiBgWhite, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        ProfileImage(userPicture)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MobiTextAlmostBlack
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = MobiTextDarkGray
            )
            Text(
                text = userPhoneNumber,
                style = MaterialTheme.typography.bodyMedium,
                color = MobiTextDarkGray
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
fun MenuItem(text: String, emoji: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MobiBgWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily(Font(R.font.emoji)),
                modifier = Modifier
                    .padding(end = 16.dp)
                    .padding(top = 8.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MobiTextAlmostBlack
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate",
                tint = MobiTextDarkGray
            )
        }
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF3B30),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "로그아웃",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = FontFamily(Font(R.font.pbold))
        )
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
            .background(MobiBgWhite)
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
                onCheckedChange = onAutoSaveParkingChange,
                modifier = Modifier.background(MobiBgWhite)
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
            uncheckedTrackColor = MobiBgWhite
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