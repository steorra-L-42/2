package com.kimnlee.payment.presentation.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.network.ApiClient
import com.kimnlee.payment.data.api.PaymentApiService
import com.kimnlee.payment.data.model.PaymentHistoryResponse
import com.kimnlee.payment.data.model.ReceiptResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val authManager: AuthManager,
    private val apiClient: ApiClient) : ViewModel() {

    private val paymentService: PaymentApiService = apiClient.authenticatedApi.create(PaymentApiService::class.java)

    private val _paymentHistory = MutableStateFlow<PaymentHistoryState>(PaymentHistoryState.Initial)
    val paymentHistory: StateFlow<PaymentHistoryState> = _paymentHistory

    private val _electronicReceipt = MutableStateFlow<ElectronicReceiptState>(ElectronicReceiptState.Initial)
    val electronicReceipt: StateFlow<ElectronicReceiptState> = _electronicReceipt

    // 결제 내역 조회
    fun loadPaymentHistory() {
        viewModelScope.launch {
            _paymentHistory.value = PaymentHistoryState.Loading
            try {
                val response = paymentService.getPaymentHistory()
                if (response.isSuccessful) {
                    val paymentHistoryResponse = response.body()
                    if (paymentHistoryResponse != null) {
                        _paymentHistory.value = PaymentHistoryState.Success(paymentHistoryResponse)
                    } else {
                        _paymentHistory.value = PaymentHistoryState.Error("Response body is null")
                    }
                } else {
                    _paymentHistory.value = PaymentHistoryState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _paymentHistory.value = PaymentHistoryState.Error("Network error: ${e.message}")
            }
        }
    }

    // 전자 영수증 출력
    fun loadElectronicReceipt(transactionUniqueNo: Int) {
        Log.d(TAG, "전자 영수증 출력 메서드 호출: $transactionUniqueNo")
        viewModelScope.launch {
            _electronicReceipt.value = ElectronicReceiptState.Loading
            try {
                val response = paymentService.printReceipt(transactionUniqueNo)
                Log.d(TAG, "전자영수증 출력 결과: $response")
                if (response.isSuccessful) {
                    val receiptResponse = response.body()
                    if (receiptResponse != null) {
                        _electronicReceipt.value = ElectronicReceiptState.Success(receiptResponse)
                    } else {
                        _electronicReceipt.value = ElectronicReceiptState.Error("Response body is null")
                    }
                } else {
                    Log.d(TAG, "전자영수증 출력 실패: $response")
                    _electronicReceipt.value = ElectronicReceiptState.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "전자영수증 출력 실패: $e")
                _electronicReceipt.value = ElectronicReceiptState.Error("Network error: ${e.message}")
            }
        }
    }
}

sealed class PaymentHistoryState {
    object Initial : PaymentHistoryState()
    object Loading : PaymentHistoryState()
    data class Success(val data: PaymentHistoryResponse) : PaymentHistoryState()
    data class Error(val message: String) : PaymentHistoryState()
}

sealed class ElectronicReceiptState {
    object Initial : ElectronicReceiptState()
    object Loading : ElectronicReceiptState()
    data class Success(val data: ReceiptResponse) : ElectronicReceiptState()
    data class Error(val message: String) : ElectronicReceiptState()
}