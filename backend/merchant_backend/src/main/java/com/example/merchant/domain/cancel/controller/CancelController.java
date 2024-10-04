package com.example.merchant.domain.cancel.controller;

import com.example.merchant.domain.cancel.dto.CancelTransactionResponse;
import com.example.merchant.domain.cancel.dto.MerchantTranscactionResponse;
import com.example.merchant.domain.cancel.service.CancelSerivce;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/merchants")
@RestController
public class CancelController {

    private final CancelSerivce cancelService;

    @GetMapping("/{merchantType}/transactions")
    public ResponseEntity<MerchantTranscactionResponse> getTransactions(@RequestHeader("merApiKey") String merApiKey,
                                                                        @PathVariable("merchantType") String merchantType) {

        return cancelService.getTransactions(merApiKey, merchantType);
    }

    @PatchMapping("/{merchantType}/cancelled-transactions/{transactionUniqueNo}")
    public ResponseEntity<CancelTransactionResponse> cancelTransaction(@RequestHeader("merApiKey") String merApiKey,
                                                                      @PathVariable("merchantType") String merchantType,
                                                                      @PathVariable("transactionUniqueNo") String transactionUniqueNo) {

       CancelTransactionResponse response = cancelService.cancelTransaction(merApiKey, merchantType, transactionUniqueNo);

        return null;
    }
}
