package com.example.merchant.domain.cancel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class CancelTransactionResponse {

    @NotNull
    private Boolean success;

    @NotNull
    private String message;

}
