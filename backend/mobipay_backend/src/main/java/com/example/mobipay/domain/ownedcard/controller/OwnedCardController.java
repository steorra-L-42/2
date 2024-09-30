package com.example.mobipay.domain.ownedcard.controller;

import com.example.mobipay.domain.ownedcard.dto.OwnedCardDto;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.ownedcard.service.OwnedCardService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cards")
public class OwnedCardController {

    private final OwnedCardService ownedCardService;

    @GetMapping("/owned/{mobiUserId}")
    public ResponseEntity<List<OwnedCardDto>> getOwnedCardsList(@PathVariable("mobiUserId") Long mobiUserId) {
        List<OwnedCard> ownedCard = ownedCardService.getOwnedCardsList(mobiUserId);
        List<OwnedCardDto> ownedCardDto = ownedCard.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ownedCardDto);
    }

    private OwnedCardDto convertToDto(OwnedCard ownedCard) {
        return OwnedCardDto.builder()
                .id(ownedCard.getId())
                .cardNo(ownedCard.getCardNo())
                .cvc(ownedCard.getCvc())
                .withdrawalDate(ownedCard.getWithdrawalDate())
                .cardExpiryDate(ownedCard.getCardExpiryDate())
                .created(ownedCard.getCreated())
                .build();
    }
}
