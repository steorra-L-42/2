package com.example.merchant.domain.menu.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class MobiMenuListRequest {

    @NotNull
    @Size(min = 7, max = 8, message = "자동차 번호는 7자리 이상 8자리 이하로 입력해주세요.")
    String carNumber;

    @NotNull
    Long merchantId;

    @NotNull
    String info;

    @NotNull
    Long roomId;

    public static MobiMenuListRequest of(MenuListRequest menuListRequest, Long merchantId) {
        return MobiMenuListRequest.builder()
                .carNumber(menuListRequest.getCarNumber())
                .info(menuListRequest.getInfo())
                .roomId(menuListRequest.getRoomId())
                .merchantId(merchantId)
                .build();
    }
}
