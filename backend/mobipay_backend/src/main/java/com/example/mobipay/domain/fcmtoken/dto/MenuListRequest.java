package com.example.mobipay.domain.fcmtoken.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class MenuListRequest {

    @NotBlank(message = "Car number is empty")
    @Size(min = 7, max = 8, message = "Car number must be 7 ~ 8 characters")
    private String carNumber;

    @NotNull(message = "Merchant id is empty")
    private Long merchantId;

    @NotNull(message = "Info is empty")
    private String info;

    @NotNull(message = "Room id is empty")
    private Long roomId;
}
