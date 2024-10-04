package com.example.mobipay.domain.postpayments.dto;

import com.example.mobipay.domain.approvalwaiting.entity.ApprovalWaiting;
import com.example.mobipay.domain.merchant.entity.Merchant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApprovalWaitingResponse {
    private Merchant merchant;
    private ApprovalWaiting approvalWaiting;

    public static ApprovalWaitingResponse of(Merchant merchant, ApprovalWaiting approvalWaiting) {
        return ApprovalWaitingResponse.builder()
                .merchant(merchant)
                .approvalWaiting(approvalWaiting)
                .build();
    }
}
