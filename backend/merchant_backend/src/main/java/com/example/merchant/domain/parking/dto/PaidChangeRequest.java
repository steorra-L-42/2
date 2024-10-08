package com.example.merchant.domain.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class PaidChangeRequest {

    @NotBlank(message = "Car number is empty")
    @Size(min = 7, max = 8, message = "Car number must be 7 ~ 8 characters")
    private String carNumber;
}
