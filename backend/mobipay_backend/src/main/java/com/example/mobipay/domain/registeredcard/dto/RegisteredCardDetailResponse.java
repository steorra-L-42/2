package com.example.mobipay.domain.registeredcard.dto;

import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisteredCardDetailResponse {
    private Long ownedCardId;
    private String cardNo;
    private String cvc;
    private String cardExpiryDate;
    private Integer oneDayLimit;
    private Integer oneTimeLimit;

    public static RegisteredCardDetailResponse from(RegisteredCard registeredCard) {
        return RegisteredCardDetailResponse.builder()
                .ownedCardId(registeredCard.getOwnedCardId())
                .cardNo(registeredCard.getOwnedCard().getCardNo())
                .cvc(registeredCard.getOwnedCard().getCvc())
                .cardExpiryDate(registeredCard.getOwnedCard().getCardExpiryDate())
                .oneDayLimit(registeredCard.getOneDayLimit())
                .oneTimeLimit(registeredCard.getOneTimeLimit())
                .build();
    }
}
