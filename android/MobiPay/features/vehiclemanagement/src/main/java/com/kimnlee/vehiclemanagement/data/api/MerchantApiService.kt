package com.kimnlee.vehiclemanagement.data.api

import com.kimnlee.vehiclemanagement.data.model.AutoPaymentStatusRequest
import com.kimnlee.vehiclemanagement.data.model.AutoPaymentStatusResponse
import com.kimnlee.vehiclemanagement.data.model.CarMembersResponse
import com.kimnlee.vehiclemanagement.data.model.PaidParkingLotResponse
import com.kimnlee.vehiclemanagement.data.model.VehicleListResponse
import com.kimnlee.vehiclemanagement.data.model.VehicleRegistrationRequest
import com.kimnlee.vehiclemanagement.data.model.VehicleRegistrationResponse
import retrofit2.Response
import retrofit2.http.*

interface MerchantApiService {
    // 차량의 주차여부 조회
    @GET("api/v1/merchants/parking/cars/{car_number}/entry")
    suspend fun getIfUsingPaidParkingLot(@Path("car_number") carNumber: String): Response<PaidParkingLotResponse>
}