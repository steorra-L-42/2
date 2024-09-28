package com.kimnlee.mobipay.presentation.screen

import android.content.Context
import android.content.SharedPreferences
import android.view.Gravity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.common.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

@Composable
fun HomeScreen(
    viewModel: LoginViewModel,
    navController: NavController,
    context: Context
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    var lastLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }


    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate("auth") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        lastLocation = getLastLocation(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "82가 8282",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.testLogout() // 현재 테스트 로그아웃이라서 나중에 백이랑 연결되면 일반 logout 메서드로 바꾸면 됨
            }
        ) {
            Text("로그아웃")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("vehiclemanagement") }
        ) {
            Text("나의 차 관리")
        }
        Spacer(modifier = Modifier.height(8.dp))
//        Button(
//            onClick = { navController.navigate("memberinvitation") }
//        ) {
//            Text("멤버 초대")
//        }
        Spacer(modifier = Modifier.height(16.dp))

        NaverMapView(lastLocation)

        Button(
            onClick = {
                navController.navigate("paymentsucceed")
            }
        ) {
            Text("Payment Succeed")
        }



    }
}

@Composable
fun NaverMapView(lastLocation: Pair<Double, Double>?) {

    val context = LocalContext.current
    var mapView = remember { MapView(context) }

    // 주차 정보가 없으면 기본 위치 표시
    val lastLocationLatLng = lastLocation?.let { LatLng(it.first, it.second) } ?: LatLng(36.107143, 128.416248)


    AndroidView(
        factory = { mapView },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp)),
        update = { view ->
            view.getMapAsync { naverMap ->

                val cameraUpdate = CameraUpdate.scrollTo(lastLocationLatLng)
                naverMap.moveCamera(cameraUpdate)

                if(lastLocation != null){
                    val marker = Marker()
                    marker.icon = OverlayImage.fromResource(R.drawable.park)
                    marker.position = lastLocationLatLng
                    marker.width = 130
                    marker.height = 130
                    marker.map = naverMap
                }

                naverMap.uiSettings.apply {
                    isZoomControlEnabled = false
                    logoGravity = Gravity.END or Gravity.BOTTOM
                    setLogoMargin(0,0,30,30)
                    isScaleBarEnabled = false
                }

            }
        }
    )
}


private fun getLastLocation(context: Context): Pair<Double, Double>? {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("last_location", Context.MODE_PRIVATE)
    val lat = sharedPreferences.getString("last_latitude", null)
    val lng = sharedPreferences.getString("last_longitude", null)

    return if (lat != null && lng != null) {
        Pair(lat.toDouble(), lng.toDouble())
    } else {
        null
    }
}
