package com.kimnlee.vehiclemanagement.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kimnlee.memberinvitation.presentation.components.MemberInvitationBottomSheet
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import com.kimnlee.vehiclemanagement.R
import com.kimnlee.vehiclemanagement.data.model.CarMember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleManagementDetailScreen(
    vehicleId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToInvitePhone: (Int) -> Unit,
    onNavigateToNotification: () -> Unit,
    viewModel: VehicleManagementViewModel,
    navController: NavController
) {
    val vehicle = viewModel.getVehicleById(vehicleId)
    val carMembers by viewModel.carMembers.collectAsState()
    var isCardEnabled by remember { mutableStateOf(false) }

    val memberViewModel: MemberInvitationViewModel = viewModel()
    val showBottomSheet by memberViewModel.showBottomSheet.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(vehicleId) {
        viewModel.requestCarMembers(vehicleId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground
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

                TextOnLP(formatLicensePlate(it.number))

                Spacer(modifier = Modifier.height(28.dp))

                CarMembersRow(
                    carMembers = carMembers,
                    onAddMember = { memberViewModel.openBottomSheet() }
                )

            } ?: Text("차량을 찾을 수 없습니다.")

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
                onNavigateToInvitePhone = { onNavigateToInvitePhone(vehicleId) },
                onNavigateToConfirmation = { navController.navigate("member_confirmation/$vehicleId") }
            )
        }

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

@Composable
fun CarMembersRow(
    carMembers: List<CarMember>,
    onAddMember: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        carMembers.take(3).forEach { member ->
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model = member.picture,
                    contentDescription = member.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (carMembers.size > 3) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+${carMembers.size - 3}",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEEEEE))
                .clickable(onClick = onAddMember),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add member",
                tint = Color.Black
            )
        }
    }
}

fun formatLicensePlate(number: String): String {
    return number.reversed().chunked(4)
        .joinToString(" ")
        .reversed()
}