package com.example.mobipay.domain.registeredcard.service;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.ownedcard.error.OwnedCardNotFoundException;
import com.example.mobipay.domain.ownedcard.repository.OwnedCardRepository;
import com.example.mobipay.domain.registeredcard.dto.RegisteredCardAutoPayRequest;
import com.example.mobipay.domain.registeredcard.dto.RegisteredCardAutoPayResponse;
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
                request.getPassword(), false);

        registeredCard.addRelations(mobiUser, ownedCard);

        registeredCardRepository.save(registeredCard);

        return RegisteredCardResponse.of(registeredCard);

    }

//    public RegisteredCardListResponse registeredCardList(CustomOAuth2User oauth2User) {
//
//        // 사용자 정보 조회
//        MobiUser mobiUser = findMobiUser(oauth2User.getMobiUserId());
//
//        Car car =
//
////        carid 안에 존재하는 멤버들의 등록한 카드를 노출
//    }

    @Transactional
    public RegisteredCardAutoPayResponse registeredCardAutoPay(RegisteredCardAutoPayRequest request,
                                                               CustomOAuth2User oauth2User) {

        MobiUser mobiUser = findMobiUser(oauth2User.getMobiUserId());

        // 요청된 카드 조회
        RegisteredCard registeredCard = registeredCardRepository.findByOwnedCardIdAndMobiUserId(
                        request.getOwnedCardId(), mobiUser.getId())
                .orElseThrow(OwnedCardNotFoundException::new);

        Boolean newAutoPayStatus = request.getAutoPayStatus();

        // 1. autoPayStatus가 false일 경우 요청된 카드를 false로 저장
        if (!newAutoPayStatus) {
            // 요청된 카드의 상태를 false로 변경
            registeredCard.setAutoPayStatus(false);

            registeredCardRepository.save(registeredCard);

            return RegisteredCardAutoPayResponse.of(registeredCard);
        }
        // 2. autoPayStatus가 true일 경우 처리

        // 기존에 autoPayStatus가 true인 카드가 있는지 확인
        RegisteredCard existingAutoPayCard = registeredCardRepository
                .findByMobiUserIdAndAutoPayStatus(mobiUser.getId(), true)
                .orElse(null);

        // 기존 autoPayStatus가 true인카드가 있으면 autoPayStatus를 false로 변경
        if (existingAutoPayCard != null) {

            existingAutoPayCard.setAutoPayStatus(false);

            registeredCardRepository.save(existingAutoPayCard);
        }

        // 요청된 카드의 autoPayStatus를 true로 변경
        registeredCard.setAutoPayStatus(true);

        // 변경 사항 저장
        registeredCardRepository.save(registeredCard);

        return RegisteredCardAutoPayResponse.of(registeredCard);
    }

    private MobiUser findMobiUser(Long mobiUserId) {

        return mobiUserRepository.findById(mobiUserId)
                .orElseThrow(MobiUserNotFoundException::new);
    }
}
