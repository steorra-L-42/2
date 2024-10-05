package com.kimnlee.memberinvitation.presentation.screen

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.kimnlee.common.FCMData
import com.kimnlee.common.FCMDataForInvitation
import com.kimnlee.common.FCMDependencyProvider
import com.kimnlee.common.MemberInvitationOperations
import com.kimnlee.common.R
import com.kimnlee.common.utils.CarModelImageProvider
import com.kimnlee.memberinvitation.data.repository.MemberInvitationRepository
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel

private const val TAG = "InvitedScreen"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitedScreen(
    memberInvitationViewModel: MemberInvitationViewModel = viewModel(),
    navController: NavController
) {

    val context = LocalContext.current
    val fcmDataForInvitationJson = (context as? Activity)?.intent?.getStringExtra("fcmDataForInvitation")

    val fcmDataForInvitation = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<FCMDataForInvitation>("invitationData")

//    val fcmDataForInvitationExtra = fcmDataForInvitationJson?.let { Gson().fromJson(it, FCMDataForInvitation::class.java) }
//
//    if (fcmDataForInvitationExtra == null || isAnyFieldNull(fcmDataForInvitationExtra)){
//        return
//    }

    val memberInvitationRepository = (context.applicationContext as FCMDependencyProvider).memberInvitationOperations

    if (fcmDataForInvitation == null || isAnyFieldNull(fcmDataForInvitation)) {
        Log.d(TAG, "InvitedScreen: NULL 발견되어 종료!! $fcmDataForInvitation")
        return
    }else{
        Log.d(TAG, "InvitedScreen: NULL 아니므로 화면 표시!")
    }

    val imageResId = CarModelImageProvider.getImageResId(fcmDataForInvitation.carModel!!)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("초대 되었습니다") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.Close, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                    titleContentColor = TextColor
                )
            )
        },
        containerColor = BackgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
//            SingleInvitationCard(fcmDataForInvitationExtra, imageResId)
            SingleInvitationCard(navController, fcmDataForInvitation!!, imageResId, memberInvitationRepository)
        }
    }
}

@Composable
fun SingleInvitationCard(navController: NavController, fcmDataForInvitation: FCMDataForInvitation, imageResId: Int, memberInvitationRepository: MemberInvitationOperations) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "차량 이미지",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .padding(vertical = 24.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "차량 멤버로 초대되었어요",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                formatLicensePlate(fcmDataForInvitation.carNumber!!),
                fontSize = 18.sp,
                color = TextColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "차량의 멤버로 초대되었습니다.\n수락하시겠습니까?",
                fontSize = 16.sp,
                color = SecondaryTextColor,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("거절", color = TextColor)
            }
            Button(
                onClick = {
                                navController.popBackStack()
                                // 초대 수락 로직
                                memberInvitationRepository.acceptInvitation(fcmDataForInvitation.invitationId!!)

                          },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("수락", color = Color.White)
            }
        }
    }
}

fun formatLicensePlate(number: String): String {
    return number.reversed().chunked(4)
        .joinToString(" ")
        .reversed()
}

private fun isAnyFieldNull(fcmData: FCMDataForInvitation): Boolean {
    return fcmData.type == null ||
            fcmData.title == null ||
            fcmData.body == null ||
            fcmData.invitationId == null ||
            fcmData.created == null ||
            fcmData.inviterName == null ||
            fcmData.inviterPicture == null ||
            fcmData.carNumber == null ||
            fcmData.carModel == null
}