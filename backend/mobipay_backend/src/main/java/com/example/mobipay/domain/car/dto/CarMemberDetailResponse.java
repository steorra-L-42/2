package com.example.mobipay.domain.car.dto;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CarMemberDetailResponse {
    private Long memberId;
    private String name;
    private String picture;
    private String phoneNumber;

    public static CarMemberDetailResponse from(MobiUser mobiUser) {
        return CarMemberDetailResponse.builder()
                .memberId(mobiUser.getId())
                .name(mobiUser.getName())
                .picture(mobiUser.getPicture())
                .phoneNumber(mobiUser.getPhoneNumber())
                .build();
    }
}
