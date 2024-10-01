package com.example.mobipay.domain.registeredcard.dto;

import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisteredCardResponse {
    private Long mobiUserId;
    private Long ownedCardId;
    private Integer oneDayLimit;
    private Integer oneTimeLimit;
    private String password;

    public static RegisteredCardResponse of(RegisteredCard registeredCard) {
        return RegisteredCardResponse.builder()
                .mobiUserId(registeredCard.getMobiUserId())
                .ownedCardId(registeredCard.getOwnedCardId())
                .oneDayLimit(registeredCard.getOneDayLimit())
                .oneTimeLimit(registeredCard.getOneTimeLimit())
                .password(registeredCard.getPassword())
                .build();
    }
}
