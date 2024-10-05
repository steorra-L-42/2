package com.example.mobipay.domain.ownedcard.dto;

import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OwnedCardResponse {
    private Long id;
    private String cardNo;
    private String cvc;
    private String withdrawalDate;
    private String cardExpiryDate;
    private LocalDateTime created;
    private Long mobiUserId;
    private Long accountId;
    private String cardUniqueNo;

    public static OwnedCardResponse from(OwnedCard ownedCard) {
        return OwnedCardResponse.builder()
                .id(ownedCard.getId())
                .cardNo(ownedCard.getCardNo())
                .cvc(ownedCard.getCvc())
                .withdrawalDate(ownedCard.getWithdrawalDate())
                .cardExpiryDate(ownedCard.getCardExpiryDate())
                .created(ownedCard.getCreated())
                .mobiUserId(ownedCard.getMobiUser().getId())
                .accountId(ownedCard.getAccount().getId())
                .cardUniqueNo(ownedCard.getCardProduct().getCardUniqueNo())
                .build();
    }
}
