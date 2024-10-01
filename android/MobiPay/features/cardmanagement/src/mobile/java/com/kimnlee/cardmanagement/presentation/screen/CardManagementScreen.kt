package com.kimnlee.cardmanagement.presentation.screen

import Photos
import RegistrationCard
import android.util.Log
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.kimnlee.cardmanagement.R
import com.kimnlee.cardmanagement.presentation.components.CardManagementBottomSheet
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.RegistrationCardUiState
import com.kimnlee.cardmanagement.presentation.viewmodel.PhotoUiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardManagementScreen(
    onNavigateToDetail: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    onNavigateToOwnedCards: () -> Unit,
    viewModel: CardManagementViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val photoUiState by viewModel.photoUiState.collectAsState()
    val listState = rememberLazyListState()
    val registrationCardUiState by viewModel.registrationCardUiState.collectAsState()

    val showBottomSheet by viewModel.showBottomSheet.collectAsState()

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
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
        when (val state = photoUiState) {
            is PhotoUiState.Loading -> {
//        when (val state = registrationCardUiState) {
//            is RegistrationCardUiState.Loading -> {
                CircularProgressIndicator()
            }

            is PhotoUiState.Success -> {
                if (state.photos.isEmpty()) {
//            is RegistrationCardUiState.Success -> {
//                if (state.cards.isEmpty()) {
                    Text("등록된 카드가 없습니다.")
                    AddCardButton { viewModel.openBottomSheet() }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
//                        items(state.cards) { card ->
                        item {
                            AddCardButton { viewModel.openBottomSheet() }
                        }
                        items(state.photos) { card ->
                            CardItem(card, onNavigateToDetail, painterResource(id = R.drawable.bc_baro), "카드")
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
            is PhotoUiState.Error -> {
//            is RegistrationCardUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Button(onClick = { viewModel.requestRegistrationCards() }) {
                    Text("다시 시도")
                }
                Button(onClick = { viewModel.openBottomSheet() }) {
                    Text("추가하러 가기")
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
    }
}

@Composable
fun CardItem(
    card: Photos,
//    card: RegistrationCard,
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
                text = "카드 이름",
//                text = card.cardName,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                 Row{
                    Text(text ="1일 한도" )
//                    Text(text ="${card.oneDayLimit}" )
                     Text(text = "100,000")
                }
                Row{
                    Text(text ="1회 한도" )
//                    Text(text ="${card.oneDayLimit}" )
                    Text(text = "100,000")
                }
                Row{
                    Text(text ="자동 결제 여부" )
//                    card.autoPayStatus ?: Text(text = "100,000", style = MaterialTheme.typography.bodySmall)
                    Text(text = "자동 결제 중")
                }
            }
        }
    }
}}

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
