package com.example.merchant.domain.menu.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MenuListRequest {

    @NotNull
    @Size(min = 7, max = 8, message = "자동차 번호는 7자리 이상 8자리 이하로 입력해주세요.")
    String carNumber;

    @NotNull
    String info;

    @NotNull
    Long roomId;

}
