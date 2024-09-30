package com.kimnlee.vehiclemanagement.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.kimnlee.vehiclemanagement.R

data class Vehicle(val carId: Int, val number: String, val imageResId: Int)

class VehicleManagementViewModel : ViewModel() {
    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles

    init {
        // 더미 데이터로 초기화(실제로는 데이터베이스에서 가져와야 합니다)
        viewModelScope.launch {
            _vehicles.value = listOf(
                Vehicle(1, "123가 4567", R.drawable.genesis_g90),
                Vehicle(2, "258하 1302", R.drawable.genesis_g90)
            )
        }
    }

    fun addVehicle(number: String) {
        viewModelScope.launch {
            val updatedList = _vehicles.value.toMutableList()
            val newVehicle = Vehicle(
                carId = updatedList.size + 1,
                number = number,
                imageResId = R.drawable.genesis_g90 // 기본 이미지 설정
            )
            updatedList.add(newVehicle)
            _vehicles.value = updatedList // 새로운 리스트를 다시 할당(실제로는 데이터베이스에 저장)
            println("New vehicle list size: ${_vehicles.value.size}")
        }
    }

    fun getVehicleById(id: Int): Vehicle? {
        return _vehicles.value.find { it.carId == id }
    }
}
