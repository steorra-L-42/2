package com.example.mobipay.domain.postpayments.dto;

import com.example.mobipay.domain.approvalwaiting.entity.ApprovalWaiting;
import com.example.mobipay.domain.merchant.entity.Merchant;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentContext {
    
    private MobiUser mobiUser;
    private OwnedCard ownedCard;
    private RegisteredCard registeredCard;
    private Merchant merchant;
    private ApprovalWaiting approvalWaiting;
    private ApprovalPaymentRequest request;
}
