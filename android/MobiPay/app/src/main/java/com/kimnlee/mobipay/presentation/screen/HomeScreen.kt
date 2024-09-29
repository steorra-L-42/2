package com.kimnlee.mobipay.presentation.screen

import android.content.Context
import android.content.SharedPreferences
import android.view.Gravity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.common.R
import com.kimnlee.common.ui.theme.MobiCardBgGray
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
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

    MobiPayTheme {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 2.dp)
            ){
                Text(
                    text = " ☀ ",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily(Font(R.font.emoji)),
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(top = 3.dp)
                )
                Text(
                    text = " 원영님, 반가워요!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MobiTextAlmostBlack,
                    fontSize = 24.sp,
                )
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(25.dp)
                ){
                    Row(
                        modifier = Modifier
                            .padding(end = 8.dp)
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.bell_new),
                            contentDescription = "알림 아이콘",
                            contentScale = ContentScale.Fit
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MobiCardBgGray),
            ) {
                Column(
                    modifier = Modifier
                        .padding(40.dp, 20.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Image(
//                        painter = painterResource(id = com.kimnlee.vehiclemanagement.R.drawable.genesis_g90),
                        painter = painterResource(id = com.kimnlee.mobipay.R.drawable.gv80),
                        contentDescription = "차량 이미지",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    TextOnLP()

                    Spacer(modifier = Modifier.height(28.dp))
                    CarUserIconsRow()
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MobiCardBgGray)
                    .padding(24.dp, 18.dp, 24.dp, 18.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                    ){
                        Text(
                            text = "\uD83C\uDD7F",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily(Font(R.font.emoji)),
                            fontSize = 22.sp,
                            modifier = Modifier
                                .padding(top = 0.dp)
                        )
                        Text(
                            text = "  유료주차장 이용중",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack,
                            fontSize = 21.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(17.dp))
                    Column(
                        modifier = Modifier
                            .padding(start = 5.dp, end = 5.dp)
                            .clip(RoundedCornerShape(6.dp))
                    ){
                        Text(
                            text = "여의도 더현대 지하주차장",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack,
                            fontSize = 22.5.sp,
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        Text(
                            text = "입차: 2024년 9월 29일 오전 10시 11분",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MobiTextAlmostBlack,
                        )
                        Spacer(modifier = Modifier.height(21.dp))
                        Text(
                            text = "2시간 14분 경과",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack,
                            fontSize = 19.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "예상 요금  8,000원",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack,
                            fontSize = 19.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .height(260.dp)
                    .background(MobiCardBgGray)
                    .padding(24.dp, 18.dp, 24.dp, 24.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                    ){
                        Text(
                            text = "\uD83D\uDCCD",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily(Font(R.font.emoji)),
                            fontSize = 22.sp,
                            modifier = Modifier
                                .padding(top = 0.dp)
                        )
                        Text(
                            text = "  여기에 주차했어요!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack,
                            fontSize = 21.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                    ){
                        NaverMapView(lastLocation)
                    }
                }
            }

        }
    }

}

@Composable
fun NaverMapView(lastLocation: Pair<Double, Double>?) {

    val context = LocalContext.current
    var mapView = remember { MapView(context) }

    // 주차 정보가 없으면 기본 위치 표시
    val lastLocationLatLng = lastLocation?.let { LatLng(it.first, it.second) } ?: LatLng(
        37.526665, 126.927127)


    AndroidView(
        factory = { mapView },
        modifier = Modifier
            .fillMaxWidth(),
        update = { view ->
            view.getMapAsync { naverMap ->

                naverMap.moveCamera(CameraUpdate.scrollTo(lastLocationLatLng))

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

@Composable
fun CarUserIconsRow() {
    val userImages = listOf(
        painterResource(id = R.drawable.wy2),
        painterResource(id = R.drawable.hani2),
        painterResource(id = R.drawable.iseo2),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        userImages.forEach { painter ->
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = "멤버 추가 버튼",
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEEEEE))
                    .padding(10.dp)
            )
        }
    }
}

@Composable
fun TextOnLP() {
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
                painter = painterResource(id = R.drawable.mobi_lp),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .width(160.dp)
                    .aspectRatio(aspectRatio)
                    .padding(start = 22.dp, top = 4.dp, end = 2.dp, bottom = 2.dp)
            ){
                Text(
                    text = "383모 3838",
                    color = Color.Black,
                    fontSize = 23.sp,
                    fontFamily = FontFamily(Font(R.font.nsrextrabold)),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

        }
    }
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
