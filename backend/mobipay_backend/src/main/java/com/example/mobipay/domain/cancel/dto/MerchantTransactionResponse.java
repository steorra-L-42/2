package com.example.mobipay.domain.cancel.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class MerchantTransactionResponse {

    private List<MerchantTransactionItem> items;

    public static MerchantTransactionResponse of(List<MerchantTransactionItem> items) {
        return new MerchantTransactionResponse(items);
    }

}