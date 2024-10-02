package com.kimnlee.cardmanagement.presentation.screen

import OwnedCard
import RegisterCardRequest
import android.widget.EditText
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kimnlee.cardmanagement.R
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.OwnedCardUiState
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.round
import kotlin.random.Random
import kotlin.reflect.typeOf

// 소유한 카드 리스트에서 모비페이에 등록할 카드를 등록하는 페이지
@Composable
fun CardManagementRegistrationOwnedCardScreen(
    viewModel: CardManagementViewModel = viewModel(),
    onNavigateToDetail: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val ownedCardUiState by viewModel.ownedCardUiState.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    var cardNum by remember { mutableStateOf(0) }
    val randomIndex = Random.nextInt(1, 4)
    var oneDayLimit by remember { mutableStateOf("") }
    var oneTimeLimit by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var autoPayStatus by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { onNavigateBack() }) {
                Text(text = "뒤로 가기")
            }
        }
        Text(
            text = "내가 소유한 $cardNum 개의 카드를 찾았어요",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.padding(16.dp))

        when (val state = ownedCardUiState) {
            is OwnedCardUiState.Loading -> {
                CircularProgressIndicator()
            }

            is OwnedCardUiState.Success -> {
                if (state.cards.isEmpty()) {
                    Text("등록된 카드가 없습니다.")
                } else {
                    cardNum = state.cards.size
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(state.cards) { card ->
                            Text(text = card.id.toString(), )
                            OwnedCardItem(
                                card,
                                { viewModel.openDialog(card.cardNo) },
                                painterResource(id = findCardCompany(card.cardNo, randomIndex)),
                                "${card.mobiUserId}님의 카드"
                            )
                            if (showDialog) {
                                AlertDialog(
                                    onDismissRequest = { viewModel.closeDialog() },
                                    title = { Text("카드 정보") },
                                    text = {
                                        Column {
                                            OutlinedTextField(
                                                value = oneDayLimit,
                                                onValueChange = { oneDayLimit = it },
                                                label = { Text("일일 한도") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            OutlinedTextField(
                                                value = oneTimeLimit,
                                                onValueChange = { oneTimeLimit = it },
                                                label = { Text("1회 한도") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            OutlinedTextField(
                                                value = password,
                                                onValueChange = { password = it },
                                                label = { Text("비밀번호") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("자동 결제 상태", modifier = Modifier.weight(1f))
                                                Switch(
                                                    checked = autoPayStatus,
                                                    onCheckedChange = { autoPayStatus = it }
                                                )
                                            }
                                        }
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                card?.let {
                                                    viewModel.registerCard(
                                                        it.id.toLong(),
                                                        oneDayLimit.toIntOrNull() ?: 0,
                                                        oneTimeLimit.toIntOrNull() ?: 0,
                                                        password,
                                                        autoPayStatus
                                                    )
                                                }
                                                viewModel.closeDialog()
                                                onNavigateBack()
                                            },
                                            enabled = oneDayLimit.isNotEmpty() && oneTimeLimit.isNotEmpty() && password.isNotEmpty(),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("카드 등록")
                                        }
                                        Button(
                                            onClick = {
                                                viewModel.closeDialog()
                                            }
                                        ) {
                                            Text("취소")
                                        }
                                    }
                                )
                            }
                        }
                    }
                    Text(text = viewModel.registrationStatus.collectAsState().toString())
                }
            }

            is OwnedCardUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Button(onClick = { viewModel.requestOwnedCards() }) {
                    Text("다시 시도")
                }
            }
        }
    }
}

@Composable
fun OwnedCardItem(
    card: OwnedCard,
    onNavigateToDetail: () -> Unit,
    painter: Painter,
    contentDescription: String,
) {
    var imageWidth by remember { mutableStateOf(0) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onNavigateToDetail),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        // 이미지의 너비를 얻어 imageWidth에 저장
                        imageWidth = coordinates.size.width
                    },
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedText(
                    text = maskCardNumber(card.cardNo),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        OutlinedText("만료일")
                        OutlinedText(formatExpiryDate(card.cardExpiryDate))
                    }
                }
            }
        }
    }
}

