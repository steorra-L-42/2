package com.kimnlee.cardmanagement.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.ui.theme.MobiBgWhite
import com.kimnlee.common.ui.theme.MobiBlue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDataAgreementScreen(
    viewModel: CardManagementViewModel,
    onNavigateToOwnedCards: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var showLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    fun getPrivacyText(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    val agreements = remember {
        listOf(
            Agreement("마이데이터서비스 이용 약관 (필수)", getPrivacyText("Terms_of_Service_for_MyData_Service.txt")),
            Agreement("전자금융거래 기본약관 (필수)", getPrivacyText("Standard_Terms_and_Conditions_for_Electronic_Financial_Transactions.txt"))
        )
    }

    val allAgreed = remember(agreements) {
        derivedStateOf { agreements.all { it.isChecked.value } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("서비스 신청 및 이용약관동의") },
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "마이데이터 서비스 이용을 위해\n아래 약관 동의가 필요해요.",
                style = MaterialTheme.typography.bodyLarge
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MobiBgWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = allAgreed.value,
                            onCheckedChange = { checked ->
                                agreements.forEach { it.isChecked.value = checked }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = MobiBlue)
                        )
                        Text("전체 동의(필수)", style = MaterialTheme.typography.titleMedium)
                    }
                    HorizontalDivider()
                    agreements.forEachIndexed { index, agreement ->
                        AgreementItem(
                            agreement = agreement,
                            expanded = expandedIndex == index,
                            onExpandToggle = {
                                expandedIndex = if (expandedIndex == index) null else index
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    showLoading = true
                    coroutineScope.launch {
                        viewModel.setMyDataAgreement() // 비동기 작업 시작
                        delay(2000) // 2초 지연
                        withContext(Dispatchers.Main) {
                            showLoading = false
                            onNavigateToOwnedCards() // 메인 스레드에서 실행
                        }
                    }
                },
                enabled = allAgreed.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3182F6)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "동의하고 계속하기",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }

    if (showLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "내 카드 정보를 가져오고 있어요",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun AgreementItem(
    agreement: Agreement,
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    val lazyListState = rememberLazyListState()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = agreement.isChecked.value,
                    onCheckedChange = { checked ->
                        agreement.isChecked.value = checked
                    },
                    colors = CheckboxDefaults.colors(checkedColor = MobiBlue)
                )
                Text(
                    agreement.title,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            IconButton(onClick = onExpandToggle) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
        }
        if (expanded) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(start = 32.dp, top = 8.dp, bottom = 8.dp, end = 16.dp)
                    .height(200.dp)
            ) {
                item {
                    Text(
                        agreement.content,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

data class Agreement(
    val title: String,
    val content: String,
    val isChecked: MutableState<Boolean> = mutableStateOf(false)
)