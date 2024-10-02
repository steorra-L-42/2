package com.kimnlee.cardmanagement.presentation.viewmodel

import OwnedCard
import Photos
import RegisterCardRequest
import RegisteredCard
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.material3.Card
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
    private val apiClient: ApiClient,
) : ViewModel() {

    private val cardManagementService: CardManagementApiService =
        apiClient.authenticatedApi.create(CardManagementApiService::class.java)

    private val _ownedCards = MutableStateFlow<List<OwnedCard>>(emptyList())
    val ownedCards: StateFlow<List<OwnedCard>> = _ownedCards


    private val _registrationStatus = MutableStateFlow<String?>(null)
    val registrationStatus: StateFlow<String?> = _registrationStatus

    // 소유한 카드 리스트 에서 카드 등록하기
    private val _registeredCards = MutableStateFlow<List<RegisteredCard>>(emptyList())
        val registeredCards : StateFlow<List<RegisteredCard>> = _registeredCards

    // 다이어로그 보이기
    private val _showDialog = MutableStateFlow<Boolean>(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    // 소유 카드 리스트 상태
    private val _ownedCardUiState = MutableStateFlow<OwnedCardUiState>(OwnedCardUiState.Loading)
    val ownedCardUiState: StateFlow<OwnedCardUiState> = _ownedCardUiState

    // 등록 카드 리스트 상태
    private val _registratedCardState =
        MutableStateFlow<RegistratedCardState>(RegistratedCardState.Loading)
    val registratedCardState: StateFlow<RegistratedCardState> = _registratedCardState

    // 바텀 시트
    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    init {
        requestOwnedCards()
        requestRegistrationCards()
    }

    fun requestOwnedCards() {
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
            _registratedCardState.value = RegistratedCardState.Loading
            try {
                val response = cardManagementService.getRegistrationCards()
                if (response.isSuccessful) {
                    val cardList = response.body()?.items ?: emptyList()
                    _registratedCardState.value = RegistratedCardState.Success(cardList)
                    Log.d(TAG, "카드 목록 받아오기 성공: ${cardList.size} 개의 카드")
                } else {
                    _registratedCardState.value =
                        RegistratedCardState.Error("Failed to fetch cards: ${response.code()}")
                }
            } catch (e: Exception) {
                _registratedCardState.value =
                    RegistratedCardState.Error("Failed to fetch cards: ${e.message}")
            }
        }
    }
    fun registerCard(ownedCardId: Long, oneDayLimit: Int, oneTimeLimit: Int, password: String, autoPayStatus : Boolean = false) {
        viewModelScope.launch {
            try {
                val request = RegisterCardRequest(ownedCardId, oneDayLimit, oneTimeLimit, password)
                val response = cardManagementService.registerCard(request)

                // 등록된 카드 목록에 새로운 카드 추가
                val newRegisteredCard = RegisteredCard(
                    mobiUserId = response.mobiUserId,
                    ownedCardId = response.ownedCardId,
                    oneDayLimit = response.oneDayLimit,
                    oneTimeLimit = response.oneTimeLimit,
                    autoPayStatus = autoPayStatus
                )
                _registeredCards.value = _registeredCards.value + newRegisteredCard
                _registrationStatus.value = "카드가 성공적으로 등록되었습니다."
            } catch (e: Exception) {
                _registrationStatus.value = "카드 등록 실패: ${e.message}"
            }
        }
    }
    fun openBottomSheet() {
        _showBottomSheet.value = true
    }

    fun closeBottomSheet() {
        _showBottomSheet.value = false
    }

    fun openDialog(cardNo: String) {
        _showDialog.value = true
    }

    fun closeDialog() {
        _showDialog.value = false
    }
}

sealed class OwnedCardUiState {
    object Loading : OwnedCardUiState()
    data class Success(val cards: List<OwnedCard>) : OwnedCardUiState()
    data class Error(val message: String) : OwnedCardUiState()
}

sealed class RegistratedCardState {
    object Loading : RegistratedCardState()
    data class Success(val cards: List<RegisteredCard>) : RegistratedCardState()
    data class Error(val message: String) : RegistratedCardState()
}
