package com.example.mobipay.domain.ownedcard.dto;

import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OwnedCardListResponse {

    private final List<OwnedCardResponse> items;

    public static OwnedCardListResponse from(List<OwnedCard> ownedCards) {
        List<OwnedCardResponse> items = ownedCards.stream()
                .map(OwnedCardResponse::from)
                .toList();

        return new OwnedCardListResponse(items);
    }
}
