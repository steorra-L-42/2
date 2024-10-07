package com.kimnlee.cardmanagement.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.MyDataAgreementState

@Composable
fun MyDataAgreementScreen(
    viewModel: CardManagementViewModel,
    onNavigateToOwnedCards: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var isAgreed by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "마이데이터 약관 동의",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            "금융사 연결을 위해 마이데이터 서비스 이용 약관에 동의해주세요.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Checkbox(
            checked = isAgreed,
            onCheckedChange = { isAgreed = it },
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF3182F6))
        )

        Text(
            "마이데이터 서비스 이용 약관에 동의합니다.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (isAgreed) {
                    showLoading = true
                    viewModel.setMyDataAgreement()
                }
            },
            enabled = isAgreed,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3182F6))
        ) {
            Text("동의하고 계속하기")
        }
    }

    LaunchedEffect(viewModel.myDataAgreementState) {
        when (val state = viewModel.myDataAgreementState.value) {
            is MyDataAgreementState.Success -> {
                showLoading = false
                onNavigateToOwnedCards()
            }
            is MyDataAgreementState.Error -> {
                showLoading = false
                Toast.makeText(context, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
                onNavigateBack()
            }
            else -> {} // Initial 상태 처리
        }
    }

    if (showLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}