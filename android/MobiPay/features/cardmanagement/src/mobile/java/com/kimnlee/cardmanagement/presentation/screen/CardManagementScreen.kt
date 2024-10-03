package com.kimnlee.cardmanagement.presentation.screen

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
    val registeredCards by viewModel.registeredCards.collectAsState()
    val showBottomSheet by viewModel.showBottomSheet.collectAsState()

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        viewModel.getRegisteredCards()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("등록된 카드") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(registeredCards) { card ->
                CardItem(
                    card = card,
                    onAutoPaymentToggle = {
                        viewModel.setAutoPaymentCard(card.ownedCardId, !card.autoPayStatus)
                    }
                )
            }
            item {
                AddCardButton { viewModel.openBottomSheet() }
            }
        }

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
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = findCardCompany(card.ownedCardId.toString())),
                contentDescription = "Card Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = maskCardNumber(card.ownedCardId.toString()),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "일일 한도: ${card.oneDayLimit}원",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "1회 한도: ${card.oneTimeLimit}원",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    IconButton(onClick = onAutoPaymentToggle) {
                        Icon(
                            imageVector = if (card.autoPayStatus) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Toggle Auto Payment",
                            tint = if (card.autoPayStatus) Color.Yellow else Color.White
                        )
                    }
                }
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
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Card",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "카드 추가",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
