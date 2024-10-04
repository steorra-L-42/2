package com.kimnlee.common.network

import com.kimnlee.common.auth.model.ReverseGeocodeResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverMapService {
    @GET("map-reversegeocode/v2/gc")
    suspend fun reverseGeocode(
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String,
        @Query("coords") coords: String,
        @Query("output") output: String = "json",
        @Query("orders") orders: String = "legalcode,addr" // 지번 주소를 우선으로 설정
    ): ReverseGeocodeResponse
}