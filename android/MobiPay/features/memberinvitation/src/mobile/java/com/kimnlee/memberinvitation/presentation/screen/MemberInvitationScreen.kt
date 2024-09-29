package com.kimnlee.memberinvitation.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import com.kimnlee.memberinvitation.presentation.components.MemberInvitationBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberInvitationScreen(
    vehicleId: Int,
    onNavigateBack: () -> Unit,
    viewModel: MemberInvitationViewModel = viewModel(),
    onNavigateToInvitePhone : () -> Unit,
    onNavigateToConfirmation : () -> Unit
) {
    val showBottomSheet by viewModel.showBottomSheet.collectAsState()

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateBack) {
            Text("뒤로 가기")
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            Text(text = "$vehicleId 번 차량의 초대할 방법을 고르는 페이지")
            // 모달을 띄우는 버튼
            Button(onClick = {
                viewModel.openBottomSheet()
            }) {
                Text(text = "모달 열기")
            }
            if (showBottomSheet) {
                MemberInvitationBottomSheet(
                    vehicleId = vehicleId,
                    sheetState = sheetState,
                    scope = scope,
                    viewModel = viewModel,
                    onNavigateToInvitePhone = onNavigateToInvitePhone,
                    onNavigateToConfirmation = onNavigateToConfirmation
                )
            }
        }
    }
}