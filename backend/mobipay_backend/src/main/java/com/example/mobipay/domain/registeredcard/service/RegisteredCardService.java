package com.example.mobipay.domain.registeredcard.service;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.ownedcard.error.OwnedCardNotFoundException;
import com.example.mobipay.domain.ownedcard.repository.OwnedCardRepository;
import com.example.mobipay.domain.registeredcard.dto.RegisteredCardAutoPayRequest;
import com.example.mobipay.domain.registeredcard.dto.RegisteredCardAutoPayResponse;
import com.example.mobipay.domain.registeredcard.dto.RegisteredCardListResponse;
import com.example.mobipay.domain.registeredcard.dto.RegisteredCardRequest;
import com.example.mobipay.domain.registeredcard.dto.RegisteredCardResponse;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import com.example.mobipay.domain.registeredcard.error.AlreadyRegisteredCard;
import com.example.mobipay.domain.registeredcard.repository.RegisteredCardRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import java.util.List;
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
    public RegisteredCardResponse registerCard(RegisteredCardRequest request, CustomOAuth2User oauth2User) {

        // 사용자 정보 조회
        MobiUser mobiUser = findMobiUser(oauth2User.getMobiUserId());

        OwnedCard ownedCard = ownedCardRepository.findOwnedCardById(request.getOwnedCardId())
                .orElseThrow(OwnedCardNotFoundException::new);

        registeredCardRepository.findByOwnedCardId(ownedCard.getId())
                .ifPresent(card -> {
                    throw new AlreadyRegisteredCard();
                });

        RegisteredCard registeredCard = RegisteredCard.of(
                request.getOneDayLimit(),
                request.getOneTimeLimit(),
                false);

        registeredCard.addRelations(mobiUser, ownedCard);

        registeredCardRepository.save(registeredCard);

        return RegisteredCardResponse.of(registeredCard);

    }

    public RegisteredCardListResponse registerCardList(CustomOAuth2User oauth2User) {

        // 사용자 정보 조회
        MobiUser mobiUser = findMobiUser(oauth2User.getMobiUserId());

        List<RegisteredCard> registeredCards = registeredCardRepository.findByMobiUserId(mobiUser.getId());

        return RegisteredCardListResponse.from(registeredCards);

    }

    @Transactional
    public RegisteredCardAutoPayResponse registerCardAutoPay(RegisteredCardAutoPayRequest request,
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

            return RegisteredCardAutoPayResponse.from(registeredCard);
        }
        // 2. autoPayStatus가 true일 경우 처리

        // 기존에 autoPayStatus가 true인 카드가 있는지 확인
        List<RegisteredCard> existingAutoPayCard = registeredCardRepository
                .findAllByMobiUserIdAndAutoPayStatus(mobiUser.getId(), true);

        // 기존 autoPayStatus가 true인카드가 있으면 autoPayStatus를 false로 변경
        if (!existingAutoPayCard.isEmpty()) {

            for (RegisteredCard card : existingAutoPayCard) {
                card.setAutoPayStatus(false);
            }

            registeredCardRepository.saveAll(existingAutoPayCard);
        }

        // 요청된 카드의 autoPayStatus를 true로 변경
        registeredCard.setAutoPayStatus(true);

        // 변경 사항 저장
        registeredCardRepository.save(registeredCard);

        return RegisteredCardAutoPayResponse.from(registeredCard);
    }

    private MobiUser findMobiUser(Long mobiUserId) {

        return mobiUserRepository.findById(mobiUserId)
                .orElseThrow(MobiUserNotFoundException::new);
    }
}
