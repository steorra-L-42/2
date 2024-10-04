package com.example.mobipay.domain.registeredcard.dto;

import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisteredCardResponse {
    private Long mobiUserId;
    private Long ownedCardId;
    private String cardNo;
    private Integer oneDayLimit;
    private Integer oneTimeLimit;

    public static RegisteredCardResponse of(RegisteredCard registeredCard) {
        return RegisteredCardResponse.builder()
                .mobiUserId(registeredCard.getMobiUserId())
                .ownedCardId(registeredCard.getOwnedCardId())
                .cardNo(registeredCard.getOwnedCard().getCardNo())
                .oneDayLimit(registeredCard.getOneDayLimit())
                .oneTimeLimit(registeredCard.getOneTimeLimit())
                .build();
    }
}
