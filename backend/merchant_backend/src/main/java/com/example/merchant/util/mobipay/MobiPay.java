package com.example.merchant.util.mobipay;

import com.example.merchant.util.mobipay.dto.CancelTransactionRequest;
import com.example.merchant.util.mobipay.dto.CancelTransactionResponse;
import com.example.merchant.util.mobipay.dto.MerchantTransactionRequest;
import com.example.merchant.util.mobipay.dto.MerchantTransactionResponse;
import com.example.merchant.util.mobipay.dto.PaymentRequest;
import com.example.merchant.util.mobipay.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;

public interface MobiPay {

    // 1. 결제 요청 보내기
    // POST /api/v1/postpayments/request
    // PaymentRequest, PaymentResponse
    public ResponseEntity<PaymentResponse> request(PaymentRequest request, Class<PaymentResponse> responseClass);

    // 2. 결제 내역 조회
    // GET /api/v1/merchants/{merchant_id}/transactions
    // MerchantTransactionRequest, MerchantTransactionResponse
    public ResponseEntity<MerchantTransactionResponse> getTransactionList(MerchantTransactionRequest request, Class<MerchantTransactionResponse> responseClass);

    // 3. 결제 취소 요청
    // PATCH /api/v1/merchants/{merchant_id}/cancelled-transactions/{transactionUniqueNo}
    // CancelTransactionRequest, CancelTransactionResponse
    public ResponseEntity<CancelTransactionResponse> cancelTransaction(CancelTransactionRequest request, Class<CancelTransactionResponse> responseClass);

}
