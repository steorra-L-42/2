package com.kimnlee.cardmanagement.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.kimnlee.cardmanagement.R
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardManagementBottomSheet(
    sheetState: SheetState,
    scope: CoroutineScope,
    viewModel: CardManagementViewModel,
    onNavigateToRegistration: () -> Unit,
    onNavigateToOwnedCards: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.closeBottomSheet()
        },
        sheetState = sheetState,
        containerColor = Color(0xFFF2F4F6),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CardManagementOptionItem(
                icon = ImageVector.vectorResource(id = R.drawable.baseline_credit_card_24),
                title = "소유한 카드 중 고르기",
                description = "소유중인 카드 중 고를 수 있습니다.",
                onItemClick = {
                    // 소유중인 카드 리스트 목록으로 가는 기능
                    onNavigateToOwnedCards()
                    viewModel.closeBottomSheet()
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            CardManagementOptionItem(
                icon = ImageVector.vectorResource(id = R.drawable.baseline_add_card_24),
                title = "새로운 카드 등록",
                description = "새로운 카드를 등록할 수 있습니다.",
                onItemClick = {
                    onNavigateToRegistration()
                    viewModel.closeBottomSheet()
                })
        }
    }
}