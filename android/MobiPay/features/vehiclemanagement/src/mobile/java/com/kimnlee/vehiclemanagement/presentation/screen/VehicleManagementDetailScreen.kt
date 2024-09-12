package com.kimnlee.vehiclemanagement.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun VehicleManagementDetailScreen(
    vehicleId: Int,
    onNavigateBack: () -> Unit,
    viewModel: VehicleManagementViewModel = viewModel()
) {
    val vehicle = viewModel.getVehicleById(vehicleId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        vehicle?.let {
            Image(
                painter = painterResource(id = it.imageResId),
                contentDescription = "Vehicle Image",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 16.dp)
            )
            Text(
                text = "차량 번호: ${it.name}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "차량 ID: ${it.id}",
                style = MaterialTheme.typography.bodyLarge
            )
        } ?: Text("차량을 찾을 수 없습니다.")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onNavigateBack) {
            Text("뒤로 가기")
        }
    }
}