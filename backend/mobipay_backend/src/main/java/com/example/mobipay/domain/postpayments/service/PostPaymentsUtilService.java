package com.example.mobipay.domain.postpayments.service;

import com.example.mobipay.domain.merchanttransaction.entity.MerchantTransaction;
import com.example.mobipay.domain.merchanttransaction.error.MerchantTransactionNotFoundException;
import com.example.mobipay.domain.merchanttransaction.repository.MerchantTransactionRepository;
import com.example.mobipay.domain.postpayments.dto.ReceiptResponse;
import com.example.mobipay.domain.postpayments.error.ReceiptUserMismatchException;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostPaymentsUtilService {

    private final MerchantTransactionRepository merchantTransactionRepository;

    public ReceiptResponse getReceipt(Long transactionUniqueNo, CustomOAuth2User oAuth2User) {

        MerchantTransaction merchantTransaction = getMerchantTransaction(transactionUniqueNo);
        validateUser(oAuth2User, merchantTransaction);

        return ReceiptResponse.from(merchantTransaction);
    }

    // MerchantTransaction 얻기
    private MerchantTransaction getMerchantTransaction(Long transactionUniqueNo) {
        return merchantTransactionRepository.findByTransactionUniqueNo(
                        transactionUniqueNo)
                .orElseThrow(MerchantTransactionNotFoundException::new);
    }

    // MerchantTransaction의 유저와, JWT 유저가 같은지 검증
    private void validateUser(CustomOAuth2User oAuth2User, MerchantTransaction merchantTransaction) {
        Long mobiUserId = merchantTransaction.getRegisteredCard().getMobiUserId();
        if (!mobiUserId.equals(oAuth2User.getMobiUserId())) {
            throw new ReceiptUserMismatchException();
        }
    }
}
