package com.kimnlee.cardmanagement.presentation.screen

import OwnedCard
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kimnlee.cardmanagement.R
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.OwnedCardUiState

@Composable
fun CardManagementRegistrationOwnedCardScreen(
    viewModel: CardManagementViewModel = viewModel(),
    onNavigateToDetail: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val ownedCardUiState by viewModel.ownedCardUiState.collectAsState()

    var cardNum by remember { mutableStateOf(0) }
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
                            OwnedCardItem(card, onNavigateToDetail, painterResource(id = R.drawable.bc_baro), "{card.mobiUserId}님의 카드")
                        }
                    }
                }
            }

            is OwnedCardUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Button(onClick = { viewModel.requestUserCards() }) {
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
    contentDescription: String
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
                Text(
                    text = maskCardNumber(card.cardNo),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("만료일", color = Color.White.copy(alpha = 0.7f))
                        Text(formatExpiryDate(card.cardExpiryDate), color = Color.White)
                    }
                }

            }
        }
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
