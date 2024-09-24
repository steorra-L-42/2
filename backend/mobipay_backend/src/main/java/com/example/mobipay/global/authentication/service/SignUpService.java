package com.example.mobipay.global.authentication.service;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;

public interface SignUpService {

    MobiUser signUp(String email, String name, String phoneNumber, String picture);
}
