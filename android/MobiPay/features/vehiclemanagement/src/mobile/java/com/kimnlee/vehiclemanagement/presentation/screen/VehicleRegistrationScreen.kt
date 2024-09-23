package com.kimnlee.vehiclemanagement.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.ui.viewinterop.AndroidView
import java.util.concurrent.Executors
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.camera.core.CameraSelector
import androidx.lifecycle.compose.LocalLifecycleOwner

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

import androidx.compose.ui.text.input.KeyboardType


@Composable
fun VehicleRegistrationScreen(
    onNavigateBack: () -> Unit,
    viewModel: VehicleManagementViewModel = viewModel()
) {
    var licensePlate by remember { mutableStateOf("") }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var oneTimeLimit by remember { mutableStateOf("") }
    var oneDayLimit by remember { mutableStateOf("") }

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    var vehicleNumberCheck by remember { mutableStateOf(false)}
    var showDialog by remember { mutableStateOf(false) }

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
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(100.dp))

                Text("차량 이미지 추가를 위한 공간") // 자동차 이미지 등록을 위한 선택지 or 검색 제공 기능 추가해야 함

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
            } else { // 실제 촬영 시 OCR로 차량 번호 인식 후 자동 입력
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

                                imageCapture = ImageCapture.Builder()
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                    .build()

                                preview.setSurfaceProvider(previewView.surfaceProvider)

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        imageCapture
                                    )
                                } catch (e: Exception) {
                                    Log.e("CameraPreview", "Use case binding failed", e)
                                }
                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    IconButton(
                        onClick = {
                            takePhoto(
                                imageCapture,
                                context,
                                onPhotoTaken = { recognizedText  ->
                                    licensePlate = recognizedText // OCR로 추출한 텍스트를 차량 번호로 설정
                                    // 사진 촬영 후 권한 해제
                                    hasCameraPermission = false
                                }
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                            .size(80.dp)
                            .background(Color.LightGray, shape = CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_camera),
                            contentDescription = "Take Photo",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(licensePlate) }, // 입력된 차량 번호를 받아오기
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

private fun takePhoto(
    imageCapture: ImageCapture?,
    context: Context,
    onPhotoTaken: (String) -> Unit
) {
    imageCapture?.let {
        val photoFile = File(
            context.externalMediaDirs.firstOrNull(),
            "VehicleRegistration-${SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis())}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        it.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    // 이미지에서 텍스트 추출
                    // 추후 팀에서 학습시킨 AI로 대체
                    extractTextFromImage(context, savedUri) { recognizedText ->
                        onPhotoTaken(recognizedText)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("TakePhoto", "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }
}

private fun extractTextFromImage(context: Context, imageUri: Uri, onTextExtracted: (String) -> Unit) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    try {
        val image = InputImage.fromFilePath(context, imageUri)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // 텍스트 인식 성공 시
                val recognizedText = visionText.text
                onTextExtracted(recognizedText)
            }
            .addOnFailureListener { e ->
                Log.e("TextRecognition", "Text recognition failed: ${e.message}")
            }
    } catch (e: Exception) {
        Log.e("TextRecognition", "Failed to load image: ${e.message}")
    }
}