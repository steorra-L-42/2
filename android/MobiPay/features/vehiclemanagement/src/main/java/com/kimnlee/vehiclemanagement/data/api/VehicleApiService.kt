package com.kimnlee.vehiclemanagement.data.api

import com.kimnlee.vehiclemanagement.presentation.viewmodel.Vehicle
import retrofit2.http.*

data class VehiclesResponse(val items: List<VehicleDto>)

data class VehicleDto(
    val carId: Int,
    val number: String,
    val created: String,
    val autoPayStatus: Boolean,
    val ownerId: Int,
)

interface VehicleApiService {
    @GET("api/v1/cars")
    suspend fun getVehicles(
    ): VehiclesResponse
}