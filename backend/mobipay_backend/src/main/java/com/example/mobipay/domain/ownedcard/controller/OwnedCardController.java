package com.example.mobipay.domain.ownedcard.controller;

import com.example.mobipay.domain.ownedcard.dto.OwnedCardListResponse;
import com.example.mobipay.domain.ownedcard.service.OwnedCardService;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cards")
public class OwnedCardController {

    private final OwnedCardService ownedCardService;

    @GetMapping("/owned")
    public ResponseEntity<OwnedCardListResponse> getOwnedCardsList(
            @AuthenticationPrincipal CustomOAuth2User oauth2User) {

        OwnedCardListResponse response = ownedCardService.getOwnedCardsList(oauth2User);

        return ResponseEntity.ok(response);
    }
}
