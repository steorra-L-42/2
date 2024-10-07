package com.example.mobipay.domain.cancel.service;

import com.example.mobipay.domain.cancel.dto.CancelTransactionResponse;
import com.example.mobipay.domain.cancel.dto.MerchantTransactionItem;
import com.example.mobipay.domain.cancel.dto.MerchantTransactionResponse;
import com.example.mobipay.domain.merchant.entity.Merchant;
import com.example.mobipay.domain.merchant.error.MerchantNotFoundException;
import com.example.mobipay.domain.merchant.repository.MerchantRepository;
import com.example.mobipay.domain.merchanttransaction.entity.MerchantTransaction;
import com.example.mobipay.domain.merchanttransaction.repository.MerchantTransactionRepository;
import com.example.mobipay.domain.postpayments.error.InvalidMobiApiKeyException;
import com.example.mobipay.util.RestClientUtil;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CancelService {

    private final MerchantTransactionRepository merchantTransactionRepository;
    private final MerchantRepository merchantRepository;
    private final RestClientUtil restClientUtil;

    public MerchantTransactionResponse getTransactions(String mobiApiKey, Long merchantId) {

        validateMobiApiKey(mobiApiKey, merchantId);

        List<MerchantTransactionItem> transactionItems = merchantTransactionRepository.findAllByMerchantId(merchantId).stream()
                .map(MerchantTransactionItem::from)
                .collect(Collectors.toList());

        return MerchantTransactionResponse.of(transactionItems);
    }


    public CancelTransactionResponse cancelTransaction(String mobiApiKey, Long merchantId, Long transactionUniqueNo) {

        validateMobiApiKey(mobiApiKey, merchantId);

        return null;
    }

    void validateMobiApiKey(String mobiApiKey, Long merchantId) {
        if(mobiApiKey == null || mobiApiKey.isEmpty()) {
            throw new InvalidMobiApiKeyException();
        }
        if(merchantId == null) {
            throw new MerchantNotFoundException();
        }

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(MerchantNotFoundException::new);

        boolean inValid = !mobiApiKey.equals(merchant.getApiKey());
        if(inValid) {
            throw new InvalidMobiApiKeyException();
        }
    }
}
