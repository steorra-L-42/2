package com.kimnlee.cardmanagement.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiBgWhite
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.ui.theme.MobiTextDarkGray
import com.kimnlee.common.utils.moneyFormat
import com.kimnlee.common.utils.nonFormatCardNumber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    viewModel: CardManagementViewModel,
    cardId: Int,
    onNavigateBack: () -> Unit
) {
    val cardDetail by viewModel.cardDetail.collectAsState()

    LaunchedEffect(cardId) {
        viewModel.loadCardDetail(cardId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearCardDetail()
        }
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
                            modifier = Modifier.padding(top = 4.dp, end = 8.dp)
                        )
                        Text(
                            text = "카드 상세",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            cardDetail?.let { detail ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.6f)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Image(
                        painter = painterResource(id = findCardCompany(detail.cardNo)),
                        contentDescription = "Card Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                DetailSection(
                    items = listOf(
                        "카드 번호" to nonFormatCardNumber(detail.cardNo),
                        "CVC" to detail.cvc,
                        "카드 만료일" to nonFormatExpiryDate(detail.cardExpiryDate),
                        "카드 한도" to moneyFormat(detail.oneTimeLimit.toBigInteger())
                    )
                )
            }
        }
    }
}

@Composable
fun DetailSection(items: List<Pair<String, String>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp),
        colors = CardDefaults.cardColors(containerColor = MobiBgWhite)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            items.forEachIndexed { index, (label, value) ->
                DetailItem(label, value)
                if (index < items.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 1.dp,
                        color = Color(0xFFE5E5E5)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MobiTextDarkGray
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            color = MobiTextAlmostBlack
        )
    }
}