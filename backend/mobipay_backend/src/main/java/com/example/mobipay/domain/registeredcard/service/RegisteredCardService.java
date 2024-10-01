package com.example.mobipay.domain.registeredcard.service;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.ownedcard.error.OwnedCardNotFoundException;
import com.example.mobipay.domain.ownedcard.repository.OwnedCardRepository;
import com.example.mobipay.domain.registeredcard.dto.RegisteredCardRequest;
import com.example.mobipay.domain.registeredcard.dto.RegisteredCardResponse;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import com.example.mobipay.domain.registeredcard.repository.RegisteredCardRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class RegisteredCardService {

    private final RegisteredCardRepository registeredCardRepository;
    private final MobiUserRepository mobiUserRepository;
    private final OwnedCardRepository ownedCardRepository;

    @Transactional
    public RegisteredCardResponse registeredCard(RegisteredCardRequest request, CustomOAuth2User oauth2User) {

        // 사용자 정보 조회
        MobiUser mobiUser = findMobiUser(oauth2User.getMobiUserId());

        OwnedCard ownedCard = ownedCardRepository.findOwnedCardById(request.getOwnedCardId())
                .orElseThrow(OwnedCardNotFoundException::new);

        RegisteredCard registeredCard = RegisteredCard.of(request.getOneDayLimit(), request.getOneTimeLimit(),
                request.getPassword());

        registeredCard.addRelations(mobiUser, ownedCard);

        registeredCardRepository.save(registeredCard);

        return RegisteredCardResponse.of(registeredCard);

    }

//    public RegisteredCardListResponse registeredCardList(CustomOAuth2User oauth2User) {
//
//        // 사용자 정보 조회
//        MobiUser mobiUser = findMobiUser(oauth2User.getMobiUserId());
//
//
//    }

    private MobiUser findMobiUser(Long mobiUserId) {

        return mobiUserRepository.findById(mobiUserId)
                .orElseThrow(MobiUserNotFoundException::new);
    }
}
