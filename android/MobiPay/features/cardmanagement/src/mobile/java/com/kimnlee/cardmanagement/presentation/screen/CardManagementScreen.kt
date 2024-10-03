package com.kimnlee.cardmanagement.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kimnlee.cardmanagement.data.model.RegisteredCard
import com.kimnlee.cardmanagement.presentation.components.CardManagementBottomSheet
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.RegisteredCardState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardManagementScreen(
    onNavigateToRegistration: () -> Unit,
    onNavigateToOwnedCards: () -> Unit,
    viewModel: CardManagementViewModel,
) {
    val scrollState = rememberScrollState()
    val registeredCardState by viewModel.registratedCardState.collectAsState()
    val registeredCards by viewModel.registeredCards.collectAsState()
    val showBottomSheet by viewModel.showBottomSheet.collectAsState()

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        if (registeredCards.isEmpty()) {
            viewModel.getRegisteredCards()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "등록된 카드 확인",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.padding(16.dp))

        when (registeredCardState) {
            is RegisteredCardState.Loading -> {
                CircularProgressIndicator()
            }
            is RegisteredCardState.Success -> {
                if (registeredCards.isEmpty()) {
                    Text("등록된 카드가 없습니다.")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(registeredCards) { card ->
                            CardItem(
                                card = card,
                                onAutoPaymentToggle = {
                                    viewModel.setAutoPaymentCard(card.ownedCardId, !card.autoPayStatus)
                                }
                            )
                        }
                    }
                }
            }
            is RegisteredCardState.Error -> {
                Text(
                    text = (registeredCardState as RegisteredCardState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Button(onClick = { viewModel.getRegisteredCards() }) {
                    Text("다시 시도")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        AddCardButton { viewModel.openBottomSheet() }

        if (showBottomSheet) {
            CardManagementBottomSheet(
                sheetState = sheetState,
                scope = scope,
                viewModel = viewModel,
                onNavigateToRegistration = onNavigateToRegistration,
                onNavigateToOwnedCards = onNavigateToOwnedCards
            )
        }
    }
}

@Composable
fun CardItem(
    card: RegisteredCard,
    onAutoPaymentToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("카드 ID: ${card.ownedCardId}")
                Text("일일 한도: ${card.oneDayLimit}원")
                Text("1회 한도: ${card.oneTimeLimit}원")
                Text("자동 결제: ${if (card.autoPayStatus) "활성화" else "비활성화"}")
            }
            IconButton(onClick = onAutoPaymentToggle) {
                Icon(
                    imageVector = if (card.autoPayStatus) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Toggle Auto Payment",
                    tint = if (card.autoPayStatus) Color.Yellow else Color.Gray
                )
            }
        }
    }
}

@Composable
fun AddCardButton(openBottomSheet: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = openBottomSheet),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Card",
                tint = Color.Gray,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("카드 추가", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
