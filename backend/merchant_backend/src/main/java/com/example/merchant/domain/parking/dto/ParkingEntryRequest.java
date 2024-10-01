package com.example.merchant.domain.parking.dto;

import com.example.merchant.global.annotation.ValidRequestTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class ParkingEntryRequest {

    @NotBlank(message = "Car number is empty")
    @Size(min = 7, max = 8, message = "Car number must be 7 ~ 8 characters")
    private String carNumber;

    @NotNull(message = "Entry time is empty")
    @ValidRequestTime(message = "Entry time must be within +-1 minute of the current time")
    private LocalDateTime entry;
}
