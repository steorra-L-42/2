package com.kimnlee.vehiclemanagement.presentation.screen

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kimnlee.common.network.ApiClient
import com.kimnlee.vehiclemanagement.data.model.LicensePlateAnalyzer
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel
import java.util.concurrent.Executors

private const val TAG = "VehicleRegistrationScreen"

@Composable
fun VehicleRegistrationScreen(
    onNavigateBack: () -> Unit,
    viewModel: VehicleManagementViewModel,
    apiClient: ApiClient
) {
    var licensePlate by remember { mutableStateOf("") }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var hasCameraPermission by remember { mutableStateOf(false) }
    var vehicleNumberCheck by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

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
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "차량 등록",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

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

            OutlinedTextField(
                value = licensePlate,
                onValueChange = { licensePlate = it },
                label = { Text("차량 번호") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(100.dp))

            Text("차량 이미지 추가를 위한 공간")

            Spacer(modifier = Modifier.height(100.dp))

            Button(
                onClick = {
                    showDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("다음")
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }

                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build()

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            val imageAnalyzer = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            imageAnalyzer.setAnalyzer(
                                cameraExecutor,
                                LicensePlateAnalyzer(
                                    context = ctx,
                                    isAnalyzing = { isAnalyzing },
                                    onLicensePlateRecognized = { plateNumber ->
                                        recognizedLicensePlate = plateNumber
                                    },
                                    apiClient = apiClient
                                )
                            )

                            preview.setSurfaceProvider(previewView.surfaceProvider)

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageAnalyzer
                                )
                            } catch (e: Exception) {
                                Log.e("CameraPreview", "Use case binding failed", e)
                            }
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            LaunchedEffect(recognizedLicensePlate) {
                if (recognizedLicensePlate.isNotEmpty()) {
                    licensePlate = recognizedLicensePlate
                    hasCameraPermission = false
                    isAnalyzing = false
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(licensePlate) },
                text = { Text("이 차량 번호가 맞나요?.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.registerVehicle(licensePlate)
                            showDialog = false
                            onNavigateBack()
                        }
                    ) {
                        Text("확인")
                    }
                    Button(
                        onClick = {
                            showDialog = false
                        }
                    ) {
                        Text("취소")
                    }
                }
            )
        }
    }
}
