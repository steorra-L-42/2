package com.kimnlee.vehiclemanagement.presentation.screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kimnlee.cardmanagement.presentation.screen.findCardCompany
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.ui.theme.MobiBlue
import com.kimnlee.common.utils.CarModelImageProvider
import com.kimnlee.common.utils.formatCardNumber
import com.kimnlee.memberinvitation.presentation.components.MemberInvitationBottomSheet
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel
import com.kimnlee.vehiclemanagement.R
import com.kimnlee.vehiclemanagement.data.model.CarMember
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel

private const val TAG = "VehicleManagementDetail"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleManagementDetailScreen(
    context : Context,
    vehicleId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToInvitePhone: (Int) -> Unit,
    onNavigateToNotification: () -> Unit,
    viewModel: VehicleManagementViewModel,
    memberInvitationViewModel: MemberInvitationViewModel,
    cardManagementViewModel: CardManagementViewModel,
    navController: NavController
) {
    val vehicle = viewModel.getVehicleById(vehicleId)
    val carMembers by viewModel.carMembers.collectAsState()
    var isCardEnabled by remember { mutableStateOf(false) }
    val autoPaymentStatus by viewModel.autoPaymentStatus.collectAsState()
    val registeredCards by cardManagementViewModel.registeredCards.collectAsState()

    val showBottomSheet by memberInvitationViewModel.showBottomSheet.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val hasNewNotifications by viewModel.hasNewNotifications.collectAsState()

    if(vehicle?.carModel == null){
        Log.d(TAG, "VehicleManagementDetailScreen: 차량 모델 NULL! return 하겠음!!!!")
        return
    }
    val imageResId = CarModelImageProvider.getImageResId(vehicle?.carModel!!)

    LaunchedEffect(vehicleId) {
        viewModel.requestCarMembers(vehicleId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            Spacer(modifier = Modifier.height(40.dp))

            // 차량 이미지 추가
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Vehicle Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            vehicle?.let {
                TextOnLP(formatLicensePlate(it.number))
            }

            Spacer(modifier = Modifier.height(28.dp))

            CarMembersRow(
                carMembers = carMembers,
                onAddMember = { memberInvitationViewModel.openBottomSheet() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = autoPaymentStatus,
                    onCheckedChange = {
                        viewModel.toggleAutoPayment(vehicleId, it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MobiBlue,
                        uncheckedThumbColor = Color.DarkGray,
                        checkedTrackColor = Color.LightGray,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("내 카드로 자동결제")
            }

            if (autoPaymentStatus) {
                val autoPaymentCard = registeredCards.find { it.autoPayStatus }
                autoPaymentCard?.let { card ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(250.dp)  // 카드 높이를 늘림
                    ) {
                        Image(
                            painter = painterResource(id = findCardCompany(card.cardNo)),
                            contentDescription = "Card Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = formatCardNumber(card.cardNo),
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    shadow = Shadow(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        offset = Offset(2f, 2f),
                                        blurRadius = 4f
                                    )
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "1회 한도: ${card.oneTimeLimit}원",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    shadow = Shadow(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        offset = Offset(1f, 1f),
                                        blurRadius = 2f
                                    )
                                )
                            )
                        }
                    }
                } ?: Text("자동결제 카드가 설정되지 않았습니다.")
            }
        }


    if (showBottomSheet) {
            MemberInvitationBottomSheet(
                context = context,
                vehicleId = vehicleId,
                sheetState = sheetState,
                scope = scope,
                viewModel = memberInvitationViewModel,
                onNavigateToInvitePhone = { onNavigateToInvitePhone(vehicleId) },
                onNavigateToConfirmation = { navController.navigate("member_confirmation/$vehicleId") }
            )
        }

        IconButton(
            onClick = {
                onNavigateToNotification()
                viewModel.markNotificationsAsRead()
            },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Box {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "알림",
                    modifier = Modifier.size(30.dp)
                )
                if (hasNewNotifications) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.Red, shape = CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }
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
        carMembers.take(3).forEachIndexed { index, member ->
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Transparent)
            ) {
                AsyncImage(
                    model = member.picture,
                    contentDescription = member.name,
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (index == 0) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_crown),
                        contentDescription = "오너",
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopStart)
                            .offset(x = (-10).dp, y = (-10).dp)
                            .graphicsLayer(rotationZ = -45f)
                    )
                }
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