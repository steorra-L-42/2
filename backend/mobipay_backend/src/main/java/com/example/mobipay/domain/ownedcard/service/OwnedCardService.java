package com.example.mobipay.domain.ownedcard.service;

import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.ownedcard.repository.OwnedCardRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnedCardService {

    private final OwnedCardRepository ownedCardRepository;

    public List<OwnedCard> getOwnedCardsList(Long mobiUserId) {
        return ownedCardRepository.findByMobiUserId(mobiUserId);
    }

}
