package com.kimnlee.memberinvitation.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.kimnlee.memberinvitation.R
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


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
        containerColor = Color(0xFFF2F4F6),
    ) {
        val onExpandClick = {
            scope.launch { sheetState.expand() }
        }
        if (!showInvitationBLE) {
            Column(modifier = Modifier.padding(16.dp)) {
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
                    icon = ImageVector.vectorResource(id = R.drawable.baseline_radar_24),
                    title = "주변 기기로 초대하기",
                    description = "다른 회원과 폰은 겹쳐주세요.",
                    onItemClick = { viewModel.openInvitationBLE()
                        onExpandClick()}
                )
            }
        } else {
            Column(modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()) {
                MemberInvitationViaBLE(
                    viewModel = viewModel,
                    onNavigateToConfirmation = onNavigateToConfirmation
                )
            }
        }
    }
}