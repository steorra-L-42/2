package com.kimnlee.memberinvitation.presentation.components

import android.bluetooth.BluetoothManager
import android.content.Context
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
import androidx.core.content.ContextCompat.getSystemService
import com.kimnlee.common.FCMDependencyProvider
import com.kimnlee.common.utils.MobiNotificationManager
import com.kimnlee.memberinvitation.R
import com.kimnlee.memberinvitation.data.api.MemberInvitationApiService
import com.kimnlee.memberinvitation.data.repository.MemberInvitationRepository
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberInvitationBottomSheet(
    context: Context,
    vehicleId: Int,
    sheetState: SheetState,
    scope: CoroutineScope,
    viewModel: MemberInvitationViewModel,
    onNavigateToInvitePhone: () -> Unit,
    onNavigateToConfirmation: () -> Unit,
) {
    val showInvitationBLE by viewModel.showInvitationBLE.collectAsState()
    val onExpandClick = {
        scope.launch { sheetState.expand() }
    }
    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight(),
        onDismissRequest = {
            viewModel.closeBottomSheet()
            viewModel.closeInvitationBLE()
        },
        sheetState = sheetState,
        containerColor = Color(0xFFF2F4F6),
    ) {

        val apiClient = (context.applicationContext as? FCMDependencyProvider)?.apiClient
        var memberInvitationRepository : MemberInvitationRepository? = null
        if(apiClient != null){
            val memberInvitationApiService = apiClient.authenticatedApi.create(
                MemberInvitationApiService::class.java)
            val mobiNotificationManager = MobiNotificationManager.getInstance(context)
            memberInvitationRepository = MemberInvitationRepository(memberInvitationApiService, mobiNotificationManager, context.applicationContext, viewModel)
        }

        if (!showInvitationBLE) {
            Column(modifier = Modifier.padding(16.dp)) {
//                Text(text = "$vehicleId 차량의 초대 화면")
                MemberInvitationOptionItem(
//                    icon = Icons.Default.Phone,
                    icon = "☎",
                    title = "전화번호로 초대하기",
                    description = "다른 회원의 전화번호로 초대할 수 있어요.",
                    onItemClick = {
                        onNavigateToInvitePhone()
                        viewModel.closeBottomSheet()
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
                MemberInvitationOptionItem(
//                    icon = ImageVector.vectorResource(id = R.drawable.baseline_radar_24),
                    icon = "\uD83E\uDE84",
                    title = "내 근처 회원 초대하기",
                    description = "초대받을 회원은 더보기 메뉴에서 초대 대기를 눌러주세요.",
                    onItemClick = {
                        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                        val bluetoothAdapter = bluetoothManager.adapter

                        viewModel.initBluetoothAdapter(bluetoothAdapter)
                        viewModel.openInvitationBLE(vehicleId)
                        onExpandClick()
                    }
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight()
            ) {
                MemberInvitationViaBLE(
                    memberInvitationRepository = memberInvitationRepository,
                    vehicleId = vehicleId,
                    viewModel = viewModel,
                    onNavigateToConfirmation = onNavigateToConfirmation
                )
            }
        }
    }
}
