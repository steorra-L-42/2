package com.example.merchant.domain.parking.dto;

import com.example.merchant.global.annotation.ValidRequestTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class ParkingExitRequest {

    @NotBlank(message = "Car number is empty")
    @Size(min = 7, max = 8, message = "Car number must be 7~8 characters")
    private String carNumber;

    @NotNull(message = "Exit time is empty")
    @ValidRequestTime(message = "Exit time must be in +-1 minute from the current time")
    private LocalDateTime exit;
}
