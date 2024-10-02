package com.example.mobipay.domain.car.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class CarRegisterRequest {

    @NotBlank(message = "empty number")
    private String number;

    @NotBlank(message = "empty carModel")
    @Size(max = 20, message = "too long carModel")
    private String carModel;
}
