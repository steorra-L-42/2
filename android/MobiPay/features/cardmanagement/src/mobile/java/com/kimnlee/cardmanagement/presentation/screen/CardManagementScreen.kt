package com.kimnlee.cardmanagement.presentation.screen

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kimnlee.common.R
import com.kimnlee.cardmanagement.data.model.RegisteredCard
import com.kimnlee.cardmanagement.presentation.components.CardManagementBottomSheet
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardManagementScreen(
    onNavigateToRegistration: () -> Unit,
    onNavigateToOwnedCards: () -> Unit,
    viewModel: CardManagementViewModel,
) {
    val registeredCards by viewModel.registeredCards.collectAsState()
    val showBottomSheet by viewModel.showBottomSheet.collectAsState()
    val autoPaymentMessage by viewModel.autoPaymentMessage.collectAsState()

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
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
                            fontSize = 24.sp,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "등록된 카드",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.6f)
    ) {
        Image(
            painter = painterResource(id = findCardCompany(card.ownedCardId.toString())),
            contentDescription = "Card Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TextWithShadow(
                text = maskCardNumber(card.ownedCardId.toString()),
                style = MaterialTheme.typography.titleLarge
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    TextWithShadow(
                        text = "일일 한도: ${card.oneDayLimit}원",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextWithShadow(
                        text = "1회 한도: ${card.oneTimeLimit}원",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Box(
                    modifier = Modifier
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

    Canvas(modifier = Modifier) {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = shadowColorArgb
        frameworkPaint.textSize = style.fontSize.toPx()
        val typeface = when {
            style.fontWeight == FontWeight.Bold -> Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            style.fontStyle == FontStyle.Italic -> Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            else -> Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
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
fun AddCardButton(openBottomSheet: () -> Unit) {
    Spacer(modifier = Modifier.height(8.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = openBottomSheet),
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
}
