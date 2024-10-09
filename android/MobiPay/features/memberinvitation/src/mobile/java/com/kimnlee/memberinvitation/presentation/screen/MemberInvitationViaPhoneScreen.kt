package com.kimnlee.memberinvitation.presentation.screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kimnlee.common.FCMDependencyProvider
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.ui.theme.MobiTextDarkGray
import com.kimnlee.common.utils.MobiNotificationManager
import com.kimnlee.memberinvitation.data.api.MemberInvitationApiService
import com.kimnlee.memberinvitation.data.repository.MemberInvitationRepository
import com.kimnlee.memberinvitation.presentation.viewmodel.MemberInvitationViewModel

private const val TAG = "MemberInvitationViaPhon"
@Composable
fun MemberInvitationViaPhoneScreen(
    vehicleId: Int,
    onNavigateBack: () -> Unit,
    context: Context,
    viewModel: MemberInvitationViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()

    val apiClient = (context as? FCMDependencyProvider)?.apiClient
    var memberInvitationRepository : MemberInvitationRepository? = null
    if(apiClient != null){
        val memberInvitationApiService = apiClient.authenticatedApi.create(
            MemberInvitationApiService::class.java)
        val mobiNotificationManager = MobiNotificationManager.getInstance(context)
        memberInvitationRepository = MemberInvitationRepository(memberInvitationApiService, mobiNotificationManager, context, viewModel)
    }


    MobiPayTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
//                .imePadding()
                .windowInsetsPadding(WindowInsets.ime)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 2.dp)
            ) {
                Text(
                    text = "\uD83D\uDCEE",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily(Font(R.font.emoji)),
                    fontSize = 28.sp,
                    modifier = Modifier
                        .padding(top = 3.dp)
                )
                Text(
                    text = "  전화번호로 초대하기",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MobiTextAlmostBlack,
                    fontSize = 28.sp,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "초대받을 회원의 전화번호를 입력해서 초대하면 모비페이를 함께 사용할 수 있어요!",
                color = MobiTextDarkGray,
                fontSize = 17.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { viewModel.updatePhoneNumber(it) },
                label = { Text("전화번호 입력하기") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Gray,
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray,
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(100.dp))
//            Spacer(modifier = Modifier.weight(0.5f))

            Button(
                onClick = {
                    if (memberInvitationRepository != null) {
                        memberInvitationRepository.sendInvitationPhone(phoneNumber, vehicleId, onNavigateBack)
                    } else {
                        Log.d(TAG, "MemberInvitationViaPhoneScreen: memberInvitationRepository가 null이네.")
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3182F6)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "초대하기",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "뒤로가기",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }
    }
}