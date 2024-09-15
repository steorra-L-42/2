package com.kimnlee.cardmanagement.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.cardmanagement.data.model.Photos
import com.kimnlee.cardmanagement.data.model.User
import com.kimnlee.cardmanagement.data.repository.CardManagementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CardManagementViewModel : ViewModel() {
    private val repository = CardManagementRepository()

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    private val _photouiState = MutableStateFlow<PhotoUiState>(PhotoUiState.Loading)
    val uiState: StateFlow<UserUiState> = _uiState
    val photoUiState: StateFlow<PhotoUiState> = _photouiState

    init {
        fetchUsers()
        fetchPhotos()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading
            try {
                val users = repository.getUsers()
                _uiState.value = UserUiState.Success(users)
            } catch (e: Exception) {
                _uiState.value = UserUiState.Error("Failed to fetch users: ${e.message}")
            }
        }
    }
    fun fetchPhotos() {
        viewModelScope.launch {
            _photouiState.value = PhotoUiState.Loading
            try {
                val photos = repository.getPhotos()
                _photouiState.value = PhotoUiState.Success(photos)
            } catch (e: Exception) {
                _photouiState.value = PhotoUiState.Error("Failed to fetch users: ${e.message}")
            }
        }
    }
}

sealed class UserUiState {
    object Loading : UserUiState()
    data class Success(val users: List<User>) : UserUiState()
    data class Error(val message: String) : UserUiState()
}
sealed class PhotoUiState {
    object Loading : PhotoUiState()
    data class Success(val photos: List<Photos>) : PhotoUiState()
    data class Error(val message: String) : PhotoUiState()
}
