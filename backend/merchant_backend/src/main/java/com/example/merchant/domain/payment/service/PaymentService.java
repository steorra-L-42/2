package com.example.merchant.domain.payment.service;

import com.example.merchant.domain.payment.dto.PaymentRequest;
import com.example.merchant.domain.payment.dto.PaymentResponse;
import com.example.merchant.domain.payment.dto.PaymentResult;
import com.example.merchant.domain.payment.dto.PaymentResultRequest;
import com.example.merchant.global.enums.MerchantType;
import com.example.merchant.util.credential.CredentialUtil;
import com.example.merchant.util.mobipay.MobiPay;
import com.example.merchant.util.mobipay.dto.MobiPaymentRequest;
import com.example.merchant.util.mobipay.dto.MobiPaymentResponse;
import com.example.merchant.util.pos.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final MobiPay mobiPay;
    private final CredentialUtil credentialUtil;
    private final WebSocketHandler webSocketHandler;

    public ResponseEntity<PaymentResponse> request(String posMerApiKey, PaymentRequest request) {

        credentialUtil.validatePosMerApiKey(posMerApiKey);

        // send a request to the mobipay server
        Long merchantId = credentialUtil.getMerchantIdByType(request.getType());
        MobiPaymentRequest mobiPaymentRequest = MobiPaymentRequest.of(request, merchantId);
        ResponseEntity<MobiPaymentResponse> mobiPaymentResponse =
                mobiPay.request(mobiPaymentRequest, MobiPaymentResponse.class);

        return ResponseEntity.status(mobiPaymentResponse.getStatusCode())
                .body(PaymentResponse.from(mobiPaymentResponse.getBody()));
    }

    public void result(String mobiMerApiKey, PaymentResultRequest result) {

        credentialUtil.validateMobiMerApiKey(mobiMerApiKey);

        // send a result to POS
        MerchantType type = credentialUtil.getMerchantTypeById(result.getMerchantId());
        PaymentResult paymentResult = PaymentResult.of(result, type);
        webSocketHandler.sendResult(type, paymentResult);
    }
}
