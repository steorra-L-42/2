package com.kimnlee.memberinvitation.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel

@Composable
fun MemberInvitationViaBLE(
    viewModel: MemberInvitationViewModel,
    onNavigateToConfirmation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Text(
            text = "근처 멤버 초대",
            style = MaterialTheme.typography.headlineMedium
        )

        // 테스트 코드 추가(구현 시 삭제 예정)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onNavigateToConfirmation()
            viewModel.closeInvitationBLE()
            viewModel.closeBottomSheet()
        }) {
            Text("멤버 초대 확인 페이지로 이동")
        }
        Text("멤버 초대 확인 페이지로 이동")

        Text("멤버 초대 확인 페이지로 이동")

        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")
        Text("멤버 초대 확인 페이지로 이동")

    }
}