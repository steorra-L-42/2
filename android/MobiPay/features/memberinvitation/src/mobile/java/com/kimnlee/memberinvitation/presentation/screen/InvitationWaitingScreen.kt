package com.kimnlee.memberinvitation.presentation.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kimnlee.common.ui.theme.MobiSplashBgBlue
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel

val BackgroundColor = Color(0xFFF2F4F6)
val PrimaryColor = Color(0xFF3182F6)
val TextColor = Color(0xFF191F28)
val SecondaryTextColor = Color(0xFF8B95A1)
val ButtonColor = Color(0xFFF2F3F5)
private const val TAG = "InvitationWaitingScreen"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationWaitingScreen(
    memberInvitationViewModel: MemberInvitationViewModel = viewModel(),
    navController: NavController
) {
    var isLoading by remember { mutableStateOf(true) }
    var showInvitation by remember { mutableStateOf(false) }
    val phoneNumber by memberInvitationViewModel.phoneNumber.collectAsState("")
    val navigateEvent by memberInvitationViewModel.navigateEvent.collectAsState()

    LaunchedEffect(key1 = true) {
        memberInvitationViewModel.startAdvertising()
        isLoading = true
        showInvitation = true
    }

    DisposableEffect(Unit) {
        onDispose {
            memberInvitationViewModel.stopAdvertising()
        }
    }

    LaunchedEffect(navigateEvent) {
        if (navigateEvent) {
            navController.popBackStack()
            memberInvitationViewModel.triggerNavigateToInvitedScreen(navController)
            Log.d(TAG, "InvitationWaitingScreen: 지시 전달받음")
            memberInvitationViewModel.onNavigateHandled()
        }
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
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        "주변에서 회원님을 찾을 수 있도록\n텔레파시를 전달하는 중이에요!",
                        textAlign = TextAlign.Center,
                        color = TextColor,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(22.dp))
                    Text(
                        "내 번호\n${phoneNumberFormat(phoneNumber)}",
                        color = SecondaryTextColor,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        memberInvitationViewModel.stopAdvertising()
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MobiSplashBgBlue)
                ) {
                    Text(
                        text = "취소하고 돌아가기",
                        fontFamily = FontFamily(Font(com.kimnlee.common.R.font.psemibold)),
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}


private fun phoneNumberFormat(phoneNumber: String): String {
    return phoneNumber.replace(Regex("(\\d{3})(\\d{4})(\\d{4})"), "$1-$2-$3")
}