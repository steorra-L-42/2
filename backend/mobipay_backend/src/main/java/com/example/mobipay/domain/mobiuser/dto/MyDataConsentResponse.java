package com.example.mobipay.domain.mobiuser.dto;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyDataConsentResponse {

    private final Long mobiUserId;
    private final Boolean myDataConsent;

    public static MyDataConsentResponse newInstance(MobiUser mobiUser) {

        return MyDataConsentResponse.builder()
                .mobiUserId(mobiUser.getId())
                .myDataConsent(mobiUser.getMyDataConsent())
                .build();
    }
}
