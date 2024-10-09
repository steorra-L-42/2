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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.zIndex
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
                Toast.makeText(context, "차량의 전체 실루엣이 보여야 인식돼요.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "카메라 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val imageResId = CarModelImageProvider.getImageResId(selectedVehicleType)


    val validPattern = Regex("^(\\d{3}[가-힣]\\d{4}|\\d{2}[가-힣]\\d{4})?$")
    val isLicensePlateValid = validPattern.matches(licensePlate)

    val defaultFont = FontFamily(Font(com.kimnlee.common.R.font.pmedium))

    MobiPayTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
                .zIndex(1f)
        ) {
            IconButton(
                onClick = {
                    Log.d(TAG, "뒤로가기 버튼 클릭")
                    if (hasCameraPermission) {
                        hasCameraPermission = false
                        recognizedLicensePlate = ""
                        isAnalyzing = true
                    } else {
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "뒤로 가기",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Black
                )
            }

            Text(
                text = "차량 등록",
                fontSize = 22.sp,
                fontFamily = FontFamily(Font(com.kimnlee.common.R.font.psemibold)),
                color = MobiTextAlmostBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))


            if (!hasCameraPermission) {
                Row(

                ){
                    Text(
                        text = "제조사",
                        fontFamily = defaultFont,
                        fontSize = 20.sp,
                        color = MobiTextAlmostBlack,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(start = 10.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(30.dp))
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(end = 20.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Button(
                            onClick = { expandedBrand = true },
                            modifier = Modifier
//                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .width(200.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Black
                            ),
                        ) {
                            Text(
                                text = if (selectedBrand.isEmpty()) "제조사 선택 ▼" else selectedBrand,
                                fontFamily = defaultFont,
                                color = MobiTextAlmostBlack,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                            )
                        }

                        DropdownMenu(
                            expanded = expandedBrand,
                            onDismissRequest = { expandedBrand = false },
                            modifier = Modifier
                                .width(190.dp)
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
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(

                ){
                    Text(
                        text = "차종",
                        fontFamily = defaultFont,
                        color = MobiTextAlmostBlack,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(start = 10.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(30.dp))
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(end = 20.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {

                        Button(
                            onClick = { expandedVehicleType = true },
                            modifier = Modifier
                                .background(Color.White)
                                .width(200.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = MobiTextAlmostBlack,
                                disabledContainerColor = Color.White,
                                disabledContentColor = Color.LightGray
                            ),
                            enabled = selectedBrand.isNotEmpty()
                        ) {
                            Text(
                                text = if (selectedVehicleType.isEmpty()) "차종 선택 ▼" else selectedVehicleType,
                                fontFamily = defaultFont,
                                color = if (selectedBrand.isNotEmpty()) MobiTextAlmostBlack else Color.LightGray,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                            )
                        }

                        DropdownMenu(
                            expanded = expandedVehicleType,
                            onDismissRequest = { expandedVehicleType = false },
                            modifier = Modifier
                                .width(190.dp)
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
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (selectedVehicleType.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .height(180.dp)
                    ) {
                        Text(
                            text = "",
                            fontFamily = defaultFont
                        )
                    }
                } else if (selectedVehicleType.isNotEmpty() && imageResId != null) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = selectedVehicleType,
                        modifier = Modifier
                            .height(180.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "차량 번호",
                    fontSize = 20.sp,
                    fontFamily = defaultFont,color = MobiTextAlmostBlack,
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
                                fontFamily = defaultFont,color = MobiTextAlmostBlack,
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
                        focusedIndicatorColor = MobiTextAlmostBlack,
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
                    text = "---- 또는 ----",
                    fontFamily = defaultFont,
                    color = MobiTextAlmostBlack,
                    fontSize = 16.sp
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
                        .background(Color.White)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MobiTextAlmostBlack
                    )
                ) {
                    Text(
                        "카메라로 차량 번호 인식하기",
                        fontFamily = defaultFont,
                        color = MobiTextAlmostBlack,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MobiBlue,
                        contentColor = Color.White
                    ),
                    enabled = selectedVehicleType.isNotEmpty() && licensePlate.isNotEmpty() && isLicensePlateValid
                ) {
                    Text(
                        "확 인",
                        fontFamily = defaultFont,color = Color.White,
                        fontSize = 20.sp
                    )
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
                    title = {
                        Text(
                            "차량 정보를 확인해주세요",
                            fontFamily = defaultFont
                        )},
                    text = {
                        Text(
                            text = "차종 : $selectedVehicleType \n\n차량 번호 : $licensePlate",
                            fontFamily = defaultFont,color = MobiTextAlmostBlack,
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
                            Text("확인",
                                fontFamily = FontFamily(Font(com.kimnlee.common.R.font.pbold))
                            )
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
                            Text("취 소",
                                fontFamily = defaultFont
                                )
                        }
                    },
                    containerColor = Color.White
                )
            }
        }
    }

}
