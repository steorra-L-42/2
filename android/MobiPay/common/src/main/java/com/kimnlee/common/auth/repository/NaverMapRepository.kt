package com.kimnlee.common.auth.repository

import android.util.Printer
import com.kimnlee.common.network.NaverMapService
import com.naver.maps.geometry.LatLng
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NaverMapRepository(
    private val clientId: String,
    private val clientSecret: String,
    private val api : NaverMapService?
) {
//    private val api = Retrofit.Builder()
//        .baseUrl("https://naveropenapi.apigw.ntruss.com/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//        .create(NaverMapService::class.java)
    suspend fun getAddressFromCoords(latlng : LatLng): String { //latitude: Double, longitude: Double
        val longitude = latlng.longitude
        val latitude = latlng.latitude
        val coords = "$longitude,$latitude"
        val response = api?.reverseGeocode(clientId, clientSecret, coords)
        if (response?.status?.code == 0 && response.results.isNotEmpty()) {
            // 도로명 주소 찾기
            val legalCodeResult  = response.results.find { it.name == "legalcode" }
            if (legalCodeResult != null) {
                return buildString {
                    append(legalCodeResult.region.area1.name) // 시/도
                    append(" ")
                    append(legalCodeResult.region.area2.name) // 구/군
                    append(" ")
                    append(legalCodeResult.region.area3.name) // 동
                    append(" ")
                    // land 정보 처리
                    legalCodeResult.land?.let { land ->
                        if (!land.number1.isNullOrEmpty()) {
                            append(land.number1)
                            if (!land.number2.isNullOrEmpty()) {
                                append("-")
                                append(land.number2)
                            } else {

                            }
                        } else {
                            append("번지 정보 없음")
                        }
                    } ?: append("번지 정보 없음")
                }
            }

            // 도로명 주소가 없으면 지번 주소 반환
            val legalCode = response.results.find { it.name == "legalcode" }
            if (legalCode != null) {
                return "${legalCode.region.area1.name} ${legalCode.region.area2.name} ${legalCode.region.area3.name}"
            }
        }

        return "주소를 찾을 수 없습니다."
    }
}