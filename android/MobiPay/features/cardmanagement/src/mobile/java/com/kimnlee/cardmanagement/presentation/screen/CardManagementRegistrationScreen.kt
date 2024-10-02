package com.kimnlee.cardmanagement.presentation.screen

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kimnlee.cardmanagement.data.model.CardAnalyzer
import com.kimnlee.cardmanagement.presentation.viewmodel.CardManagementViewModel
import com.kimnlee.common.network.ApiClient
import java.util.concurrent.Executors

// 카드 등록 페이지
@Composable
fun CardManagementRegistrationScreen(
    apiClient: ApiClient,
    viewModel: CardManagementViewModel,
    onNavigateToDirectRegistration: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    var licensePlate by remember { mutableStateOf("") }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var hasCameraPermission by remember { mutableStateOf(false) }

    var recognizedLicensePlate by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(true) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
            if (isGranted) {
                Toast.makeText(context, "Camera permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 10.dp)
        ) {
            Button(
                onClick = onNavigateBack,
            ) {
                Text("뒤로 가기")
            }
        }
        // 제목
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "카드 등록",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(0.2f)
            )

            if (!hasCameraPermission) {
                Button(
                    onClick = {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        hasCameraPermission = true
                        recognizedLicensePlate = ""
                        isAnalyzing = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("카메라로 차량 번호 인식하기")
                }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "카메라 권한을 허용해주세요")
            } else {
                // Camera Preview
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.Black)
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
                    Button(
                        onClick = onNavigateToDirectRegistration,
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
                LaunchedEffect(recognizedLicensePlate) {
                    if (recognizedLicensePlate.isNotEmpty()) {
                        licensePlate = recognizedLicensePlate
                        hasCameraPermission = false
                        isAnalyzing = false
                    }
                }
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