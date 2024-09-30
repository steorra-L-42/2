package com.kimnlee.cardmanagement.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.cardmanagement.data.model.Card
import com.kimnlee.cardmanagement.data.repository.CardManagementRepository
import com.kimnlee.common.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CardManagementViewModel(private val authManager: AuthManager) : ViewModel() {
    private val repository = CardManagementRepository(authManager)
    private val _cardUiState = MutableStateFlow<CardUiState>(CardUiState.Loading)
    val cardUiState: StateFlow<CardUiState> = _cardUiState

    init {
        fetchPhotos()
        Log.d("_dddddddd",_cardUiState.value.toString())
        Log.d("dddddddd",cardUiState.value.toString())
    }
    fun fetchPhotos() {
        viewModelScope.launch {
            _cardUiState.value = CardUiState.Loading

            try {
                val cards = repository.getCards()
                Log.d("photos",cards.toString())
                _cardUiState.value = CardUiState.Success(cards)
            } catch (e: Exception) {
                _cardUiState.value = CardUiState.Error("Failed to fetch users: ${e.message}")
            }
        }
    }
}

sealed class CardUiState {
    object Loading : CardUiState()
    data class Success(val cards: List<Card>) : CardUiState()
    data class Error(val message: String) : CardUiState()
}
