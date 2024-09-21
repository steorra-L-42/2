package com.example.mobipay.domain.car.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class CarAutoPayChangeRequest {

    @NotNull(message = "empty carId")
    private Long carId;

    @NotNull(message = "empty autoPayStatus")
    private Boolean autoPayStatus;
}
