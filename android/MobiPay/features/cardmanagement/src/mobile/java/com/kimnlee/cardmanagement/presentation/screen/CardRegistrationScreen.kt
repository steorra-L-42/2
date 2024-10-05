package com.kimnlee.cardmanagement.presentation.screen

import com.kimnlee.common.utils.MoneyFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kimnlee.common.R
import com.kimnlee.cardmanagement.data.model.CardInfo
import com.kimnlee.cardmanagement.data.model.RegisterCardRequest
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.ui.theme.MobiTextDarkGray
import java.math.BigInteger
import com.kimnlee.common.utils.moneyFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardRegistrationScreen(
    viewModel: CardManagementViewModel,
    cardInfos: List<CardInfo>,
    onNavigateBack: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { cardInfos.size })
    val focusManager = LocalFocusManager.current

    var oneDayLimits by remember { mutableStateOf(List(cardInfos.size) { "" }) }
    var oneTimeLimits by remember { mutableStateOf(List(cardInfos.size) { "" }) }

    var oneDayLimitErrors by remember { mutableStateOf(List(cardInfos.size) { "" }) }
    var oneTimeLimitErrors by remember { mutableStateOf(List(cardInfos.size) { "" }) }

    var isApplyToAll by remember { mutableStateOf(false) }

    val isAnyLimitExceeded = oneDayLimitErrors.any { it.isNotEmpty() } || oneTimeLimitErrors.any { it.isNotEmpty() }

    val oneDayLimitFocusRequester = remember { FocusRequester() }
    val oneTimeLimitFocusRequester = remember { FocusRequester() }

    fun formatMoney(input: String): String {
        return try {
            val amount = BigInteger(input.replace(Regex("[^0-9]"), ""))
            moneyFormat(amount)
        } catch (e: NumberFormatException) {
            ""
        }
    }

    fun applyToAllCards(oneDayValue: String, oneTimeValue: String) {
        oneDayLimits = List(cardInfos.size) { oneDayValue }
        oneTimeLimits = List(cardInfos.size) { oneTimeValue }
        val errors = cardInfos.indices.map { validateLimits(oneDayValue, oneTimeValue) }
        oneDayLimitErrors = errors.map { it.first }
        oneTimeLimitErrors = errors.map { it.second }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "💳",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily(Font(R.font.emoji)),
                            modifier = Modifier.padding(top = 8.dp, end = 8.dp)
                        )
                        Text(
                            text = "카드 등록",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
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
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.titleMedium,
                    color = MobiTextAlmostBlack
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (cardInfos.size > 1) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Checkbox(
                        checked = isApplyToAll,
                        onCheckedChange = { newValue ->
                            isApplyToAll = newValue
                            if (newValue) {
                                val currentPage = pagerState.currentPage
                                applyToAllCards(oneDayLimits[currentPage], oneTimeLimits[currentPage])
                            }
                        },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF3182F6))
                    )
                    Text(
                        text = "한도 일괄 적용",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily(Font(R.font.pbold)),
                        color = MobiTextAlmostBlack
                    )
                }
            }

            OutlinedTextField(
                value = oneDayLimits[pagerState.currentPage],
                onValueChange = { newValue ->
                    val filteredValue = newValue.filter { it.isDigit() }
                    if (isApplyToAll) {
                        applyToAllCards(filteredValue, oneTimeLimits[pagerState.currentPage])
                    } else {
                        oneDayLimits = oneDayLimits.toMutableList().apply { this[pagerState.currentPage] = filteredValue }
                        val (oneDayError, oneTimeError) = validateLimits(filteredValue, oneTimeLimits[pagerState.currentPage])
                        oneDayLimitErrors = oneDayLimitErrors.toMutableList().apply { this[pagerState.currentPage] = oneDayError }
                        oneTimeLimitErrors = oneTimeLimitErrors.toMutableList().apply { this[pagerState.currentPage] = oneTimeError }
                    }
                },
                label = {
                    Text(
                        text = "1일 결제 한도",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MobiTextDarkGray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(oneDayLimitFocusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { oneTimeLimitFocusRequester.requestFocus() }),
                isError = oneDayLimitErrors[pagerState.currentPage].isNotEmpty(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray
                ),
                visualTransformation = MoneyFormat(),
            )

            if (oneDayLimitErrors[pagerState.currentPage].isNotEmpty()) {
                Text(oneDayLimitErrors[pagerState.currentPage], color = Color.Red)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = oneTimeLimits[pagerState.currentPage],
                onValueChange = { newValue ->
                    val filteredValue = newValue.filter { it.isDigit() }
                    if (isApplyToAll) {
                        applyToAllCards(oneDayLimits[pagerState.currentPage], filteredValue)
                    } else {
                        oneTimeLimits = oneTimeLimits.toMutableList().apply { this[pagerState.currentPage] = filteredValue }
                        val (oneDayError, oneTimeError) = validateLimits(oneDayLimits[pagerState.currentPage], filteredValue)
                        oneDayLimitErrors = oneDayLimitErrors.toMutableList().apply { this[pagerState.currentPage] = oneDayError }
                        oneTimeLimitErrors = oneTimeLimitErrors.toMutableList().apply { this[pagerState.currentPage] = oneTimeError }
                    }
                },
                label = { Text(
                    text = "1회 결제 한도",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MobiTextDarkGray
                ) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(oneTimeLimitFocusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                isError = oneTimeLimitErrors[pagerState.currentPage].isNotEmpty(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray
                ),
                visualTransformation = MoneyFormat()
            )
            if (oneTimeLimitErrors[pagerState.currentPage].isNotEmpty()) {
                Text(oneTimeLimitErrors[pagerState.currentPage], color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val cardsToRegister = cardInfos.mapIndexed { index, cardInfo ->
                        RegisterCardRequest(
                            ownedCardId = cardInfo.cardId,
                            oneDayLimit = oneDayLimits[index].toIntOrNull() ?: 0,
                            oneTimeLimit = oneTimeLimits[index].toIntOrNull() ?: 0,
                            password = "123" // 임시로 비밀번호 적용, 나중에 없어짐
                        )
                    }
                    viewModel.registerCards(cardsToRegister)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = oneDayLimits.all { it.isNotEmpty() } &&
                        oneTimeLimits.all { it.isNotEmpty() } &&
                        !isAnyLimitExceeded,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3182F6)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "등록하기",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }
    }

    LaunchedEffect(pagerState.currentPage, isApplyToAll) {
        if (isApplyToAll) {
            applyToAllCards(oneDayLimits[pagerState.currentPage], oneTimeLimits[pagerState.currentPage])
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
    val oneDayValue = oneDayLimit.toLongOrNull() ?: 0L
    val oneTimeValue = oneTimeLimit.toLongOrNull() ?: 0L

    val oneDayError = when {
        oneDayValue > 10_000_000L -> "1일 결제 한도는 1,000만원을 초과할 수 없어요."
        else -> ""
    }

    val oneTimeError = when {
        oneTimeValue > 1_000_000L -> "1회 결제 한도는 100만원을 초과할 수 없어요."
        oneTimeValue > oneDayValue && oneDayValue != 0L -> "1회 결제 한도는 1일 결제 한도를 초과할 수 없어요."
        else -> ""
    }

    return Pair(oneDayError, oneTimeError)
}