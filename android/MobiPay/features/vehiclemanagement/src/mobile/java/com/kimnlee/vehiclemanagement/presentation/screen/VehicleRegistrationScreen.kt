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


@Composable
fun VehicleRegistrationScreen(
    onNavigateBack: () -> Unit,
    viewModel: VehicleManagementViewModel = viewModel()
) {
    var licensePlate by remember { mutableStateOf("") }
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // 권한 승인 되면 카메라 호출
            } else {
                // 권한 거부된 경우 처리

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

        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                    ) {
                    // 카메라 권한이 이미 허용된 경우 카메라 호출 로직
                } else {
                    // 카메라 권한이 허용되지 않은 경우 권한 요청
                    cameraLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("카메라로 차량 번호 인식")
        }

        OutlinedTextField(
            value = licensePlate,
            onValueChange = { licensePlate = it },
            label = { Text("차량 번호") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.addVehicle(licensePlate) // 실제로는 api 욫청을 보냄
                onNavigateBack() // 차량 등록 후 뒤로 가기
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("등록")
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
