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
import androidx.compose.ui.text.font.FontWeight
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.common.utils.CarModelImageProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleManagementScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToRegistration: () -> Unit,
    viewModel: VehicleManagementViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.getUserVehicles()
    }

    val vehicles by viewModel.vehicles.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🚗",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily(Font(com.kimnlee.common.R.font.emoji)),
                            fontSize = 24.sp,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "차량 관리",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(vehicles) { vehicle ->
                VehicleItem(vehicle, onClick = { onNavigateToDetail(vehicle.carId) })
            }

            item {
                AddVehicleCard(onNavigateToRegistration)
            }
        }
    }
}

@Composable
fun AddVehicleCard(onClick: () -> Unit) {
    Spacer(modifier = Modifier.height(8.dp))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "차량 등록하러 가기",
                    modifier = Modifier.size(48.dp),
                    tint = MobiTextAlmostBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "차량 등록하러 가기",
                    style = MaterialTheme.typography.titleMedium,
                    color = MobiTextAlmostBlack
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
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