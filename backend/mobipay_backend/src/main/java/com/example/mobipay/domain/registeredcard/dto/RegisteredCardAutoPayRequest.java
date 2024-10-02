package com.example.mobipay.domain.registeredcard.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class RegisteredCardAutoPayRequest {
    @NotNull(message = "empty cardId")
    private Long ownedCardId;

    @NotNull(message = "empty autoPayStatus")
    private Boolean autoPayStatus;
}
