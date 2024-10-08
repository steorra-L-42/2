package com.kimnlee.mobipay.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.common.event.EventBus
import com.kimnlee.common.event.NewNotificationEvent
import com.kimnlee.common.network.ApiClient
import com.kimnlee.common.network.NaverMapService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.kimnlee.vehiclemanagement.data.api.VehicleApiService
import com.kimnlee.vehiclemanagement.data.model.CarMember
import com.kimnlee.vehiclemanagement.data.model.VehicleItem
import com.kimnlee.common.auth.AuthManager

class HomeViewModel (apiClient: ApiClient, private val authManager: AuthManager) : ViewModel() {
    private val _naverMapService = MutableStateFlow<NaverMapService?>(apiClient.naverMapService)
    val naverMapService: StateFlow<NaverMapService?> = _naverMapService

    private val _hasNewNotifications = MutableStateFlow(false)
    val hasNewNotifications: StateFlow<Boolean> = _hasNewNotifications

    private val vehicleService: VehicleApiService = apiClient.authenticatedApi.create(VehicleApiService::class.java)

    private val _vehicles = MutableStateFlow<List<VehicleItem>>(emptyList())
    val vehicles: StateFlow<List<VehicleItem>> = _vehicles

    private val _carMembers = MutableStateFlow<List<CarMember>>(emptyList())
    val carMembers: StateFlow<List<CarMember>> = _carMembers

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _userPhoneNumber = MutableStateFlow("")
    val userPhoneNumber: StateFlow<String> = _userPhoneNumber

    init {
        viewModelScope.launch {
            EventBus.events.collectLatest { event ->
                when (event) {
                    is NewNotificationEvent -> updateNotificationStatus(event.hasNew)
                }
            }
        }
        getUserVehicles()
        loadUserName()
        loadUserPhoneNumber()
    }

    private fun loadUserName() {
        val userInfo = authManager.getUserInfo()
        _userName.value = userInfo.name
    }

    private fun updateNotificationStatus(hasNew: Boolean) {
        _hasNewNotifications.value = hasNew
    }

    fun markNotificationsAsRead() {
        viewModelScope.launch {
            updateNotificationStatus(false)
        }
    }

    private fun getUserVehicles() {
        viewModelScope.launch {
            try {
                val response = vehicleService.getUserVehicleList()
                if (response.isSuccessful) {
                    response.body()?.let { listResponse ->
                        _vehicles.value = listResponse.items
                    }
                }
            } catch (e: Exception) {
                Log.d("HomeViewModel", "Error fetching user vehicles: ${e.message}")
            }
        }
    }

    fun getCarMembers(carId: Int) {
        viewModelScope.launch {
            try {
                val response = vehicleService.getVehicleMembers(carId)
                if (response.isSuccessful) {
                    response.body()?.let { carMembersResponse ->
                        val vehicle = _vehicles.value.find { it.carId == carId }
                        val sortedMembers = carMembersResponse.items.sortedWith(
                            compareBy<CarMember> {
                                it.phoneNumber != _userPhoneNumber.value
                            }.thenBy { it.name }
                        )
                        _carMembers.value = sortedMembers
                    }
                } else {
                    Log.e("HomeViewModel", "Failed to get car members: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error getting car members", e)
            }
        }
    }

    fun refreshVehicles() {
        getUserVehicles()
    }

    private fun loadUserPhoneNumber() {
        val userInfo = authManager.getUserInfo()
        _userPhoneNumber.value = userInfo.phoneNumber
    }
}