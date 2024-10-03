package com.kimnlee.mobipay.presentation.screen

import com.kimnlee.common.BuildConfig
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverMapApi {
    @GET("map-reversegeocode/v2/gc")
    suspend fun reverseGeocode(
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String,
        @Query("coords") coords: String,
        @Query("orders") orders: String = "legalcode,roadaddr",
        @Query("output") output: String = "json"
    ): ReverseGeocodeResponse
}

data class ReverseGeocodeResponse(
    val status: Status,
    val results: List<Result>
)

data class Status(
    val code: Int,
    val name: String,
    val message: String
)

data class Result(
    val name: String,
    val code: Code,
    val region: Region,
    val land: Land?
)
data class Land(
    val type: String,
    val number1: String,
    val number2: String,
    val addition0: Addition?,
    val name : String
)

data class Addition(
    val type: String,
    val value: String
)
data class Code(
    val id: String,
    val type: String,
    val mappingId: String
)

data class Region(
    val area0: Area,
    val area1: Area,
    val area2: Area,
    val area3: Area,
    val area4: Area
)

data class Area(
    val name: String,
    val coords: Coords
)

data class Coords(
    val center: Center
)

data class Center(
    val crs: String,
    val x: Double,
    val y: Double
)
class NaverMapRepository(
    private val clientId: String,
    private val clientSecret: String
) {
    private val api = Retrofit.Builder()
        .baseUrl("https://naveropenapi.apigw.ntruss.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NaverMapApi::class.java)

    suspend fun getAddressFromCoords(latitude: Double, longitude: Double): String {
        val coords = "$longitude,$latitude"
        val response = api.reverseGeocode(clientId, clientSecret, coords)

        if (response.status.code == 0 && response.results.isNotEmpty()) {
            // 도로명 주소 찾기
            val roadAddr = response.results.find { it.name == "roadaddr" }
            if (roadAddr != null) {
                return buildString {
                    append(roadAddr.region.area1.name) // 시/도
                    append(" ")
                    append(roadAddr.region.area2.name) // 구/군
                    append(" ")
                    append(roadAddr.land?.name) // 도로명
                    append(" ")
                    append(roadAddr.land?.number1) // 건물번호1
                    if (roadAddr.land!!.number2.isNotEmpty()) {
                        append("-")
                        append(roadAddr.land?.number2) // 건물번호2
                    }
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

fun main() {
    val YOUR_CLIENT_SECRET = BuildConfig.NAVER_MAP_CLIENT_SECRET
    runBlocking {
        val repository = NaverMapRepository("z9pdjednkz", YOUR_CLIENT_SECRET)
        val address = repository.getAddressFromCoords(37.526665, 126.927127) //35.2982640,129.1133567
        println(address)
    }
}