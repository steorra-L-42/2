package com.example.mobipay.domain.registeredcard.service;

import com.example.mobipay.domain.registeredcard.repository.RegisteredCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RegisteredCardService {

    private final RegisteredCardRepository registeredCardRepository;

    //@Transactional
    // 카드 조회 로직
//    public void CheckOwnedCardList()

    // 카드 등록 로직
//    public void registerCard(Long mobiUserId, Long ownedCardId, MobiCardRegisterRequest request) {
//
//    }

    // 자동결제카드 등록 로직
}
