package com.kimnlee.mobipay.presentation.screen

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.view.Gravity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.kimnlee.auth.presentation.viewmodel.LoginViewModel
import com.kimnlee.common.BuildConfig
import com.kimnlee.common.R
import com.kimnlee.common.auth.repository.NaverMapRepository
import com.kimnlee.common.network.ApiClient
import com.kimnlee.common.network.NaverMapService
import com.kimnlee.common.ui.theme.MobiCardBgGray
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.common.ui.theme.MobiTextAlmostBlack
import com.kimnlee.mobipay.presentation.viewmodel.HomeViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.graphicsLayer
import coil.compose.AsyncImage
import com.kimnlee.common.utils.CarModelImageProvider
import com.kimnlee.vehiclemanagement.data.model.CarMember
import com.kimnlee.vehiclemanagement.data.model.VehicleItem

private val YOUR_CLIENT_SECRET = BuildConfig.NAVER_MAP_CLIENT_SECRET

@Composable
fun HomeScreen(
    loginViewModel: LoginViewModel,
    homeViewModel: HomeViewModel,
    navController: NavController,
    context: Context
) {
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
    var lastLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    val naverMapService by homeViewModel.naverMapService.collectAsState()
    val hasNewNotifications by homeViewModel.hasNewNotifications.collectAsState()
    val vehicles by homeViewModel.vehicles.collectAsState()
    val carMembers by homeViewModel.carMembers.collectAsState()
    val currentVehicle = vehicles.firstOrNull()
    val userName by homeViewModel.userName.collectAsState()
    val userPhoneNumber by homeViewModel.userPhoneNumber.collectAsState()



    LaunchedEffect(vehicles) {
        if (vehicles.isNotEmpty()) {
            homeViewModel.getCarMembers(vehicles.first().carId)
        }
    }

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
        snapshotFlow { navController.currentBackStackEntry }
            .collect {
                homeViewModel.refreshVehicles()
            }
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
                    text = " ${userName} 님, 반가워요!",
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
                        IconButton(
                            onClick = {
                                navController.navigate("notification_main")
                                homeViewModel.markNotificationsAsRead()
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            val notificationIcon = if (hasNewNotifications) {
                                painterResource(id = R.drawable.bell_new)
                            } else {
                                painterResource(id = R.drawable.bell)
                            }
                            Icon(
                                painter = notificationIcon,
                                contentDescription = "알림",
                                tint = Color.Unspecified
                            )
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.height(16.dp))

            if (vehicles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clickable {
                            navController.navigate("vehiclemanagement_registration")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.no_car),
                            contentDescription = "No car image",
                            modifier = Modifier
                                .size(200.dp)
                        )
                        Text(
                            text = "차량을 등록해주세요",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MobiTextAlmostBlack,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                }
                }
            } else {
                val firstVehicle = vehicles.first()
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
                            painter = painterResource(id = CarModelImageProvider.getImageResId(firstVehicle.carModel)),
                            contentDescription = "차량 이미지",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.FillWidth
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        TextOnLP(formatLicensePlate(firstVehicle.number))

                        Spacer(modifier = Modifier.height(28.dp))
                        CarUserIconsRow(carMembers = carMembers, userPhoneNumber = userPhoneNumber)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
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
                    .height(300.dp)
                    .background(MobiCardBgGray)
                    .padding(24.dp, 18.dp, 24.dp, 12.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                    ){
                        Text(
                            text = "\uE116",
//                            text = "\uD83D\uDCCD",
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
                        NaverMapView(lastLocation, naverMapService)
                    }
                }
            }
        }
    }
}

@Composable
fun NaverMapView(lastLocation: Pair<Double, Double>?, naverMapService: NaverMapService?) {

    val context = LocalContext.current
    var mapView = remember { MapView(context) }

    lateinit var address : String
    // 주차 정보가 없으면 기본 위치 표시
    val lastLocationLatLng = lastLocation?.let { LatLng(it.first, it.second) } ?: LatLng(
        36.107368, 128.425046) // 37.526665, 126.927127
    runBlocking {
        val repository = NaverMapRepository("81dn8nvzim", YOUR_CLIENT_SECRET, naverMapService)
        address = repository.getAddressFromCoords(lastLocationLatLng)
    }

    Column {
        AndroidView(
            factory = { mapView },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
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

        Text(text = address, fontSize = 16.sp, letterSpacing = 0.1.sp, lineHeight = 13.sp)
    }
}


@Composable
fun TextOnLP(number: String) {
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
                    text = number,
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

@Composable
fun CarUserIconsRow(carMembers: List<CarMember>, userPhoneNumber: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        carMembers.take(3).forEachIndexed { index, member ->
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Transparent)
            ) {
                AsyncImage(
                    model = member.picture,
                    contentDescription = member.name,
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (index == 0) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_crown),
                        contentDescription = "오너",
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopStart)
                            .offset(x = (-10).dp, y = (-10).dp)
                            .graphicsLayer(rotationZ = -45f)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (carMembers.size > 3) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+${carMembers.size - 3}",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEEEEE)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add member",
                tint = Color.Black
            )
        }
    }
}

fun formatLicensePlate(number: String): String {
    return number.reversed().chunked(4)
        .joinToString(" ")
        .reversed()
}