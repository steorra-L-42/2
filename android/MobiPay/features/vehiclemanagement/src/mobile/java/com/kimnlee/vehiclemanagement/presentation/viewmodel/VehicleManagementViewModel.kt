package com.kimnlee.vehiclemanagement.presentation.viewmodel

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.kimnlee.vehiclemanagement.R
import com.kimnlee.vehiclemanagement.data.api.VehicleApiService
import com.kimnlee.vehiclemanagement.data.model.AutoPaymentStatusRequest
import com.kimnlee.vehiclemanagement.data.model.CarMember
import com.kimnlee.vehiclemanagement.data.model.VehicleItem
import com.kimnlee.vehiclemanagement.data.model.VehicleRegistrationRequest
import kotlinx.coroutines.flow.update
import com.kimnlee.common.event.EventBus
import com.kimnlee.common.event.NewNotificationEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


data class Vehicle(
    val carId: Int,
    val number: String,
    val created: String,
    val autoPayStatus: Boolean,
    val ownerId: Int,
    val carModel: String
)

class VehicleManagementViewModel(
    private val apiClient: ApiClient,
    private val context: Context,
    private val authManager: AuthManager
) : ViewModel() {

    private val _userPhoneNumber = MutableStateFlow("")
    val userPhoneNumber: StateFlow<String> = _userPhoneNumber

    private val vehicleService: VehicleApiService = apiClient.authenticatedApi.create(VehicleApiService::class.java)

    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles

    private val _apiVehicles = MutableStateFlow<List<VehicleItem>>(emptyList())
    val apiVehicles: StateFlow<List<VehicleItem>> = _apiVehicles

    private val _registrationStatus = MutableStateFlow<RegistrationStatus>(RegistrationStatus.Idle)
    val registrationStatus: StateFlow<RegistrationStatus> = _registrationStatus

    private val _carMembers = MutableStateFlow<List<CarMember>>(emptyList())
    val carMembers: StateFlow<List<CarMember>> = _carMembers

    private val _autoPaymentStatuses = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val autoPaymentStatuses: StateFlow<Map<Int, Boolean>> = _autoPaymentStatuses

    private val _autoPaymentStatus = MutableStateFlow<Boolean>(false)
    val autoPaymentStatus: StateFlow<Boolean> = _autoPaymentStatus

    private val _hasNewNotifications = MutableStateFlow<Boolean>(false)
    val hasNewNotifications: StateFlow<Boolean> = _hasNewNotifications

    init {
        viewModelScope.launch {
            EventBus.events.collectLatest { event ->
                when (event) {
                    is NewNotificationEvent -> updateNotificationStatus(event.hasNew)
                }
            }
        }
        loadUserPhoneNumber()
    }

    private fun loadUserPhoneNumber() {
        viewModelScope.launch {
            try {
                val userInfo = authManager.getUserInfo()
                _userPhoneNumber.value = userInfo.phoneNumber
            } catch (e: Exception) {
                Log.e("VehicleManagementViewModel", "Error loading user phone number", e)
                _userPhoneNumber.value = ""
            }
        }
    }

    // 사용자가 소속된 차량의 목록 불러오기
    fun getUserVehicles() {
        viewModelScope.launch {
            try {
                val response = vehicleService.getUserVehicleList()
                if (response.isSuccessful) {
                    val vehicleResponse = response.body()
                    vehicleResponse?.let { listResponse ->
                        _apiVehicles.value = listResponse.items

                        // API 응답을 기존 Vehicle 형식으로 변환하고 자동 결제 상태 맵 업데이트
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

                        // 각 차량의 자동 결제 상태를 맵에 저장
                        _autoPaymentStatuses.value = listResponse.items.associate {
                            it.carId to it.autoPayStatus
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

    fun getAutoPaymentStatus(carId: Int): Boolean {
        return _autoPaymentStatuses.value[carId] ?: false
    }

    // 차량 등록
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

    // 차량에 포함된 멤버 조회
    fun requestCarMembers(carId: Int) {
        viewModelScope.launch {
            try {
                val response = vehicleService.getVehicleMembers(carId)
                if (response.isSuccessful) {
                    response.body()?.let { carMembersResponse ->
                        val vehicle = _vehicles.value.find { it.carId == carId }
                        val ownerId = vehicle?.ownerId
                        val sortedMembers = carMembersResponse.items.sortedWith(
                            compareBy<CarMember> { member ->
                                when {
                                    member.memberId == ownerId -> 0 // 오너를 첫 번째로
                                    member.phoneNumber == _userPhoneNumber.value -> 1 // 현재 사용자를 두 번째로 (오너가 아닌 경우)
                                    else -> 2 // 다른 멤버들
                                }
                            }.thenBy { it.name } // 각 그룹 내에서 이름순으로 정렬
                        )
                        _carMembers.value = sortedMembers
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

    // 차량 자동결제 사용 토글
    fun toggleAutoPayment(carId: Int, autoPayStatus: Boolean) {
        viewModelScope.launch {
            try {
                val request = AutoPaymentStatusRequest(carId, autoPayStatus)
                val response = vehicleService.updateAutoPaymentStatus(request)
                if (response.isSuccessful) {
                    val updatedVehicle = response.body()
                    updatedVehicle?.let {
                        _vehicles.update { vehicles ->
                            vehicles.map { vehicle ->
                                if (vehicle.carId == carId) {
                                    vehicle.copy(autoPayStatus = it.autoPayStatus)
                                } else {
                                    vehicle
                                }
                            }
                        }
                        // 개별 차량의 자동 결제 상태 업데이트
                        _autoPaymentStatuses.update { statuses ->
                            statuses + (carId to it.autoPayStatus)
                        }

                        // 현재 선택된 차량(디테일 페이지)의 자동 결제 상태 업데이트
                        _autoPaymentStatus.value = it.autoPayStatus
                        Log.d(TAG, "Auto payment status updated successfully for car $carId")
                    }
                } else {
                    Log.e(TAG, "Failed to update auto payment status for car $carId: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating auto payment status for car $carId", e)
            }
        }
    }

    fun initializeAutoPaymentStatus(carId: Int) {
        viewModelScope.launch {
            val status = getAutoPaymentStatus(carId)
            _autoPaymentStatus.value = status
        }
    }

    // 알림 상태 업데이트
    fun updateNotificationStatus(hasNew: Boolean) {
        _hasNewNotifications.value = hasNew
    }

    // 알림을 읽으면 호출
    fun markNotificationsAsRead() {
        updateNotificationStatus(false)
    }

    fun startRefreshingCycle(carId: Int) {
        viewModelScope.launch {
            repeat(7) {
                requestCarMembers(carId)
                delay(2000)
            }
        }
    }
}

sealed class RegistrationStatus {
    object Idle : RegistrationStatus()
    object Loading : RegistrationStatus()
    object Success : RegistrationStatus()
    data class Error(val message: String) : RegistrationStatus()
}