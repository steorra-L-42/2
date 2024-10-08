package com.example.merchant.domain.cancel.service;

import com.example.merchant.domain.cancel.dto.CancelTransactionResponse;
import com.example.merchant.domain.cancel.dto.MerchantTransactionResponse;
import com.example.merchant.domain.cancel.error.InvalidTransactionUniqueNoException;
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

    public ResponseEntity<MerchantTransactionResponse> getTransactions(String posMerApiKey, String merchantType) {

        credentialUtil.validatePosMerApiKey(posMerApiKey);
        validateMerchantType(merchantType);

        // mobiPay server의 응답을 그대로 전달
        return mobiPay.getTransactionList(credentialUtil.getMerchantTypeLowerCaseString(merchantType),
                MerchantTransactionResponse.class);
    }


    public ResponseEntity<CancelTransactionResponse> cancelTransaction(String posMerApiKey, String merchantType, String transactionUniqueNo) {

        credentialUtil.validatePosMerApiKey(posMerApiKey);
        validateMerchantType(merchantType);
        Long valid_T_U_No = getValidTransactionUniqueNo(transactionUniqueNo);

        // mobiPay server의 응답을 그대로 전달
        return mobiPay.cancelTransaction(credentialUtil.getMerchantTypeLowerCaseString(merchantType),
                valid_T_U_No, CancelTransactionResponse.class);
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

    private Long getValidTransactionUniqueNo(String transactionUniqueNo) {
        if(transactionUniqueNo == null || transactionUniqueNo.isEmpty()) {
            throw new InvalidTransactionUniqueNoException();
        }
        try {
            return Long.parseLong(transactionUniqueNo);
        } catch (NumberFormatException e) {
            throw new InvalidTransactionUniqueNoException();
        }
    }
}
