package com.example.mobipay.domain.cancel.controller;

import com.example.mobipay.domain.cancel.dto.MerchantTransactionResponse;
import com.example.mobipay.domain.cancel.service.CancelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/merchants")
public class CancelController {

    private final CancelService cancelService;

    @RequestMapping("/{merchantId}/transactions")
    public ResponseEntity<MerchantTransactionResponse> getTransactions(@RequestHeader("mobiApiKey") String mobiApiKey,
                                                                       @PathVariable("merchantId") Long merchantId) {

        MerchantTransactionResponse response = cancelService.getTransactions(mobiApiKey, merchantId);

        if(response.getItems().isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    @RequestMapping("/{merchantId}/cancelled-transactions/{transactionUniqueNo}")
    public ResponseEntity<?> cancelTransaction(@RequestHeader("mobiApiKey") String mobiApiKey,
                                                                       @PathVariable("merchantId") Long merchantId,
                                                                       @PathVariable("transactionUniqueNo") Long transactionUniqueNo) {

        cancelService.cancelTransaction(mobiApiKey, merchantId, transactionUniqueNo);

        return ResponseEntity.ok().build();
    }
}
