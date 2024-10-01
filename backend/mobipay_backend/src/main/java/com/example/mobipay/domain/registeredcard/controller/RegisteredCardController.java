package com.example.mobipay.domain.registeredcard.controller;

import com.example.mobipay.domain.registeredcard.dto.RegisteredCardAutoPayRequest;
import com.example.mobipay.domain.registeredcard.dto.RegisteredCardAutoPayResponse;
import com.example.mobipay.domain.registeredcard.dto.RegisteredCardRequest;
import com.example.mobipay.domain.registeredcard.dto.RegisteredCardResponse;
import com.example.mobipay.domain.registeredcard.service.RegisteredCardService;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cards")
public class RegisteredCardController {

    private final RegisteredCardService registeredCardService;


    @PostMapping
    public ResponseEntity<RegisteredCardResponse> registerCard(@RequestBody @Valid RegisteredCardRequest request,
                                                               @AuthenticationPrincipal CustomOAuth2User oauth2User) {
        RegisteredCardResponse response = registeredCardService.registeredCard(request, oauth2User);
        return ResponseEntity.ok(response);
    }

//    @GetMapping
//    public ResponseEntity<RegisteredCardListResponse> registerCardList(
//            @AuthenticationPrincipal CustomOAuth2User oauth2User) {
//
//        RegisteredCardListResponse response = registeredCardService.registeredCardList(oauth2User);
//        return ResponseEntity.ok(response);
//
//    }

    @PatchMapping("/auto-pay")
    public ResponseEntity<RegisteredCardAutoPayResponse> autoPayCard(
            @RequestBody @Valid RegisteredCardAutoPayRequest request,
            @AuthenticationPrincipal CustomOAuth2User oauth2User) {
        RegisteredCardAutoPayResponse response = registeredCardService.registeredCardAutoPay(request, oauth2User);
        return ResponseEntity.ok(response);
    }


}
