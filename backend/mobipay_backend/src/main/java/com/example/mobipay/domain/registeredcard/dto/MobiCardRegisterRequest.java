package com.example.mobipay.domain.registeredcard.dto;

import lombok.Getter;

@Getter
public class MobiCardRegisterRequest {

    private Long mobiUserId;
    private Long ownedCardId;

    private Integer oneDayLimit;
    private Integer oneTimeLimit;

    private String password;
    private Boolean autoPayStatus;
}
