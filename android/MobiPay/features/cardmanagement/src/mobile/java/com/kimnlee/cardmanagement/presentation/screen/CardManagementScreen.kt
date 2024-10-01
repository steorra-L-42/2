package com.kimnlee.cardmanagement.presentation.screen

import Card
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.kimnlee.cardmanagement.R
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.CardUiState
import com.kimnlee.cardmanagement.presentation.viewmodel.PhotoUiState

@Composable
fun CardManagementScreen(
    onNavigateToDetail: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    viewModel: CardManagementViewModel = viewModel()
) {
    val cardUiState by viewModel.cardUiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "카드 관리",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.padding(16.dp))

        when (val state = cardUiState) {
            is CardUiState.Loading -> {
                CircularProgressIndicator()
            }
            is CardUiState.Success -> {
                LazyRow(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.cards) { card ->
                        CardItem(card, onNavigateToDetail)
                    }
                }
                Spacer(modifier = Modifier.padding(16.dp))
                AddCardButton(onNavigateToRegistration)
            }
            is CardUiState.Error -> {
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
fun CardItem(card: Card, onNavigateToDetail: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onNavigateToDetail),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .padding(16.dp)
        ) {
            Text(
                text = maskCardNumber(card.cardNo),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(text = "만료일: ${card.cardExpriyDate}")
            Text(text = "출금일: ${card.withdrawalDate}")
        }
    }
}

@Composable
fun AddCardButton(onNavigateToRegistration: () -> Unit) {
    OutlinedButton(
        onClick = onNavigateToRegistration,
        modifier = Modifier
            .fillMaxWidth(0.66f)
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Card",
            tint = Color.Gray,
            modifier = Modifier.size(40.dp)
        )
    }
}

fun maskCardNumber(cardNumber: String): String {
    val visiblePart = cardNumber.take(4)
    val maskedPart = "*".repeat(cardNumber.length - 4)
    return "$visiblePart$maskedPart"
}