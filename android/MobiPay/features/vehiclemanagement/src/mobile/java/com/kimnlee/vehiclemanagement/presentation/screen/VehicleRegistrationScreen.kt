package com.kimnlee.vehiclemanagement.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel

@Composable
fun VehicleRegistrationScreen(
    onNavigateBack: () -> Unit,
    viewModel: VehicleManagementViewModel = viewModel()
) {
    var licensePlate by remember { mutableStateOf("") }

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
