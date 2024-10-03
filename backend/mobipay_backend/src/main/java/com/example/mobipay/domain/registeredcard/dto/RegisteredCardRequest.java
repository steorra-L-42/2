package com.example.mobipay.domain.registeredcard.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class RegisteredCardRequest {
    @NotNull(message = "empty cardId")
    private Long ownedCardId;

    @Min(value = 1, message = "oneDayLimit : minimum value is 1")
    @Max(value = 10000000, message = "oneDayLimit : maximum value is 10,000,000")
    @NotNull(message = "empty oneDayLimit")
    private Integer oneDayLimit;

    @Min(value = 1, message = "oneTimeLimit : minimum value is 1")
    @Max(value = 1000000, message = "oneTimeLimit : maximum value is 1,000,000")
    @NotNull(message = "empty oneTimeLimit")
    private Integer oneTimeLimit;

    @NotNull(message = "empty password")
    private String password;

    @AssertTrue(message = "oneDayLimit must be greater than or equal to oneTimeLimit")
    public boolean isOneDayLimitValid() {
        // oneDayLimit이 oneTimeLimit보다 크거나 같아야 함
        return oneDayLimit == null || oneTimeLimit == null || oneDayLimit >= oneTimeLimit;
    }
}
