package com.example.merchant.domain.cancel.service;

import com.example.merchant.domain.cancel.dto.CancelTransactionResponse;
import com.example.merchant.domain.cancel.dto.MerchantTranscactionResponse;
import com.example.merchant.domain.cancel.error.InvalidTransactionUniqueNoException;
import com.example.merchant.domain.payment.dto.PaymentResponse;
import com.example.merchant.domain.payment.error.InvalidMerchantTypeException;
import com.example.merchant.util.credential.CredentialUtil;
import com.example.merchant.util.mobipay.MobiPay;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CancelSerivce {

    private final CredentialUtil credentialUtil;
    private final MobiPay mobiPay;

    public ResponseEntity<MerchantTranscactionResponse> getTransactions(String posMerApiKey, String merchantType) {

        credentialUtil.validatePosMerApiKey(posMerApiKey);
        validateMerchantType(merchantType);

        return mobiPay.getTransactionList(credentialUtil.getMerchantTypeLowerCaseString(merchantType),
                MerchantTranscactionResponse.class);
    }


    public CancelTransactionResponse cancelTransaction(String posMerApiKey, String merchantType, String transactionUniqueNo) {

        credentialUtil.validatePosMerApiKey(posMerApiKey);
        validateMerchantType(merchantType);
        validateTransactionUniqueNo(transactionUniqueNo);

        return null;
//        return ResponseEntity.status(mobiPaymentResponse.getStatusCode())
//                .body(PaymentResponse.from(mobiPaymentResponse.getBody()));
    }


    private void validateMerchantType(String merchantType) {
        if (merchantType == null || merchantType.isEmpty()) {
            throw new InvalidMerchantTypeException();
        }

        List<String> merchantTypes = List.of("parking", "oil", "food", "washing", "motel", "street");
        if(!merchantTypes.contains(merchantType)) {
            throw new InvalidMerchantTypeException();
        }
    }

    private void validateTransactionUniqueNo(String transactionUniqueNo) {
        if(transactionUniqueNo == null || transactionUniqueNo.isEmpty()) {
            throw new InvalidTransactionUniqueNoException();
        }
    }
}
