package com.kimnlee.memberinvitation.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun MemberInvitationConfirmationScreen(
    onNavigateBack: () -> Unit,
    vehicleId : Int,
) {
    var isChecked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "$vehicleId 번째 자동차의 초대확인용 테스트페이지",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 차량 이미지와 차량 번호(vehicleId를 기준으로 api 통신)

        Spacer(modifier = Modifier.height(16.dp))

        Text("초대 됐어요!")

        Spacer(modifier = Modifier.height(16.dp))

        // 초대 받은 사람의 카드 목록 중에서 주결제 카드 선택
        Text("카드 목록 공간")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(4.dp))
                    .padding(4.dp)
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Transparent,
                        uncheckedColor = Color.Transparent,
                        checkmarkColor = Color.Black
                    )
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("나중에 선택하기")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text("수락")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text("거절")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateBack) {
            Text("뒤로 가기")
        }
    }
}