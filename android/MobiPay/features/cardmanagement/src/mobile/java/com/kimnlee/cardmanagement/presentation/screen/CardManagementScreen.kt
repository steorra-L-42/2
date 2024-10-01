package com.kimnlee.cardmanagement.presentation.screen

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.kimnlee.cardmanagement.R
import com.kimnlee.cardmanagement.data.model.Photos
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.cardmanagement.presentation.viewmodel.CardUiState
import com.kimnlee.cardmanagement.presentation.viewmodel.PhotoUiState
import kotlin.properties.Delegates

@Composable
fun CardManagementScreen(
    onNavigateToDetail: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    viewModel: CardManagementViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val photoUiState by viewModel.photoUiState.collectAsState()
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "사용자 관리 (카드 관리 모듈)\n등록한 카드 목록",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.padding(16.dp))

        when (val state = photoUiState) {
            is PhotoUiState.Loading -> {
                CircularProgressIndicator()
            }
            is PhotoUiState.Success -> {
                LazyRow(
                    state = listState,
                    flingBehavior = flingBehavior,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.photos) { card ->
                        CardItem(card, onNavigateToDetail)
                    }
                }
            }
            is PhotoUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Button(onClick = { viewModel.fetchPhotos() }) {
                    Text("다시 시도")
                }
            }
        }

        Spacer(modifier = Modifier.padding(16.dp))

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
}

@Composable
fun CardItem(card: Photos, onNavigateToDetail: () -> Unit) {
    var imageRatio by remember { mutableStateOf(1f) }
    val maxHeight = 300.dp
    val cardWidth = 240.dp
    val num: Int = card.id % 2
    Card(
        modifier = Modifier
            .width(cardWidth)
            .wrapContentHeight()
            .clickable { onNavigateToDetail() },
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxHeight)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.card_example),
                    contentDescription = card.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (imageRatio > 1f) {
                                Modifier.fillMaxWidth()
                            } else {
                                Modifier.height(maxHeight)
                            }
                        )
                        .onGloballyPositioned { coordinates ->
                            imageRatio = coordinates.size.width / coordinates.size.height.toFloat()
                        },
                    contentScale = if (imageRatio > 1f) ContentScale.Fit else ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Album ID: ${card.albumId}")
            Text(text = "ID: ${card.id}")
            Text(text = "Title: ${card.title}", maxLines = 2)
        }
    }
}
val cardImages = listOf(
    R.drawable.bc_baro,
    R.drawable.ha_multi_living
)
//@Composable
//fun rememberSnapFlingBehavior(
//    lazyListState: LazyListState,
//    snapOffset: Int = 0,
//    springSpec: AnimationSpec<Float> = SpringSpec()
//): FlingBehavior {
//    return remember(lazyListState) {
//        SnapFlingBehavior(lazyListState, snapOffset, springSpec)
//    }
//}

//class SnapFlingBehavior(
//    private val lazyListState: LazyListState,
//    private val snapOffset: Int = 0,
//    private val springSpec: AnimationSpec<Float> = SpringSpec()
//) : FlingBehavior {
//    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
//        // 스냅 로직 구현
//        // (복잡한 구현이므로 실제 코드는 생략합니다)
//    }
//}