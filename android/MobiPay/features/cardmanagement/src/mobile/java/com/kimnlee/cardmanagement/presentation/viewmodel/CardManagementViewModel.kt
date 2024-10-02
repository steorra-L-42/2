package com.kimnlee.cardmanagement.presentation.viewmodel

import OwnedCard
import Photos
import RegistrationCard
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.model.User
import com.kimnlee.cardmanagement.data.api.CardManagementApiService
import com.kimnlee.cardmanagement.data.repository.CardManagementRepository
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.network.ApiClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardManagementViewModel(
    private val authManager: AuthManager,
    private val apiClient: ApiClient
) : ViewModel() {

    private val cardManagementService: CardManagementApiService =
        apiClient.authenticatedApi.create(CardManagementApiService::class.java)

    // 더미 데이터 용
    private val _photoUiState = MutableStateFlow<PhotoUiState>(PhotoUiState.Loading)
    val photoUiState: StateFlow<PhotoUiState> = _photoUiState

    // 소유 카드 상태
    private val _ownedCardUiState = MutableStateFlow<OwnedCardUiState>(OwnedCardUiState.Loading)
    val ownedCardUiState: StateFlow<OwnedCardUiState> = _ownedCardUiState

    // 등록 카드 상태
    private val _registrationCardUiState =
        MutableStateFlow<RegistrationCardUiState>(RegistrationCardUiState.Loading)
    val registrationCardUiState: StateFlow<RegistrationCardUiState> = _registrationCardUiState

    // 바텀 시트
    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

//    private val _ownedCardList = MutableSharedFlow<List<OwnedCard>>()
//    val ownedCardList: SharedFlow<List<OwnedCard>> = _ownedCardList.asSharedFlow()

    init {
        fetchPhotos()
        requestUserCards()
        requestRegistrationCards()
    }


    // 더미 데이터 용
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
            _ownedCardUiState.value = OwnedCardUiState.Loading
            try {
                val response = cardManagementService.getOwnedCards()
                if (response.isSuccessful) {
                    val cardList = response.body()?.items ?: emptyList()
                    _ownedCardUiState.value = OwnedCardUiState.Success(cardList)
                    Log.d(TAG, "카드 목록 받아오기 성공: ${cardList.size} 개의 카드")
                } else {
                    _ownedCardUiState.value =
                        OwnedCardUiState.Error("Failed to fetch cards: ${response.code()}")
                }
            } catch (e: Exception) {
                _ownedCardUiState.value =
                    OwnedCardUiState.Error("Failed to fetch cards: ${e.message}")
            }
        }
    }

    fun requestRegistrationCards() {
        viewModelScope.launch {
            _registrationCardUiState.value = RegistrationCardUiState.Loading
            try {
                val response = cardManagementService.getRegistrationCards()
                if (response.isSuccessful) {
                    val cardList = response.body()?.items ?: emptyList()
                    _registrationCardUiState.value = RegistrationCardUiState.Success(cardList)
                    Log.d(TAG, "카드 목록 받아오기 성공: ${cardList.size} 개의 카드")
                } else {
                    _registrationCardUiState.value =
                        RegistrationCardUiState.Error("Failed to fetch cards: ${response.code()}")
                }
            } catch (e: Exception) {
                _registrationCardUiState.value =
                    RegistrationCardUiState.Error("Failed to fetch cards: ${e.message}")
            }
        }
    }

    fun openBottomSheet() {
        _showBottomSheet.value = true
    }

    fun closeBottomSheet() {
        _showBottomSheet.value = false
    }
}

sealed class PhotoUiState {
    object Loading : PhotoUiState()
    data class Success(val photos: List<Photos>) : PhotoUiState()
    data class Error(val message: String) : PhotoUiState()
}

sealed class OwnedCardUiState {
    object Loading : OwnedCardUiState()
    data class Success(val cards: List<OwnedCard>) : OwnedCardUiState()
    data class Error(val message: String) : OwnedCardUiState()
}

sealed class RegistrationCardUiState {
    object Loading : RegistrationCardUiState()
    data class Success(val cards: List<RegistrationCard>) : RegistrationCardUiState()
    data class Error(val message: String) : RegistrationCardUiState()
}