package com.kimnlee.vehiclemanagement.data.api

import com.kimnlee.vehiclemanagement.data.model.VehicleListResponse
import com.kimnlee.vehiclemanagement.data.model.VehicleRegistrationRequest
import com.kimnlee.vehiclemanagement.data.model.VehicleRegistrationResponse
import retrofit2.Response
import retrofit2.http.*

interface VehicleApiService {
    @GET("api/v1/cars")
    suspend fun getUserVehicleList(): Response<VehicleListResponse>

    @POST("api/v1/cars")
    suspend fun registerVehicle(@Body vehicleRegistrationRequest: VehicleRegistrationRequest): Response<VehicleRegistrationResponse>
}