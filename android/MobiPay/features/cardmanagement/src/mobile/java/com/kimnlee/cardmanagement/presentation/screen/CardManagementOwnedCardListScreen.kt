package com.kimnlee.cardmanagement.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kimnlee.common.R as CommonR
import com.kimnlee.cardmanagement.R as CardManagementR
import com.kimnlee.cardmanagement.data.model.OwnedCard
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.OwnedCardUiState
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack

// 소유한 카드 리스트에서 모비페이에 등록할 카드를 등록하는 페이지
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardManagementOwnedCardListScreen(
    viewModel: CardManagementViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRegistration: (List<OwnedCard>) -> Unit
) {
    val ownedCardUiState by viewModel.ownedCardUiState.collectAsState()
    var selectedCards by remember { mutableStateOf(setOf<OwnedCard>()) }
    var isAllSelected by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "💳",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily(Font(CommonR.font.emoji)),
                            fontSize = 24.sp,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "카드 등록",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
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
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)) {

            when (val state = ownedCardUiState) {
                is OwnedCardUiState.Loading -> LoadingState()
                is OwnedCardUiState.Success -> {
                    val selectableCards = state.cards.filter { !viewModel.isCardRegistered(it.id) }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Checkbox(
                            checked = isAllSelected,
                            onCheckedChange = { isChecked ->
                                isAllSelected = isChecked
                                selectedCards = if (isChecked) selectableCards.toSet() else emptySet()
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF3182F6))
                        )
                        Text("전체 선택", style = MaterialTheme.typography.bodyMedium)
                    }

                    CardList(
                        cards = state.cards,
                        selectedCards = selectedCards,
                        onCardSelected = { card, isSelected ->
                            if (!viewModel.isCardRegistered(card.id)) {
                                selectedCards = if (isSelected) selectedCards + card else selectedCards - card
                                isAllSelected = selectedCards.size == selectableCards.size
                            }
                        },
                        isCardRegistered = { viewModel.isCardRegistered(it.id) }
                    )
                }
                is OwnedCardUiState.Error -> {
                    // 에러 상태에서는 아무것도 표시하지 않음
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { onNavigateToRegistration(selectedCards.toList()) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = selectedCards.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3182F6)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("등록하기", color = Color.White)
            }
        }
    }
}

@Composable
fun TopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Text(
            text = "카드 등록",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SelectionButtons(onSelectAll: () -> Unit, onDeselectAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = onSelectAll,
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
            shape = RectangleShape,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text("전체 선택", color = Color.Black)
        }
        Button(
            onClick = onDeselectAll,
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
            shape = RectangleShape
        ) {
            Text("전체 선택 해제", color = Color.Black)
        }
    }
}

@Composable
fun CardList(
    cards: List<OwnedCard>,
    selectedCards: Set<OwnedCard>,
    onCardSelected: (OwnedCard, Boolean) -> Unit,
    isCardRegistered: (OwnedCard) -> Boolean
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(cards) { card ->
            CardItem(
                card = card,
                isSelected = card in selectedCards,
                onSelected = { isSelected -> onCardSelected(card, isSelected) },
                isRegistered = isCardRegistered(card)
            )
        }
    }
}

@Composable
fun CardItem(
    card: OwnedCard,
    isSelected: Boolean,
    onSelected: (Boolean) -> Unit,
    isRegistered: Boolean
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        if (isRegistered) {
            Text(
                "이미 등록되었어요",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp),
                color = Color.Red
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = findCardCompany(card.cardNo)),
                    contentDescription = "Card Image",
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .alpha(if (isRegistered) 0.5f else 1f)
                ) {
                    Text(text = maskCardNumber(card.cardNo), fontWeight = FontWeight.Bold)
                    Text(text = "만료일: ${formatExpiryDate(card.cardExpiryDate)}")
                }
                if (!isRegistered) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = onSelected,
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF3182F6))
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF3182F6))
    }
}

fun findCardCompany(cardNumber: String): Int {
    val company = cardNumber.take(4)
    return when (company) {
        "1001" -> CardManagementR.drawable.kb_only_you_titanium
        "1002" -> CardManagementR.drawable.s_taptap
        "1003" -> CardManagementR.drawable.l_im_driving
        "1004" -> CardManagementR.drawable.w_inyou
        "1006" -> CardManagementR.drawable.h_energy_plus_edition3
        "1007" -> CardManagementR.drawable.bc_baro
        "1008" -> CardManagementR.drawable.nh_zgm_point
        "1009" -> CardManagementR.drawable.ha_enery_double
        "1010" -> CardManagementR.drawable.ibk_daily_happy
        else -> CardManagementR.drawable.card_example
    }
}

fun maskCardNumber(cardNumber: String): String {
    val visiblePart = cardNumber.take(6)
    val maskedPart = "******"
    val lastFour = cardNumber.takeLast(4)
    return "$visiblePart$maskedPart$lastFour"
}

fun formatExpiryDate(date: String): String {
    return "${date.substring(0, 4)}/${date.substring(4, 6)}/${date.substring(6, 8)}"
}