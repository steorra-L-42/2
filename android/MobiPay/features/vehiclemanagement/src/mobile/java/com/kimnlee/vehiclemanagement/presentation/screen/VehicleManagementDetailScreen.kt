package com.kimnlee.vehiclemanagement.presentation.screen

import android.content.Context
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
    context : Context,
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

    val imageResId = when (vehicle?.carModel) {
        // Audi
        "A3" -> R.drawable.a3
        "A4" -> R.drawable.a4
        "A5" -> R.drawable.a5
        "A6" -> R.drawable.a6
        "A7" -> R.drawable.a7
        "A8" -> R.drawable.a8
        "Q2" -> R.drawable.q2
        "Q3" -> R.drawable.q3
        "Q5" -> R.drawable.q5
        "Q7" -> R.drawable.q7
        "Q8" -> R.drawable.q8
        // Mercedes-Benz
        "A-클래스" -> R.drawable.aclass
        "C-클래스" -> R.drawable.cclass
        "CLA" -> R.drawable.cla
        "CLE" -> R.drawable.cle
        "E-클래스" -> R.drawable.eclass
        "GLC" -> R.drawable.glc
        "GLE" -> R.drawable.gle
        "S-클래스" -> R.drawable.sclass
        // KGM
        "액티언" -> R.drawable.actyon
        "코란도" -> R.drawable.corando
        "렉스턴" -> R.drawable.rexton
        "티볼리" -> R.drawable.tivoli
        "토레스" -> R.drawable.torres
        // HYUNDAI
        "아반떼" -> R.drawable.avante
        "그랜저" -> R.drawable.grandeur
        "아이오닉5" -> R.drawable.ioniq5
        "아이오닉6" -> R.drawable.ioniq6
        "코나" -> R.drawable.kona
        "넥쏘" -> R.drawable.nexo
        "팰리세이드" -> R.drawable.palisade
        "싼타페" -> R.drawable.santafe
        "쏘나타" -> R.drawable.sonata
        "투싼" -> R.drawable.tucson
        "베뉴" -> R.drawable.venue
        // BMW
        "BMW3" -> R.drawable.bmw3
        "BMW5" -> R.drawable.bmw5
        "BMS7" -> R.drawable.bmw7
        "X3" -> R.drawable.x3
        "X5" -> R.drawable.x5
        "X6" -> R.drawable.x6
        // KIA
        "카니발" -> R.drawable.carnival
        "EV3" -> R.drawable.ev3
        "EV6" -> R.drawable.ev6
        "EV9" -> R.drawable.ev9
        "K5" -> R.drawable.k5
        "K8" -> R.drawable.k8
        "K9" -> R.drawable.k9
        "모하비" -> R.drawable.mohave
        "모닝" -> R.drawable.morning
        "니로" -> R.drawable.niro
        "니로EV" -> R.drawable.niroev
        "레이" -> R.drawable.ray
        "셀토스" -> R.drawable.seltos
        "쏘렌토" -> R.drawable.sorento
        "스포티지" -> R.drawable.sportage
        // CHEVROLET
        "콜로라도" -> R.drawable.colorado
        "이쿼녹스" -> R.drawable.equinox
        "임팔라" -> R.drawable.impala
        "말리부" -> R.drawable.malibu
        "스파크" -> R.drawable.spark
        "트레일블레이저" -> R.drawable.trailblazer
        "트랙스" -> R.drawable.trax
        // GENESIS
        "G70" -> R.drawable.g70
        "G80" -> R.drawable.g80
        "GV60" -> R.drawable.gv60
        "GV70" -> R.drawable.gv70
        "GV80" -> R.drawable.gv80
        // TESLA
        "모델3" -> R.drawable.model3
        "모델S" -> R.drawable.models
        "모델X" -> R.drawable.modelx
        "모델Y" -> R.drawable.modely
        // RENAULT
        "QM6" -> R.drawable.qm6
        "SM6" -> R.drawable.sm6
        "XM3" -> R.drawable.xm3
        else -> R.drawable.ghibli
    }

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
                    painter = painterResource(id = imageResId),
                    contentDescription = "Vehicle Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(180.dp)
                        .padding(24.dp),
                    contentScale = ContentScale.Fit
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
                context = context,
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