@Composable
fun OutlinedText(text: String, fontSize: TextUnit = 16.sp) {
    Box(
        modifier = Modifier
            .wrapContentSize()
    ) {
        // 테두리 (검은색)
        Text(
            text = text,
            fontSize = fontSize,
            color = Color.Black,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = fontSize,
                drawStyle = Stroke(
                    width = 4f // 테두리 두께
                )
            )
        )
//        Text(
//            text,
////            modifier = Modifier
////                .background(Color(0x21130d00)),
//            style = MaterialTheme.typography.headlineMedium.copy(shadow = Shadow(Color(0x21130d00)))
//        )
        // 글씨 (흰색)
        Text(
            text = text,
            fontSize = fontSize,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = fontSize
            )
        )

    }
}

fun maskCardNumber(cardNumber: String): String {
    val visiblePart = cardNumber.take(cardNumber.length - 4)
    val maskedPart = "****"
    return (visiblePart + maskedPart)
        .chunked(4)
        .joinToString(" ")
}

fun formatExpiryDate(date: String): String {
    return "${date.substring(0, 4)} ${date.substring(4, 6)} ${date.substring(6, 8)}"
}

fun findCardCompany(cardNumber: String, randomIndex: Int): Int {
    val company = cardNumber.take(4)
    var resource: Int = R.drawable.card_example
    when (company) {
        "1001" -> resource =
            getCompanyImageResource("kb", randomIndex) //'KB국민카드'
        "1002" -> resource = getCompanyImageResource("s", randomIndex) // 삼성
        "1003" -> resource = getCompanyImageResource("l", randomIndex) // 롯데
        "1004" -> resource = getCompanyImageResource("w", randomIndex) // 우리
        "1006" -> resource = getCompanyImageResource("h", randomIndex) // 현대
        "1007" -> resource =
            getCompanyImageResource("bc", randomIndex) // BC
        "1008" -> resource =
            getCompanyImageResource("nh", randomIndex) // NH
        "1009" -> resource =
            getCompanyImageResource("ha", randomIndex) // 하나
        "1010" -> resource =
            getCompanyImageResource("ibk", randomIndex) // IBK
    }
    return resource
}

fun getCompanyImageResource(prefix: String, index: Int): Int {
    val resourceName = "${prefix}_$index"
    return when (resourceName) {
        "bc_1" -> R.drawable.bc_baro
        "bc_2" -> R.drawable.bc_green
        "bc_3" -> R.drawable.bc_kully
        "kb_1" -> R.drawable.kb_dadam
        "kb_2" -> R.drawable.kb_easy_auto
        "kb_3" -> R.drawable.kb_only_you_titanium
        "h_1" -> R.drawable.h_energy_plus_edition3
        "h_2" -> R.drawable.h_kia_members
        "h_3" -> R.drawable.h_my_business_x_retail
        "ha_1" -> R.drawable.ha_enery_double
        "ha_2" -> R.drawable.ha_travlog
        "ha_3" -> R.drawable.ha_multi_living
        "ibk_1" -> R.drawable.ibk_k_pass
        "ibk_2" -> R.drawable.ibk_oil_life
        "ibk_3" -> R.drawable.ibk_daily_happy
        "s_1" -> R.drawable.s_minimo
        "s_2" -> R.drawable.s_taptap
        "s_3" -> R.drawable.s_taptap_drive
        "l_1" -> R.drawable.l_im_driving
        "l_2" -> R.drawable.l_loca_for_auto
        "l_3" -> R.drawable.l_loca_likit_play
        "w_1" -> R.drawable.w_inyou
        "w_2" -> R.drawable.w_highpass
        "w_3" -> R.drawable.w_every_mile_skypass
        "nh_1" -> R.drawable.nh_allbalmn
        "nh_2" -> R.drawable.nh_k_pass
        "nh_3" -> R.drawable.nh_zgm_point
        // 다른 회사 이미지 처리 추가
        else -> R.drawable.card_example
    }
}