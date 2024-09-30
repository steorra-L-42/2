package com.example.mobipay.domain.registeredcard.controller;

import com.example.mobipay.domain.registeredcard.service.RegisteredCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cards")
public class RegisteredCardController {

    private final RegisteredCardService registeredCardService;

//    @GetMapping("/owned")
//    public ResponseEntity<String> getOwnedCards() {
//
//    }

//    @PostMapping
//    public ResponseEntity<?> registerCard(@RequestBody MobiCardRegisterRequest mobiCardRegisterRequest) {
//        Long mobiUserId = mobiCardRegisterRequest.getMobiUserId();
//        Long ownedCardId = mobiCardRegisterRequest.getOwnedCardId();
//
//        registeredCardService.
//
//        return;
//
//    }
}
