package com.kimnlee.vehiclemanagement.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kimnlee.vehiclemanagement.presentation.viewmodel.VehicleManagementViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import com.kimnlee.vehiclemanagement.presentation.viewmodel.Vehicle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import com.kimnlee.vehiclemanagement.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.kimnlee.common.ui.theme.MobiCardBgGray
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import com.kimnlee.common.utils.CarModelImageProvider

@Composable
fun VehicleManagementScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToRegistration: () -> Unit,
    viewModel: VehicleManagementViewModel
) {
    val vehicles by viewModel.vehicles.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "차량 관리",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(vehicles) { vehicle ->
                VehicleItem(vehicle, onClick = { onNavigateToDetail(vehicle.carId) })
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                AddVehicleCard(onNavigateToRegistration)
            }
        }
    }
}

@Composable
fun AddVehicleCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MobiCardBgGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AddCircle,
                contentDescription = "차량 등록하러 가기",
                modifier = Modifier.size(48.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("차량 등록하러 가기", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun VehicleItem(vehicle: Vehicle, onClick: () -> Unit) {
    val imageResId = CarModelImageProvider.getImageResId(vehicle.carModel)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MobiCardBgGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 20.dp)
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "차량 이미지",
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(96.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextOnLP(formatLicensePlate(vehicle.number))
        }
    }
}

@Composable
fun TextOnLP(vehicleNumber: String) {
    val aspectRatio = 949f / 190f
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Box(
            modifier = Modifier
                .width(162.dp)
                .aspectRatio(aspectRatio)
        ) {
            Image(
                painter = painterResource(id = com.kimnlee.common.R.drawable.mobi_lp),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .width(160.dp)
                    .aspectRatio(aspectRatio)
                    .padding(start = 20.dp, top = 4.dp, end = 2.dp, bottom = 2.dp)
            ){
                Text(
                    text = vehicleNumber,
                    color = Color.Black,
                    fontSize = 23.sp,
                    fontFamily = FontFamily(Font(com.kimnlee.common.R.font.nsrextrabold)),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

        }
    }
}