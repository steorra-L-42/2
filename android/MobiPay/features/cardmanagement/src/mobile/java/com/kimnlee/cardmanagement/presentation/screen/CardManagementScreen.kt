package com.kimnlee.cardmanagement.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.kimnlee.cardmanagement.data.model.RegisteredCard
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.MyDataConsentStatus
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.utils.formatCardNumber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardManagementScreen(
    onNavigateToOwnedCards: () -> Unit,
    onNavigateToMyDataAgreement: () -> Unit,
    onNavigateToCardDetail: (Int) -> Unit,
    viewModel: CardManagementViewModel,
) {
    val registeredCards by viewModel.registeredCards.collectAsState()
    val autoPaymentMessage by viewModel.autoPaymentMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.getRegisteredCards()
    }

    LaunchedEffect(autoPaymentMessage) {
        autoPaymentMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
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
                            modifier = Modifier
                                .padding(top = 2.dp, end = 8.dp)
                        )
                        Text(
                            text = "등록된 카드",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    },
                    onCardClick = { onNavigateToCardDetail(card.ownedCardId) }
                )
            }
            item {
                AddCardButton {
                    viewModel.checkMyDataConsentStatus { status ->
                        when (status) {
                            is MyDataConsentStatus.Fetched -> {
                                if (status.isConsented) {
                                    onNavigateToOwnedCards()
                                } else {
                                    onNavigateToMyDataAgreement()
                                }
                            }
                            is MyDataConsentStatus.Error -> {
                                // 에러 처리 (예: 토스트 메시지 표시)
                            }
                            else -> {} // Unknown 상태 처리
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardItem(
    card: RegisteredCard,
    onAutoPaymentToggle: () -> Unit,
    onCardClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.6f)
            .clickable(onClick = onCardClick)
    ) {
        Image(
            painter = painterResource(id = findCardCompany(card.cardNo)),
            contentDescription = "Card Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            TextWithShadow(
                text = formatCardNumber(card.cardNo),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(25.dp))
            TextWithShadow(
                text = "VALID THRU ${formatExpiryDate(card.cardExpriyDate)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                .clickable(onClick = onAutoPaymentToggle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (card.autoPayStatus) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Toggle Auto Payment",
                tint = if (card.autoPayStatus) Color.Yellow else Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun TextWithShadow(
    text: String,
    style: TextStyle,
    shadowColor: Color = Color.Black.copy(alpha = 0.7f),
    offsetY: Float = 1f,
    offsetX: Float = 1f,
    blurRadius: Float = 3f
) {
    val shadowColorArgb = shadowColor.toArgb()
    val textColor = Color.White
    val textColorArgb = textColor.toArgb()

    val context = LocalContext.current
    val typeface = remember {
        ResourcesCompat.getFont(context, R.font.psemibold)
    }

    Canvas(modifier = Modifier) {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = shadowColorArgb
        frameworkPaint.textSize = style.fontSize.toPx()
        frameworkPaint.typeface = typeface

        frameworkPaint.setShadowLayer(blurRadius, offsetX, offsetY, shadowColorArgb)

        drawIntoCanvas {
            it.nativeCanvas.drawText(text, 0f, style.fontSize.toPx(), frameworkPaint)
        }

        frameworkPaint.clearShadowLayer()
        frameworkPaint.color = textColorArgb

        drawIntoCanvas {
            it.nativeCanvas.drawText(text, 0f, style.fontSize.toPx(), frameworkPaint)
        }
    }
}

@Composable
fun AddCardButton(onNavigateToOwnedCards: () -> Unit) {
    Spacer(modifier = Modifier.height(8.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onNavigateToOwnedCards),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "Add Card",
                    modifier = Modifier.size(48.dp),
                    tint = MobiTextAlmostBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "카드 추가",
                    style = MaterialTheme.typography.titleMedium,
                    color = MobiTextAlmostBlack
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
}