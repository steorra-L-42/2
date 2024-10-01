package com.kimnlee.cardmanagement.presentation.viewmodel

import Card
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.cardmanagement.data.api.CardManagementApiService
import com.kimnlee.cardmanagement.data.model.Photos
import com.kimnlee.cardmanagement.data.model.User
import com.kimnlee.cardmanagement.data.repository.CardManagementRepository
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CardManagementViewModel(
    private val authManager: AuthManager,
    private val apiClient: ApiClient) : ViewModel() {

    private val cardManagementService: CardManagementApiService = apiClient.authenticatedApi.create(CardManagementApiService::class.java)

    private val _photoUiState = MutableStateFlow<PhotoUiState>(PhotoUiState.Loading)
    val photoUiState: StateFlow<PhotoUiState> = _photoUiState

    private val _cardUiState = MutableStateFlow<CardUiState>(CardUiState.Loading)
    val cardUiState: StateFlow<CardUiState> = _cardUiState

    init {
        fetchPhotos()
        requestUserCards()
    }

    fun fetchPhotos() {
        viewModelScope.launch {
            _photoUiState.value = PhotoUiState.Loading
            try {
                val photos = cardManagementService.getPhotos()
                _photoUiState.value = PhotoUiState.Success(photos)
            } catch (e: Exception) {
                _photoUiState.value = PhotoUiState.Error("Failed to fetch users: ${e.message}")
            }
        }
    }

    fun requestUserCards() {
        viewModelScope.launch {
            _cardUiState.value = CardUiState.Loading
            try {
                val response = cardManagementService.getUserCards()
                if (response.isSuccessful) {
                    val cardList = response.body()?.items ?: emptyList()
                    _cardUiState.value = CardUiState.Success(cardList)
                    Log.d(TAG, "카드 목록 받아오기 성공: ${cardList.size} 개의 카드")
                } else {
                    _cardUiState.value = CardUiState.Error("Failed to fetch cards: ${response.code()}")
                }
            } catch (e: Exception) {
                _cardUiState.value = CardUiState.Error("Failed to fetch cards: ${e.message}")
            }
        }
    }
}

sealed class PhotoUiState {
    object Loading : PhotoUiState()
    data class Success(val photos: List<Photos>) : PhotoUiState()
    data class Error(val message: String) : PhotoUiState()
}

sealed class CardUiState {
    object Loading : CardUiState()
    data class Success(val cards: List<Card>) : CardUiState()
    data class Error(val message: String) : CardUiState()
}