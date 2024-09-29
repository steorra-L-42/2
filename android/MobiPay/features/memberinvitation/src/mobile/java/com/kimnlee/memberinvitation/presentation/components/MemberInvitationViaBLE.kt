package com.kimnlee.memberinvitation.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kimnlee.memberinvitation.R
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import kotlinx.coroutines.delay


@Composable
fun MemberInvitationViaBLE(
    viewModel: MemberInvitationViewModel,
    onNavigateToConfirmation: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(5) } // 5초
    LaunchedEffect(Unit) {
        for (i in timeLeft downTo -1) {
            delay(1000) // 1초 대기
            timeLeft = i // 남은 시간 업데이트
        }
        viewModel.closeInvitationBLE()
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "근처 멤버 초대",
            style = MaterialTheme.typography.headlineMedium,
        )
        RadarAnimation(
            Modifier
                .fillMaxSize() // 화면을 꽉 채움
                .weight(0.6f)
        )
        Text(text = "남은 시간: $timeLeft 초", modifier = Modifier.weight(0.22f))
        // 테스트 코드 추가(구현 시 삭제 예정)
        Row() {
            Button(
                onClick = {
                    onNavigateToConfirmation()
                    viewModel.closeInvitationBLE()
                    viewModel.closeBottomSheet()
                },
            ) {
                Text("멤버 초대 확인 페이지로 이동")
            }
            Spacer(modifier = Modifier.padding(10.dp))
            OutlinedButton(
                onClick = {
                    viewModel.closeInvitationBLE()
                },
            ) {
                Text("돌아가기")
            }
        }

    }
}

@Composable
fun RadarAnimation(modifier: Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

    // 애니메이션으로 아이콘의 크기 변화 설정
    val size by infiniteTransition.animateFloat(
        initialValue = 0.98f, // 최소 크기
        targetValue = 1.0f,  // 최대 크기
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing), // 1초간 애니메이션
            repeatMode = RepeatMode.Reverse // 크기 확장 후 다시 축소
        )
    )
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center // 중앙 정렬
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_radar_24), // 레이더 아이콘
            contentDescription = null,
            modifier = Modifier
                .size(300.dp) // 기본 크기 설정
                .scale(size), // 애니메이션으로 크기 조절 (모든 방향으로 확장)
            tint = Color.Blue
        )
    }
}

