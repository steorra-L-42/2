package com.example.merchant.domain.payment.service;

import com.example.merchant.domain.payment.dto.PaymentRequest;
import com.example.merchant.domain.payment.dto.PaymentResponse;
import com.example.merchant.util.credential.CredentialUtil;
import com.example.merchant.util.mobipay.MobiPay;
import com.example.merchant.util.mobipay.dto.MobiPaymentRequest;
import com.example.merchant.util.mobipay.dto.MobiPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final MobiPay mobiPay;
    private final CredentialUtil credentialUtil;

    public ResponseEntity<PaymentResponse> request(String merApiKey, PaymentRequest request) {

        credentialUtil.validatePosMerApiKey(merApiKey);

        // send a request to the mobipay server
        Long merchantId = credentialUtil.getMerchantIdByType(request.getType());
        MobiPaymentRequest mobiPaymentRequest = MobiPaymentRequest.of(request, merchantId);
        ResponseEntity<MobiPaymentResponse> mobiPaymentResponse =
                mobiPay.request(mobiPaymentRequest, MobiPaymentResponse.class);

        return ResponseEntity.status(mobiPaymentResponse.getStatusCode())
                .body(PaymentResponse.from(mobiPaymentResponse.getBody()));
    }

    public void result() {

        // send a result to client
        // close the socket connection

        return;
    }
}
