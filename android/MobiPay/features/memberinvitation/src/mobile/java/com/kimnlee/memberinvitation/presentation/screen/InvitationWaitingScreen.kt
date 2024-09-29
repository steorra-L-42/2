package com.kimnlee.memberinvitation.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kimnlee.common.R
import kotlinx.coroutines.delay

val BackgroundColor = Color(0xFFF2F4F6)
val PrimaryColor = Color(0xFF3182F6)
val TextColor = Color(0xFF191F28)
val SecondaryTextColor = Color(0xFF8B95A1)
val ButtonColor = Color(0xFFF2F3F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationWaitingScreen(navController: NavController) {
    var isLoading by remember { mutableStateOf(true) }
    var showInvitation by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        delay(5000) // 5초 대기
        isLoading = false
        showInvitation = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("초대 대기") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.Close, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                    titleContentColor = TextColor
                )
            )
        },
        containerColor = BackgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "초대를 확인하고 있어요...",
                        color = TextColor,
                        fontSize = 18.sp
                    )
                }
            }

            AnimatedVisibility(
                visible = showInvitation,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SingleInvitationCard()
            }
        }
    }
}

@Composable
fun SingleInvitationCard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.genesis_g90),
                contentDescription = "Car Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .padding(vertical = 24.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "차량 초대가 도착했어요",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "광주 82가 1818",
                fontSize = 18.sp,
                color = TextColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "차량의 멤버로 초대되었습니다.\n수락하시겠습니까?",
                fontSize = 16.sp,
                color = SecondaryTextColor,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {  },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("거절", color = TextColor)
            }
            Button(
                onClick = {  },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("수락", color = Color.White)
            }
        }
    }
}