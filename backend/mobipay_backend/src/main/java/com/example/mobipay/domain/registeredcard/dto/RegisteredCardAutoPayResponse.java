package com.example.mobipay.domain.registeredcard.dto;

import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisteredCardAutoPayResponse {
    private Long mobiUserId;
    private Long ownedCardId;
    private Boolean autoPayStatus;

    public static RegisteredCardAutoPayResponse from(RegisteredCard registeredCard) {
        return RegisteredCardAutoPayResponse.builder()
                .mobiUserId(registeredCard.getMobiUserId())
                .ownedCardId(registeredCard.getOwnedCardId())
                .autoPayStatus(registeredCard.getAutoPayStatus())
                .build();
    }
}