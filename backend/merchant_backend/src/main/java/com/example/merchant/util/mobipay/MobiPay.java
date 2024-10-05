package com.example.merchant.util.mobipay;

import com.example.merchant.domain.cancel.dto.CancelTransactionResponse;
import com.example.merchant.domain.cancel.dto.MerchantTranscactionResponse;
import com.example.merchant.global.enums.MerchantType;
import com.example.merchant.util.mobipay.dto.MobiPaymentRequest;
import com.example.merchant.util.mobipay.dto.MobiPaymentResponse;
import org.springframework.http.ResponseEntity;

public interface MobiPay {

    // 1. 결제 요청 보내기
    // POST /api/v1/postpayments/request
    // PaymentRequest, PaymentResponse
    public ResponseEntity<MobiPaymentResponse> request(MobiPaymentRequest request, Class<MobiPaymentResponse> responseClass);

    // 2. 결제 내역 조회
    // GET /api/v1/merchants/{merchant_id}/transactions
    // MerchantTransactionResponse
    public ResponseEntity<MerchantTranscactionResponse> getTransactionList(MerchantType type, Class<MerchantTranscactionResponse> responseClass);

    // 3. 결제 취소 요청
    // PATCH /api/v1/merchants/{merchant_id}/cancelled-transactions/{transactionUniqueNo}
    // CancelTransactionResponse
    public ResponseEntity<CancelTransactionResponse> cancelTransaction(MerchantType type, Long transactionUniqueNo, Class<CancelTransactionResponse> responseClass);

}
