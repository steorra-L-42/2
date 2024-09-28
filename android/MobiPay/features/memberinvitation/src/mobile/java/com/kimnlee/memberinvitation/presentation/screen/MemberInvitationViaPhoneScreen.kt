package com.kimnlee.memberinvitation.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel

@Composable
fun MemberInvitationViaPhoneScreen(
    vehicleId: Int,
    onNavigateBack: () -> Unit,
    viewModel: MemberInvitationViewModel = viewModel(),
    onNavigateToConfirmation : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateBack) {
            Text("뒤로 가기")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "멤버 초대",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 전화번호 입력 칸
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { viewModel.updatePhoneNumber(it) },
            label = { Text("전화번호로 초대하기") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            trailingIcon = {
                IconButton(onClick = { viewModel.inviteMember() }) {
                    Icon(Icons.Default.Add, contentDescription = "초대")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is MemberInvitationViewModel.UiState.Loading -> {
                CircularProgressIndicator()
            }

            is MemberInvitationViewModel.UiState.InvitationSent -> {
                Text("초대가 완료되었습니다.", color = MaterialTheme.colorScheme.primary)
            }

            is MemberInvitationViewModel.UiState.UserNotFound -> {
                Text("가입된 유저가 아닙니다.", color = MaterialTheme.colorScheme.error)
            }

            else -> {} // 초기 상태, 아무것도 안 함
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "차량 ID: $vehicleId", // 실제 통신시 차량 ID로 차량 정보(차량 번호)를 받아올 예정
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "이 차량에 속한 멤버 표시(이름과 전화번호)" // 첫번째는 오너, 두번째 부터 멤버
        )

        Button(onClick = {
            onNavigateToConfirmation()
        }) {
            Text(text = "$vehicleId 자동차의 멤버 수락 화면")
        }
    }
}