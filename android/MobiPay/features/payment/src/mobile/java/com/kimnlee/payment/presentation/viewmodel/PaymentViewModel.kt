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
            try {
                val response = paymentService.getPaymentHistory()
                when (response.code()) {
                    200 -> {
                        val paymentHistoryResponse = response.body()
                        if (paymentHistoryResponse != null) {
                            _paymentHistory.value = PaymentHistoryState.Success(paymentHistoryResponse)
                        }
                    }
                    204 -> {
                        _paymentHistory.value = PaymentHistoryState.NoContent
                    }
                    else -> {
                        Log.d(TAG, "결제 내역 조회 실패: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "네트워크 오류로 결제 내역 조회 실패: $e")
            }
        }
    }

    // 전자 영수증 출력
    fun loadElectronicReceipt(transactionUniqueNo: Int) {
        viewModelScope.launch {
            try {
                val response = paymentService.printReceipt(transactionUniqueNo)
                if (response.isSuccessful) {
                    val receiptResponse = response.body()
                    if (receiptResponse != null) {
                        _electronicReceipt.value = ElectronicReceiptState.Success(receiptResponse)
                    }
                } else {
                    Log.d(TAG, "전자 영수증 출력 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "네트워크 문제로 전자 영수증 출력 실패: $e")
            }
        }
    }

    fun clearElectronicReceipt() {
        _electronicReceipt.value = ElectronicReceiptState.Initial
    }
}

sealed class PaymentHistoryState {
    object Initial : PaymentHistoryState()
    data class Success(val data: PaymentHistoryResponse) : PaymentHistoryState()
    object NoContent : PaymentHistoryState()
}

sealed class ElectronicReceiptState {
    object Initial : ElectronicReceiptState()
    data class Success(val data: ReceiptResponse) : ElectronicReceiptState()
    data class Error(val message: String) : ElectronicReceiptState()
}