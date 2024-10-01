package com.example.mobipay.domain.ownedcard.dto;

import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OwnedCardListResponse {

    private final List<OwnedCardDetailResponse> items;

    public static OwnedCardListResponse from(List<OwnedCard> ownedCards) {
        List<OwnedCardDetailResponse> items = ownedCards.stream()
                .map(OwnedCardDetailResponse::from)
                .toList();

        return new OwnedCardListResponse(items);
    }

}
