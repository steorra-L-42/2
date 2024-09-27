package com.kimnlee.memberinvitation.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kimnlee.memberinvitation.presentation.screen.MemberInvitationConfirmationScreen
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import kotlinx.coroutines.CoroutineScope


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberInvitationBottomSheet(
    vehicleId: Int,
    sheetState: SheetState,
    scope: CoroutineScope,
    viewModel: MemberInvitationViewModel,
    onNavigateToInvitePhone: () -> Unit,
    onNavigateToConfirmation: () -> Unit,
) {
    val showInvitationBLE by viewModel.showInvitationBLE.collectAsState()
    ModalBottomSheet(
        onDismissRequest = {
            viewModel.closeBottomSheet()
            viewModel.closeInvitationBLE()
        },
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!showInvitationBLE) {
                Text(text = "$vehicleId 차량의 초대 화면")
                MemberInvitationOptionItem(
                    icon = Icons.Default.Phone,
                    title = "전화번호로\n멤버 초대하기",
                    description = "다른 회원님의 전화번호를 입력해 초대할 수 있습니다.",
                    onItemClick = {
                        onNavigateToInvitePhone()
                        viewModel.closeBottomSheet()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                MemberInvitationOptionItem(
                    icon = Icons.Default.Face,
                    title = "주변 기기로 초대하기",
                    description = "다른 회원과 폰은 겹쳐주세요.",
                    onItemClick = { viewModel.openInvitationBLE() }
                )
            } else {
                MemberInvitationViaBLE(
                    viewModel = viewModel,
                    onNavigateToConfirmation = onNavigateToConfirmation
                )
            }
        }
    }
}