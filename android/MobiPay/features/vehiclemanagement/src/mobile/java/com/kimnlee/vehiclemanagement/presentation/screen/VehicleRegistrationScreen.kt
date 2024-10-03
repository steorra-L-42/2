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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kimnlee.vehiclemanagement.R
import androidx.compose.foundation.focusable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import com.kimnlee.common.ui.theme.*
import com.kimnlee.common.utils.CarModelImageProvider

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
    var isFocused by remember { mutableStateOf(false) }

    var hasCameraPermission by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    var recognizedLicensePlate by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(true) }

    val brands = listOf(
        "현대", "제네시스", "기아", "쉐보레", "르노", "KGM 모터스", "Mercedes-Benz", "BMW", "아우디", "테슬라"
    )
    var selectedBrand by remember { mutableStateOf("") }
    var expandedBrand by remember { mutableStateOf(false) }

    val vehicleTypesMap = mapOf(
        "아우디" to listOf("A3", "A4", "A5", "A6", "A7", "A8", "Q2", "Q3", "Q5", "Q7", "Q8"),
        "Mercedes-Benz" to listOf("A-클래스", "C-클래스", "CLA", "CLE", "E-클래스", "GLC", "GLE", "S-클래스"),
        "KGM 모터스" to listOf("액티언", "코란도", "렉스턴", "티볼리", "토레스"),
        "현대" to listOf("아반떼", "그랜저", "아이오닉5", "아이오닉6", "코나", "넥쏘", "팰리세이드", "싼타페", "쏘나타", "투싼", "베뉴"),
        "BMW" to listOf("BMW3", "BMW5", "BMS7", "X3", "X5", "X6"),
        "기아" to listOf("카니발", "EV3", "EV6", "EV9", "K5", "K8", "K9", "모하비", "모닝", "니로", "니로EV", "레이", "셀토스", "쏘렌토", "스포티지"),
        "쉐보레" to listOf("콜로라도", "이쿼녹스", "임팔라", "말리부", "스파크", "트레일블레이저", "트랙스"),
        "제네시스" to listOf("G70", "G80", "GV60", "GV70", "GV80"),
        "테슬라" to listOf("모델3", "모델S", "모델X", "모델Y"),
        "르노" to listOf("QM6", "SM6", "XM3")
    )
    var selectedVehicleType by remember { mutableStateOf("") }
    var expandedVehicleType by remember { mutableStateOf(false) }

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

    val imageResId = CarModelImageProvider.getImageResId(selectedVehicleType)


    val validPattern = Regex("^(\\d{3}[가-힣]\\d{4}|\\d{2}[가-힣]\\d{4})?$")
    val isLicensePlateValid = validPattern.matches(licensePlate)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        IconButton(
            onClick = {
                if (hasCameraPermission) {
                    hasCameraPermission = false
                    recognizedLicensePlate = ""
                    isAnalyzing = true
                } else {
                    onNavigateBack()
                }
            },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "뒤로 가기",
                modifier = Modifier.size(32.dp),
                tint = Color.Black
            )
        }

        Text(
            text = "차량 등록",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))


        if (!hasCameraPermission) {
            Text(
                text = "제조사",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { expandedBrand = true },
                    modifier = Modifier
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = if (selectedBrand.isEmpty()) "제조사를 선택해주세요" else selectedBrand,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                    )
                }

                DropdownMenu(
                    expanded = expandedBrand,
                    onDismissRequest = { expandedBrand = false },
                    modifier = Modifier
                        .fillMaxWidth(0.925f)
                        .background(Color.White)
                ) {
                    brands.forEach { brand ->
                        DropdownMenuItem(
                            text = { Text(brand) },
                            onClick = {
                                selectedBrand = brand
                                expandedBrand = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "차종",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedBrand.isNotEmpty()) Color.Transparent else Color.LightGray // 원하는 배경색으로 설정
                ) {
                    Button(
                        onClick = { expandedVehicleType = true },
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedBrand.isNotEmpty()) Color.Transparent else Color.LightGray,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.LightGray,
                            disabledContentColor = Color.Gray
                        ),
                        enabled = selectedBrand.isNotEmpty()
                    ) {
                        Text(
                            text = if (selectedVehicleType.isEmpty()) "차종을 선택해주세요" else selectedVehicleType,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = expandedVehicleType,
                    onDismissRequest = { expandedVehicleType = false },
                    modifier = Modifier
                        .fillMaxWidth(0.925f)
                        .background(Color.White)
                ) {
                    val currentVehicleTypes = vehicleTypesMap[selectedBrand] ?: emptyList()

                    currentVehicleTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedVehicleType = type
                                expandedVehicleType = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (selectedVehicleType.isEmpty()) {
                Box(
                    modifier = Modifier
                        .size(240.dp)
                ) {
                    Text("")
                }
            } else if (selectedVehicleType.isNotEmpty() && imageResId != null) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = selectedVehicleType,
                    modifier = Modifier
                        .size(240.dp)
                        .padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "차량 번호",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextField(
                value = licensePlate,
                onValueChange = { newValue ->
                    if (newValue.all { it != ' '}) {
                        licensePlate = newValue.trim()
                    }
                },
                label = {
                    if (!isFocused) {
                        Text(
                            text = "차량 번호를 입력해주세요",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusable()
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    }
                    .padding(end = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Gray,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Red,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "---- 또 는 ----",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    hasCameraPermission = true
                    recognizedLicensePlate = ""
                    isAnalyzing = true
                },
                modifier = Modifier
                    .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )
            ) {
                Text("카메라로 차량 번호 인식하기")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    showDialog = true
                },
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MobiBlue,
                    contentColor = Color.White
                ),
                enabled = selectedVehicleType.isNotEmpty() && licensePlate.isNotEmpty() && isLicensePlateValid
            ) {
                Text("확 인", fontSize = 20.sp)
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

        Spacer(modifier = Modifier.height(32.dp))

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("차량 정보를 확인해주세요") },
                text = {
                    Text(
                        text = "차종 : $selectedVehicleType \n\n차량 번호 : $licensePlate",
                        fontSize = 20.sp,
                        modifier = Modifier.offset(x=4.dp)
                    )
               },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.registerVehicle(licensePlate, selectedVehicleType)
                            showDialog = false
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MobiBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("확 인")
                    }
                    Button(
                        onClick = {
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("취 소")
                    }
                },
                containerColor = Color.White
            )
        }
    }
}
