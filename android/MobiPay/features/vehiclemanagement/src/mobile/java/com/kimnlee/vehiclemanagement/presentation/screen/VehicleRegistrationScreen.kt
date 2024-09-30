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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    viewModel: VehicleManagementViewModel = viewModel(),
    apiClient: ApiClient
) {
    var licensePlate by remember { mutableStateOf("") }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var oneTimeLimit by remember { mutableStateOf("") }
    var oneDayLimit by remember { mutableStateOf("") }

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

        if (!vehicleNumberCheck) {
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
                        viewModel.addVehicle(licensePlate)
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
                                showDialog = false
                                vehicleNumberCheck = true
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
        } else {
            Spacer(modifier = Modifier.height(16.dp))

            Text("차량 번호: $licensePlate")

            Spacer(modifier = Modifier.height(8.dp))

            Text("주결제 카드 등록")

            Spacer(modifier = Modifier.height(32.dp))

            Text("카드 목록 공간") // API 목록 받아오기

            Spacer(modifier = Modifier.height(32.dp))

            Row {
                OutlinedTextField(
                    value = oneDayLimit,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            oneDayLimit = newValue
                        }
                    },
                    label = { Text("1일 한도") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(300.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("원", fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                OutlinedTextField(
                    value = oneTimeLimit,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            oneTimeLimit = newValue
                        }
                    },
                    label = { Text("1회 한도") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(300.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("원", fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    onNavigateBack()
                    vehicleNumberCheck = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("등록")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    vehicleNumberCheck = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("이전")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("취소")
        }
    }
}
