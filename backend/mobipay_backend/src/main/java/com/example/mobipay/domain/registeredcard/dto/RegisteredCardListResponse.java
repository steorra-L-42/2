package com.example.mobipay.domain.registeredcard.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisteredCardListResponse {
    private Long mobiUserId;
    private Long ownedCardId;
    private Integer oneDayLimit;
    private Integer oneTimeLimit;
    private Boolean autoPayStatus;

    private final List<RegisteredCardListResponse> items;

//    public static RegisteredCardListResponse from() {
//        return RegisteredCardListResponse.builder()
//                .
//                .build();
//    }
}
