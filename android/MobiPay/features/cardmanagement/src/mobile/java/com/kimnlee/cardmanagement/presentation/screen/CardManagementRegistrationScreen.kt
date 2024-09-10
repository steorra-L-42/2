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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CardManagementRegistrationScreen(
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Camera Preview
        Box(modifier = Modifier
            .fillMaxWidth()
//            .weight(1f)
            .background(Color.Green)
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
//                        cameraPreview?.setSurfaceProvider(surfaceProvider)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(300.dp)
                    .background(Color.Red)
            )
        }

        // Button
        Button(onClick = onNavigateBack,
            modifier = Modifier.padding(16.dp)) {
            Text("직접입력")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "카드 관리 상세",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.background(Color.White)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateBack) {
            Text("뒤로 가기")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun CardExplorationScreenPreview() {
    CardManagementRegistrationScreen(onNavigateBack = {})
}