package com.kimnlee.cardmanagement.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
//import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun CardManagementRegistrationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDirectRegistration : () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 제목
         Column (
            modifier = Modifier.weight(0.2f),
            horizontalAlignment =  Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "카드 등록",
                style = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight(500),
                )
            )
        }
        // Camera Preview
        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(0.8f),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 30.dp, vertical = 30.dp)
            )
            // 카드를 찍어주세요
            Text(
                text = "카드를 찍어주세요",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight(500),
                )
            )
            // 직접입력 버튼
            Button(onClick = onNavigateToDirectRegistration,
                modifier = Modifier
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White,
                ),
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    text = "직접입력",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight(500),
                        color = Color.White,
                    )

                )
            }
        }
    }
}
//@Preview(showBackground = true)
//@Composable
//fun CardExplorationScreenPreview() {
//    CardManagementRegistrationScreen(
//        onNavigateBack = {},
//        onNavigateToDirectRegistration = {}
//    )
//}