package com.example.mobipay.global.authentication.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class FormSignUpRequest {

    @NotNull(message = "empty email")
    private String email;

    @NotNull(message = "empty name")
    private String name;

    @NotNull(message = "empty phoneNumber")
    private String phoneNumber;
}
