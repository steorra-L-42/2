package com.kimnlee.vehiclemanagement.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.navigation.NavController
import com.kimnlee.memberinvitation.presentation.components.MemberInvitationBottomSheet
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleManagementDetailScreen(
    vehicleId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToInvitePhone: (Int) -> Unit,
    onNavigateToNotification: () -> Unit,
    viewModel: VehicleManagementViewModel = viewModel(),
    navController: NavController
) {
    val vehicle = viewModel.getVehicleById(vehicleId)
    var isCardEnabled by remember { mutableStateOf(false) }

    val memberViewModel : MemberInvitationViewModel = viewModel()
    val showBottomSheet by memberViewModel.showBottomSheet.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            vehicle?.let {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.background, // 흰색 배경
                        contentColor = MaterialTheme.colorScheme.onBackground // 검은색 텍스트
                    ),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("차량 선택")
                }
                Image(
                    painter = painterResource(id = it.imageResId),
                    contentDescription = "Vehicle Image",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 16.dp)
                )
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.headlineSmall
                )
            } ?: Text("차량을 찾을 수 없습니다.")

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("멤버들의 프로필 이미지 공간") //추후 프로필 이미지를 넣을 공간(더하기 버튼을 통해 멤버추가 버튼도 만들어야함(멤버추가 모듈로 넘어가게 할듯))
            }

            Button(
                onClick = {
//                    onNavigateToMemberInvitation(vehicleId)
                    memberViewModel.openBottomSheet()
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("멤버 초대")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = isCardEnabled,
                    onCheckedChange = { isCardEnabled = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("내 카드로 자동결제")
            }

            if (isCardEnabled) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(50.dp)
                ) {
                    Text("카드 이미지 공간", modifier = Modifier.align(Alignment.Center))
                }
                Button(onClick = { /*등록된 카드 목록 띄우기(페이지 및 API 기능 구현 필요)*/ }) {
                    Text("바꾸기")
                }
            }
        }
        if (showBottomSheet) {
            MemberInvitationBottomSheet(
                vehicleId = vehicleId,
                sheetState = sheetState,
                scope = scope,
                viewModel = memberViewModel,
                onNavigateToInvitePhone = {onNavigateToInvitePhone(vehicleId)},
                onNavigateToConfirmation = {navController.navigate("member_confirmation/$vehicleId")}
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        IconButton(
            onClick = onNavigateToNotification,
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "알림",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}