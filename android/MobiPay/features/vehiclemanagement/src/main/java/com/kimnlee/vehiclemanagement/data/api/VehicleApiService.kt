package com.kimnlee.vehiclemanagement.data.api

import com.kimnlee.vehiclemanagement.data.model.AutoPaymentStatusRequest
import com.kimnlee.vehiclemanagement.data.model.AutoPaymentStatusResponse
import com.kimnlee.vehiclemanagement.data.model.CarMembersResponse
import com.kimnlee.vehiclemanagement.data.model.VehicleListResponse
import com.kimnlee.vehiclemanagement.data.model.VehicleRegistrationRequest
import com.kimnlee.vehiclemanagement.data.model.VehicleRegistrationResponse
import retrofit2.Response
import retrofit2.http.*

interface VehicleApiService {

    // 사용자의 차량 목록 조회
    @GET("api/v1/cars")
    suspend fun getUserVehicleList(): Response<VehicleListResponse>

    // 차량 등록
    @POST("api/v1/cars")
    suspend fun registerVehicle(@Body vehicleRegistrationRequest: VehicleRegistrationRequest): Response<VehicleRegistrationResponse>

    // 차량에 포함된 멤버 조회
    @GET("api/v1/cars/{car_id}/members")
    suspend fun getVehicleMembers(@Path("car_id") carId: Int): Response<CarMembersResponse>

    // 차량 자동결제 토글
    @PATCH("api/v1/cars/auto-pay")
    suspend fun updateAutoPaymentStatus(@Body autoPaymentStatusRequest: AutoPaymentStatusRequest): Response<AutoPaymentStatusResponse>
}