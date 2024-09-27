package com.example.mobipay.domain.invitation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class InvitationRequest {

    @NotBlank(message = "Phone number is empty")
    @Size(max = 20, message = "Phone number is too long")
    @Pattern(regexp = "^[0-9-]*$", message = "Phone number is not numeric or '-'.")
    private String phoneNumber;

    @NotNull(message = "Car ID is null")
    @Positive(message = "Car ID is not positive")
    private Long carId;

}
