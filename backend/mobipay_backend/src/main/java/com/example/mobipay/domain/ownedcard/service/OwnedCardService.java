package com.example.mobipay.domain.ownedcard.service;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.ownedcard.dto.OwnedCardListResponse;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.ownedcard.repository.OwnedCardRepository;
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

    private MobiUser findMobiUser(Long mobiUserId) {

        return mobiUserRepository.findById(mobiUserId)
                .orElseThrow(MobiUserNotFoundException::new);
    }


}
