package com.kimnlee.cardmanagement.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kimnlee.cardmanagement.data.model.CardInfo
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel

@Composable
fun CardRegistrationScreen(
    viewModel: CardManagementViewModel,
    cardInfos: List<CardInfo>,
    onNavigateBack: () -> Unit
) {
    var oneDayLimits by remember { mutableStateOf(List(cardInfos.size) { "" }) }
    var oneTimeLimits by remember { mutableStateOf(List(cardInfos.size) { "" }) }
    var password by remember { mutableStateOf("") }
    val pagerState = rememberPagerState(pageCount = { cardInfos.size })

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("카드 등록", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            CardRegistrationItem(
                cardInfo = cardInfos[page],
                oneDayLimit = oneDayLimits[page],
                oneTimeLimit = oneTimeLimits[page],
                onOneDayLimitChange = { newValue ->
                    oneDayLimits = oneDayLimits.toMutableList().apply { this[page] = newValue }
                },
                onOneTimeLimitChange = { newValue ->
                    oneTimeLimits = oneTimeLimits.toMutableList().apply { this[page] = newValue }
                }
            )
        }

        if (cardInfos.size > 1) {
            Text(
                text = "${pagerState.currentPage + 1}/${cardInfos.size}",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                cardInfos.forEachIndexed { index, cardInfo ->
                    viewModel.registerCard(
                        ownedCardId = cardInfo.cardId,
                        oneDayLimit = oneDayLimits[index].toIntOrNull() ?: 0,
                        oneTimeLimit = oneTimeLimits[index].toIntOrNull() ?: 0,
                        password = password
                    )
                }
                onNavigateBack()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = oneDayLimits.all { it.isNotEmpty() } &&
                    oneTimeLimits.all { it.isNotEmpty() } &&
                    password.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3182F6))
        ) {
            Text("등록하기", color = Color.White)
        }
    }
}

@Composable
fun CardRegistrationItem(
    cardInfo: CardInfo,
    oneDayLimit: String,
    oneTimeLimit: String,
    onOneDayLimitChange: (String) -> Unit,
    onOneTimeLimitChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = findCardCompany(cardInfo.cardNo)),
            contentDescription = "Card Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = maskCardNumber(cardInfo.cardNo), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = oneDayLimit,
            onValueChange = onOneDayLimitChange,
            label = { Text("일일 결제 한도") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = oneTimeLimit,
            onValueChange = onOneTimeLimitChange,
            label = { Text("1회 결제 한도") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}