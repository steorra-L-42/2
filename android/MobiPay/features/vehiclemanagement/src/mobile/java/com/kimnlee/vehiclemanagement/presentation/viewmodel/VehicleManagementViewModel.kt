package com.kimnlee.vehiclemanagement.presentation.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.common.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.kimnlee.vehiclemanagement.R
import com.kimnlee.vehiclemanagement.data.api.VehicleApiService
import com.kimnlee.vehiclemanagement.data.model.CarMember
import com.kimnlee.vehiclemanagement.data.model.VehicleItem
import com.kimnlee.vehiclemanagement.data.model.VehicleRegistrationRequest

data class Vehicle(
    val carId: Int,
    val number: String,
    val created: String,
    val autoPayStatus: Boolean,
    val ownerId: Int,
    val carModel: String
)

class VehicleManagementViewModel(
    private val apiClient: ApiClient
) : ViewModel() {

    private val vehicleService: VehicleApiService = apiClient.authenticatedApi.create(VehicleApiService::class.java)

    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles

    private val _apiVehicles = MutableStateFlow<List<VehicleItem>>(emptyList())
    val apiVehicles: StateFlow<List<VehicleItem>> = _apiVehicles

    private val _registrationStatus = MutableStateFlow<RegistrationStatus>(RegistrationStatus.Idle)
    val registrationStatus: StateFlow<RegistrationStatus> = _registrationStatus

    private val _carMembers = MutableStateFlow<List<CarMember>>(emptyList())
    val carMembers: StateFlow<List<CarMember>> = _carMembers

    init {
        getUserVehicles()
    }

    // 사용자가 소속된 차량의 목록 불러오기
    private fun getUserVehicles() {
        viewModelScope.launch {
            try {
                val response = vehicleService.getUserVehicleList()
                if (response.isSuccessful) {
                    val vehicleResponse = response.body()
                    vehicleResponse?.let { listResponse ->
                        _apiVehicles.value = listResponse.items

                        // API 응답을 기존 Vehicle 형식으로 변환
                        _vehicles.value = listResponse.items.map { apiVehicle ->
                            Vehicle(
                                carId = apiVehicle.carId,
                                number = apiVehicle.number,
                                created = apiVehicle.created,
                                autoPayStatus = apiVehicle.autoPayStatus,
                                ownerId = apiVehicle.ownerId,
                                carModel = apiVehicle.carModel
                            )
                        }
                    }
                    Log.d(TAG, "차량 목록 가져오기 성공")
                } else {
                    Log.e("VehicleManagementViewModel", "Failed to get vehicles: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("VehicleManagementViewModel", "Error getting vehicles", e)
            }
        }
    }

    fun registerVehicle(number: String, carModel: String) {
        viewModelScope.launch {
            _registrationStatus.value = RegistrationStatus.Loading
            try {
                val registrationRequest = VehicleRegistrationRequest(number, carModel)
                val response = vehicleService.registerVehicle(registrationRequest)
                if (response.isSuccessful) {
                    val registeredVehicle = response.body()
                    registeredVehicle?.let {
                        val newVehicle = Vehicle(
                            carId = it.carId,
                            number = it.number,
                            created = it.created,
                            autoPayStatus = it.autoPayStatus,
                            ownerId = it.ownerId,
                            carModel = it.carModel
                        )
                        _vehicles.value = _vehicles.value + newVehicle
                        _apiVehicles.value += VehicleItem(
                            carId = it.carId,
                            number = it.number,
                            created = it.created,
                            autoPayStatus = it.autoPayStatus,
                            ownerId = it.ownerId,
                            carModel = it.carModel
                        )
                        _registrationStatus.value = RegistrationStatus.Success
                        Log.d(TAG, "차량 등록 성공")
                    }
                } else {
                    Log.e("VehicleManagementViewModel", "Failed to register vehicle: ${response.code()}")
                    _registrationStatus.value = RegistrationStatus.Error("Registration failed: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("VehicleManagementViewModel", "Error registering vehicle", e)
                _registrationStatus.value = RegistrationStatus.Error("Registration failed: ${e.message}")
            }
        }
    }

    fun requestCarMembers(carId: Int) {
        viewModelScope.launch {
            try {
                val response = vehicleService.getVehicleMembers(carId)
                if (response.isSuccessful) {
                    response.body()?.let { carMembersResponse ->
                        _carMembers.value = carMembersResponse.items
                    }
                } else {
                    Log.e(TAG, "Failed to get car members: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting car members", e)
            }
        }
    }

    fun getVehicleById(id: Int): Vehicle? {
        return _vehicles.value.find { it.carId == id }
    }
}

sealed class RegistrationStatus {
    object Idle : RegistrationStatus()
    object Loading : RegistrationStatus()
    object Success : RegistrationStatus()
    data class Error(val message: String) : RegistrationStatus()
}