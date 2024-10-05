package com.example.mobipay.domain.registeredcard.dto;

import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisteredCardListResponse {
    private final List<RegisteredCardListResponse> items;
    private Long mobiUserId;
    private Long ownedCardId;
    private Integer oneDayLimit;
    private Integer oneTimeLimit;
    private String cardNo;
    private String cardExpriyDate;
    private String cardName;
    private Boolean autoPayStatus;

    public static RegisteredCardListResponse of(RegisteredCard registeredCard) {
        return RegisteredCardListResponse.builder()
                .mobiUserId(registeredCard.getMobiUserId())
                .ownedCardId(registeredCard.getOwnedCardId())
                .oneDayLimit(registeredCard.getOneDayLimit())
                .oneTimeLimit(registeredCard.getOneTimeLimit())
                .cardNo(registeredCard.getOwnedCard().getCardNo())
                .cardExpriyDate(registeredCard.getOwnedCard().getCardExpiryDate())
                .cardName(registeredCard.getOwnedCard().getCardProduct().getCardName())
                .autoPayStatus(registeredCard.getAutoPayStatus())
                .build();
    }

    public static RegisteredCardListResponse from(List<RegisteredCard> registeredCards) {
        List<RegisteredCardListResponse> items = registeredCards.stream()
                .map(RegisteredCardListResponse::of)
                .toList();

        return RegisteredCardListResponse.builder()
                .items(items)
                .build();
    }

}
