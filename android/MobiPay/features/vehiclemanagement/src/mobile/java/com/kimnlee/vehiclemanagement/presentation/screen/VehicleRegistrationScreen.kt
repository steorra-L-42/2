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
import com.kimnlee.vehiclemanagement.R

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
    var showDialog by remember { mutableStateOf(false) }

    var recognizedLicensePlate by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(true) }

    val brands = listOf(
        "Audi", "Mercedes-Benz", "KGM", "HYUNDAI",
        "BMW", "KIA", "CHEVROLET", "GENESIS",
        "TESLA", "RENAULT"
    )
    var selectedBrand by remember { mutableStateOf("") }
    var expandedBrand by remember { mutableStateOf(false) }

    val vehicleTypesMap = mapOf(
        "Audi" to listOf("A3", "A4", "A5", "A6", "A7", "A8", "Q2", "Q3", "Q5", "Q7", "Q8"),
        "Mercedes-Benz" to listOf("A-Class", "C-Class", "CLA", "CLE", "E-Class", "GLC", "GLE", "S-Class"),
        "KGM" to listOf("Actyon", "Corando", "Rexton", "Tivoli", "Torres"),
        "HYUNDAI" to listOf("Avante", "Grandeur", "Ioniq5", "Ioniq6", "Kona", "Nexo", "Palisade", "SantaFe", "Sonata", "Tucson", "Venue"),
        "BMW" to listOf("BMW3", "BMW5", "BMS7", "X3", "X5", "X6"),
        "KIA" to listOf("Carnival", "EV3", "3V6", "EV9", "K5", "K8", "K9", "Mohave", "Morning", "Niro", "NiroEV", "Ray", "Seltos", "Sorento", "Sportage"),
        "CHEVROLET" to listOf("Colorado", "Equinox", "Impala", "Malibu", "Spark", "Trailblazer", "Trax"),
        "GENESIS" to listOf("G70", "G80", "GV60", "GV70", "GV80"),
        "TESLA" to listOf("Model3", "ModelS", "ModelX", "ModelY"),
        "RENAULT" to listOf("QM6", "SM6", "XM3")
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

    val imageResId = when (selectedVehicleType) {
        "A3" -> R.drawable.a3
        "A4" -> R.drawable.a4
        "A5" -> R.drawable.a5
        "A6" -> R.drawable.a6
        "A7" -> R.drawable.a7
        "A8" -> R.drawable.a8
        "Q2" -> R.drawable.q2
        "Q3" -> R.drawable.q3
        "Q5" -> R.drawable.q5
        "Q7" -> R.drawable.q7
        "Q8" -> R.drawable.q8
        "A-Class" -> R.drawable.aclass
        "C-Class" -> R.drawable.cclass
        "CLA" -> R.drawable.cla
        "CLE" -> R.drawable.cle
        "E-Class" -> R.drawable.eclass
        "GLC" -> R.drawable.glc
        "GLE" -> R.drawable.gle
        "S-Class" -> R.drawable.sclass
        // KGM
        "Actyon" -> R.drawable.actyon
        "Corando" -> R.drawable.corando
        "Rexton" -> R.drawable.rexton
        "Tivoli" -> R.drawable.tivoli
        "Torres" -> R.drawable.torres
        // HYUNDAI
        "Avante" -> R.drawable.avante
        "Grandeur" -> R.drawable.grandeur
        "Ioniq5" -> R.drawable.ioniq5
        "Ioniq6" -> R.drawable.ioniq6
        "Kona" -> R.drawable.kona
        "Nexo" -> R.drawable.nexo
        "Palisade" -> R.drawable.palisade
        "SantaFe" -> R.drawable.santafe
        "Sonata" -> R.drawable.sonata
        "Tucson" -> R.drawable.tucson
        "Venue" -> R.drawable.venue
        // BMW
        "BMW3" -> R.drawable.bmw3
        "BMW5" -> R.drawable.bmw5
        "BMS7" -> R.drawable.bmw7
        "X3" -> R.drawable.x3
        "X5" -> R.drawable.x5
        "X6" -> R.drawable.x6
        // KIA
        "Carnival" -> R.drawable.carnival
        "EV3" -> R.drawable.ev3
        "EV6" -> R.drawable.ev6
        "EV9" -> R.drawable.ev9
        "K5" -> R.drawable.k5
        "K8" -> R.drawable.k8
        "K9" -> R.drawable.k9
        "Mohave" -> R.drawable.mohave
        "Morning" -> R.drawable.morning
        "Niro" -> R.drawable.niro
        "NiroEV" -> R.drawable.niroev
        "Ray" -> R.drawable.ray
        "Seltos" -> R.drawable.seltos
        "Sorento" -> R.drawable.sorento
        "Sportage" -> R.drawable.sportage
        // CHEVROLET
        "Colorado" -> R.drawable.colorado
        "Equinox" -> R.drawable.equinox
        "Impala" -> R.drawable.impala
        "Malibu" -> R.drawable.malibu
        "Spark" -> R.drawable.spark
        "Trailblazer" -> R.drawable.trailblazer
        "Trax" -> R.drawable.trax
        // GENESIS
        "G70" -> R.drawable.g70
        "G80" -> R.drawable.g80
        "GV60" -> R.drawable.gv60
        "GV70" -> R.drawable.gv70
        "GV80" -> R.drawable.gv80
        // TESLA
        "Model3" -> R.drawable.model3
        "ModelS" -> R.drawable.models
        "ModelX" -> R.drawable.modelx
        "ModelY" -> R.drawable.modely
        // RENAULT
        "QM6" -> R.drawable.qm6
        "SM6" -> R.drawable.sm6
        "XM3" -> R.drawable.xm3
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "차량 등록",
            style = MaterialTheme.typography.headlineMedium
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { expandedBrand = true },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (selectedBrand.isEmpty()) "차량 브랜드" else selectedBrand)
            }

            DropdownMenu(
                expanded = expandedBrand,
                onDismissRequest = { expandedBrand = false },
                modifier = Modifier.fillMaxWidth(0.925f)
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

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { expandedVehicleType = true },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (selectedVehicleType.isEmpty()) "차량 종류" else selectedVehicleType)
            }

            DropdownMenu(
                expanded = expandedVehicleType,
                onDismissRequest = { expandedVehicleType = false },
                modifier = Modifier.fillMaxWidth(0.925f)
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

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = licensePlate,
                    onValueChange = { licensePlate = it },
                    label = { Text("차량 번호를 입력하세요") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Gray,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Red
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier
                        .heightIn(min = 56.dp)
                        .weight(0.3f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("확인")
                }
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

        if (selectedVehicleType.isNotEmpty() && imageResId != null) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = selectedVehicleType,
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 16.dp)
            )
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
