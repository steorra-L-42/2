package com.kimnlee.cardmanagement.presentation.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.cardmanagement.data.api.CardManagementApiService
import com.kimnlee.cardmanagement.data.model.AutoPaymentCardRequest
import com.kimnlee.cardmanagement.data.model.CardDetailResponse
import com.kimnlee.cardmanagement.data.model.OwnedCard
import com.kimnlee.cardmanagement.data.model.RegisterCardRequest
import com.kimnlee.cardmanagement.data.model.RegisteredCard
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    // 다이어로그 보이기
    private val _showDialog = MutableStateFlow<Boolean>(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    // 소유 카드 리스트 상태
    private val _ownedCardUiState = MutableStateFlow<OwnedCardUiState>(OwnedCardUiState.Loading)
    val ownedCardUiState: StateFlow<OwnedCardUiState> = _ownedCardUiState

    // 등록 카드 리스트 상태
    private val _registeredCardState =
        MutableStateFlow<RegisteredCardState>(RegisteredCardState.Loading)
    val registratedCardState: StateFlow<RegisteredCardState> = _registeredCardState

    // 바텀 시트
    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    private val _cardDetail = MutableStateFlow<CardDetailResponse?>(null)
    val cardDetail: StateFlow<CardDetailResponse?> = _cardDetail

    private val _registeredCards = MutableStateFlow<List<RegisteredCard>>(emptyList())
    val registeredCards: StateFlow<List<RegisteredCard>> = _registeredCards.asStateFlow()

    init {
        getOwnedCards()
        getRegisteredCards()
    }

    // 내 소유의 카드 불러오기
    fun getOwnedCards() {
        viewModelScope.launch {
            _ownedCardUiState.value = OwnedCardUiState.Loading
            try {
                val response = cardManagementService.getOwnedCards()
                if (response.isSuccessful) {
                    val cardList = response.body()?.items ?: emptyList()
                    _ownedCardUiState.value = OwnedCardUiState.Success(cardList)
                    Log.d(TAG, "카드 목록 받아오기 성공: ${cardList.size} 개의 카드")
                    Log.d(TAG, "내 소유의 카드 목록 $cardList")
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

    // 등록된 카드 불러오기
    fun getRegisteredCards() {
        viewModelScope.launch {
            _registeredCardState.value = RegisteredCardState.Loading
            try {
                val response = cardManagementService.getRegistrationCards()
                if (response.isSuccessful) {
                    val cardList = response.body()?.items ?: emptyList()
                    _registeredCards.value = cardList
                    _registeredCardState.value = RegisteredCardState.Success(cardList)
                    Log.d(TAG, "등록된 카드 목록 받아오기 성공: ${cardList.size} 개의 카드")
                } else {
                    _registeredCardState.value =
                        RegisteredCardState.Error("Failed to fetch cards: ${response.code()}")
                }
            } catch (e: Exception) {
                _registeredCardState.value =
                    RegisteredCardState.Error("Failed to fetch cards: ${e.message}")
            }
        }
    }

    // 등록된 카드가 있는지 확인
    private fun shouldSetAutoPayment(): Boolean {
        return registeredCards.value.isEmpty()
    }

    // 내 소유의 카드 중에서 사용할 카드 등록
    fun registerCard(ownedCardId: Int, oneDayLimit: Int, oneTimeLimit: Int, password: String) {
        viewModelScope.launch {
            try {
                val request = RegisterCardRequest(ownedCardId, oneDayLimit, oneTimeLimit, password)
                val response = cardManagementService.registerCard(request)
                if (response.isSuccessful) {
                    val registeredCard = response.body()
                    if (registeredCard != null) {
                        _registrationStatus.value = "카드가 성공적으로 등록되었습니다."
                        // 등록된 카드가 없을 경우에만 자동 결제 설정
                        if (shouldSetAutoPayment()) {
                            setAutoPaymentCard(ownedCardId, true)
                        }
                        // 등록 성공 후 등록된 카드 목록을 갱신
                        getRegisteredCards()
                    } else {
                        _registrationStatus.value = "카드 등록 실패: 응답 데이터 없음"
                    }
                } else {
                    _registrationStatus.value = "카드 등록 실패: ${response.code()}"
                }
            } catch (e: Exception) {
                _registrationStatus.value = "카드 등록 실패: ${e.message}"
            }
        }
    }

    // 카드 정보 불러오기
    fun loadCardDetail(cardId: Int) {
        viewModelScope.launch {
            try {
                val response = cardManagementService.getCardDetail(cardId)
                if (response.isSuccessful) {
                    _cardDetail.value = response.body()
                } else {
                    Log.e(TAG, "Failed to load card detail: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while loading card detail: ${e.message}")
            }
        }
    }

    // 자동 결제 등록
    fun setAutoPaymentCard(ownedCardId: Int, autoPayStatus: Boolean) {
        viewModelScope.launch {
            try {
                val request = AutoPaymentCardRequest(ownedCardId, autoPayStatus)
                val response = cardManagementService.registerAutoPaymentCard(request)
                if (response.isSuccessful) {
                    // 로컬 상태 업데이트
                    _registeredCards.update { cards ->
                        cards.map { card ->
                            if (card.ownedCardId == ownedCardId) {
                                card.copy(autoPayStatus = autoPayStatus)
                            } else {
                                card
                            }
                        }
                    }
                    Log.d(TAG, "Auto payment card set successfully")
                } else {
                    Log.e(TAG, "Failed to set auto payment card: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while setting auto payment card: ${e.message}")
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

sealed class RegisteredCardState {
    object Loading : RegisteredCardState()
    data class Success(val cards: List<RegisteredCard>) : RegisteredCardState()
    data class Error(val message: String) : RegisteredCardState()
}
