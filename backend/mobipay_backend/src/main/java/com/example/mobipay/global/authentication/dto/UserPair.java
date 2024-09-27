package com.example.mobipay.global.authentication.dto;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.ssafyuser.entity.SsafyUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPair {

    private final MobiUser mobiUser;
    private final SsafyUser ssafyUser;
    
}
