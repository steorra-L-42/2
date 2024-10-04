package com.kimnlee.cardmanagement.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kimnlee.cardmanagement.data.model.CardInfo
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import kotlinx.coroutines.launch

@Composable
fun CardRegistrationScreen(
    viewModel: CardManagementViewModel,
    cardInfos: List<CardInfo>,
    onNavigateBack: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { cardInfos.size })
    val coroutineScope = rememberCoroutineScope()

    var oneDayLimits by remember { mutableStateOf(List(cardInfos.size) { "" }) }
    var oneTimeLimits by remember { mutableStateOf(List(cardInfos.size) { "" }) }
    var password by remember { mutableStateOf("") }

    var oneDayLimitErrors by remember { mutableStateOf(List(cardInfos.size) { "" }) }
    var oneTimeLimitErrors by remember { mutableStateOf(List(cardInfos.size) { "" }) }

    val isAnyLimitExceeded = oneDayLimitErrors.any { it.isNotEmpty() } || oneTimeLimitErrors.any { it.isNotEmpty() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("카드 등록", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(0.5f)
                .fillMaxWidth(),
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 48.dp)
        ) { page ->
            CardImage(cardInfo = cardInfos[page])
        }

        if (cardInfos.size > 1) {
            Text(
                text = "${pagerState.currentPage + 1}/${cardInfos.size}",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input fields
        OutlinedTextField(
            value = oneDayLimits[pagerState.currentPage],
            onValueChange = {
                val newValue = it.filter { char -> char.isDigit() }
                oneDayLimits = oneDayLimits.toMutableList().apply { this[pagerState.currentPage] = newValue }
                val (oneDayError, oneTimeError) = validateLimits(newValue, oneTimeLimits[pagerState.currentPage])
                oneDayLimitErrors = oneDayLimitErrors.toMutableList().apply { this[pagerState.currentPage] = oneDayError }
                oneTimeLimitErrors = oneTimeLimitErrors.toMutableList().apply { this[pagerState.currentPage] = oneTimeError }
            },
            label = { Text("일일 결제 한도") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = oneDayLimitErrors[pagerState.currentPage].isNotEmpty()
        )
        if (oneDayLimitErrors[pagerState.currentPage].isNotEmpty()) {
            Text(oneDayLimitErrors[pagerState.currentPage], color = Color.Red)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = oneTimeLimits[pagerState.currentPage],
            onValueChange = {
                val newValue = it.filter { char -> char.isDigit() }
                oneTimeLimits = oneTimeLimits.toMutableList().apply { this[pagerState.currentPage] = newValue }
                val (oneDayError, oneTimeError) = validateLimits(oneDayLimits[pagerState.currentPage], newValue)
                oneDayLimitErrors = oneDayLimitErrors.toMutableList().apply { this[pagerState.currentPage] = oneDayError }
                oneTimeLimitErrors = oneTimeLimitErrors.toMutableList().apply { this[pagerState.currentPage] = oneTimeError }
            },
            label = { Text("1회 결제 한도") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = oneTimeLimitErrors[pagerState.currentPage].isNotEmpty()
        )
        if (oneTimeLimitErrors[pagerState.currentPage].isNotEmpty()) {
            Text(oneTimeLimitErrors[pagerState.currentPage], color = Color.Red)
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
                    password.isNotEmpty() &&
                    !isAnyLimitExceeded,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3182F6))
        ) {
            Text("등록하기", color = Color.White)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            coroutineScope.launch {
                pagerState.animateScrollToPage(page)
            }
        }
    }
}

@Composable
fun CardImage(cardInfo: CardInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.6f),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Image(
            painter = painterResource(id = findCardCompany(cardInfo.cardNo)),
            contentDescription = "Card Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Text(
            text = maskCardNumber(cardInfo.cardNo),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}

private fun validateLimits(oneDayLimit: String, oneTimeLimit: String): Pair<String, String> {
    val oneDayValue = oneDayLimit.toIntOrNull() ?: 0
    val oneTimeValue = oneTimeLimit.toIntOrNull() ?: 0

    val oneDayError = when {
        oneDayValue > 10000000 -> "일일 결제 한도는 천만원을 초과할 수 없어요."
        else -> ""
    }

    val oneTimeError = when {
        oneTimeValue > 1000000 -> "1회 결제 한도는 백만원을 초과할 수 없어요."
        oneTimeValue > oneDayValue && oneDayValue != 0 -> "1회 결제 한도는 일일 결제 한도를 초과할 수 없어요."
        else -> ""
    }

    return Pair(oneDayError, oneTimeError)
}