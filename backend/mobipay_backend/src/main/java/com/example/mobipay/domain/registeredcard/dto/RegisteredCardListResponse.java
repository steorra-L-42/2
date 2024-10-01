package com.example.mobipay.domain.registeredcard.dto;

import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisteredCardListResponse {
    private Long mobiUserId;
    private Long ownedCardId;
    private Integer oneDayLimit;
    private Integer oneTimeLimit;
    private String cardName;
    private Boolean autoPayStatus;

    private final List<RegisteredCardListResponse> items;

    public static RegisteredCardListResponse from(RegisteredCard registeredCard) {
        return RegisteredCardListResponse.builder()
                .mobiUserId(registeredCard.getMobiUserId())
                .ownedCardId(registeredCard.getOwnedCardId())
                .oneDayLimit(registeredCard.getOneDayLimit())
                .oneTimeLimit(registeredCard.getOneTimeLimit())
                .cardName(registeredCard.getOwnedCard().getCardProduct().getCareName())
                .autoPayStatus(registeredCard.getAutoPayStatus())
                .build();
    }
}
