package com.example.mobipay.domain.ownedcard.dto;

import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OwnedCardDetailResponse {
    private Long id;
    private String cardNo;
    private String cvc;
    private String withdrawalDate;
    private String cardExpiryDate;
    private LocalDateTime created;
    private Long mobiUserId;
    private Long accountId;
    private String cardUniqueNo;

    public static OwnedCardDetailResponse from(OwnedCard ownedCard) {
        return OwnedCardDetailResponse.builder()
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

    public static OwnedCardDetailResponse fromDetailInfo(OwnedCard ownedCard) {
        return OwnedCardDetailResponse.builder()
                .cardNo(ownedCard.getCardNo())
                .cvc(ownedCard.getCvc())
                .cardExpiryDate(ownedCard.getCardExpiryDate())
                .build();
    }
}
