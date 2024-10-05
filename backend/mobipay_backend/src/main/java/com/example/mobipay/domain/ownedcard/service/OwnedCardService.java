package com.example.mobipay.domain.ownedcard.service;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.ownedcard.dto.OwnedCardDetailResponse;
import com.example.mobipay.domain.ownedcard.dto.OwnedCardListResponse;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.ownedcard.error.OwnedCardNotFoundException;
import com.example.mobipay.domain.ownedcard.repository.OwnedCardRepository;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import com.example.mobipay.domain.registeredcard.repository.RegisteredCardRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnedCardService {

    private final OwnedCardRepository ownedCardRepository;
    private final MobiUserRepository mobiUserRepository;
    private final RegisteredCardRepository registeredCardRepository;

    public OwnedCardListResponse getOwnedCardsList(CustomOAuth2User oauth2User) {
        // 사용자 정보 찾기
        MobiUser mobiUser = findMobiUser(oauth2User.getMobiUserId());

        //사용자에 해당하는 카드정보 리스트로 가져오기
        List<OwnedCard> ownedCards = ownedCardRepository.findAllByMobiUser(mobiUser);

        return OwnedCardListResponse.from(ownedCards);
    }

    public OwnedCardDetailResponse getOwnedCardDetails(Long cardId, CustomOAuth2User oauth2User) {
        // 사용자 정보 찾기
        MobiUser mobiUser = findMobiUser(oauth2User.getMobiUserId());

        //파라미터로 입력받은 카드ID값에 해당하는 카드 찾기
        OwnedCard ownedCard = ownedCardRepository.findOwnedCardById(cardId)
                .orElseThrow(OwnedCardNotFoundException::new);

        RegisteredCard registeredCard = registeredCardRepository.findByOwnedCardId(cardId)
                .orElseThrow(OwnedCardNotFoundException::new);

        //찾은 카드의 사용자와 요청하는 사용자가 일치하는지 확인(본인카드가 맞는지)
        if (!ownedCard.getMobiUser().getId().equals(mobiUser.getId())) {
            throw new OwnedCardNotFoundException();
        }
        return OwnedCardDetailResponse.fromDetailInfo(ownedCard, registeredCard);
    }

    private MobiUser findMobiUser(Long mobiUserId) {

        return mobiUserRepository.findById(mobiUserId)
                .orElseThrow(MobiUserNotFoundException::new);
    }


}
