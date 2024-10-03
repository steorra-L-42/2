package com.kimnlee.auth.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kimnlee.auth.R
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.common.ui.theme.MobiBgGray

@Composable
fun PrivacyPolicyModal(
    viewModel: LoginViewModel,
    privacyText : String
) {
    Dialog(
        onDismissRequest = { viewModel.closePrivacyModal() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 770.dp),
            shape = MaterialTheme.shapes.medium,
            color = MobiBgGray,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 이용약관 제목
                Text(
                    text = "이용약관",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                // 스크롤 가능한 이용약관 텍스트
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text = privacyText, fontSize = 10.sp)
                }
            }
        }
    }
}