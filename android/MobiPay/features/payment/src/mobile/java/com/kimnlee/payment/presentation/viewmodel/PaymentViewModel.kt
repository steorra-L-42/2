package com.kimnlee.payment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.payment.data.model.Photos
import com.kimnlee.payment.data.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// 내부 로직 뜯어 고쳐야 함!!!
class PaymentViewModel : ViewModel() {
    private val repository = PaymentRepository()
    private val _photoUiState = MutableStateFlow<PhotoUiState>(PhotoUiState.Loading)
    val photoUiState: StateFlow<PhotoUiState> = _photoUiState // 읽기 전용

    init {
        fetchPhotos()
    }
    fun fetchPhotos() {
        viewModelScope.launch {
            _photoUiState.value = PhotoUiState.Loading
            try {
                val photos = repository.getPhotos()
                _photoUiState.value = PhotoUiState.Success(photos)
            } catch (e: Exception) {
                _photoUiState.value = PhotoUiState.Error("Failed to fetch users: ${e.message}")
            }
        }
    }
}

sealed class PhotoUiState {
    object Loading : PhotoUiState()
    data class Success(val photos: List<Photos>) : PhotoUiState()
    data class Error(val message: String) : PhotoUiState()
}
