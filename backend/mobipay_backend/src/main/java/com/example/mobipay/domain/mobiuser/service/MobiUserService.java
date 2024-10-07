package com.example.mobipay.domain.mobiuser.service;

import com.example.mobipay.domain.mobiuser.dto.MyDataConsentResponse;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MobiUserService {


    private final MobiUserRepository mobiUserRepository;

    @Transactional
    public MyDataConsentResponse approveMyDataConsent(CustomOAuth2User oauth2User) {

        MobiUser mobiUser = mobiUserRepository.findById(oauth2User.getMobiUserId())
                .orElseThrow(MobiUserNotFoundException::new);

        // 동의 활성화
        mobiUser.approveMyDataConsent();

        return MyDataConsentResponse.newInstance(mobiUser);
    }
}
