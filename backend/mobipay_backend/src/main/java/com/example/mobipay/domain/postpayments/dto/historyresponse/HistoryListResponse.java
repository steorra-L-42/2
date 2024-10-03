package com.example.mobipay.domain.postpayments.dto.historyresponse;

import com.example.mobipay.domain.merchanttransaction.entity.MerchantTransaction;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HistoryListResponse {
    private final List<HistoryDetailResponse> items;

    public static HistoryListResponse from(List<MerchantTransaction> mobiUserTransactions) {
        List<HistoryDetailResponse> items = mobiUserTransactions.stream()
                .map(HistoryDetailResponse::from)
                .toList();

        return new HistoryListResponse(items);
    }
}
