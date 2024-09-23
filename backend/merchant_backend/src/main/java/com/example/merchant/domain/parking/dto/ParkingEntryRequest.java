package com.example.merchant.domain.parking.dto;

import jakarta.validation.constraints.Future;
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
public class ParkingEntryRequest {

    @NotBlank(message = "Car number is empty")
    @Size(min = 8, max = 8, message = "Car number must be 8 characters")
    private String carNumber;

    @NotNull(message = "Entry time is empty")
    @Past(message = "Entry time must be in the past")
    private LocalDateTime entry;
}
