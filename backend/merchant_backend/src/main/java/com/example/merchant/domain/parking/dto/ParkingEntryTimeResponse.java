package com.example.merchant.domain.parking.dto;

import com.example.merchant.domain.parking.entity.Parking;
import com.example.merchant.domain.parking.util.ParkingUtil;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ParkingEntryTimeResponse {

    private String parkingLotName;
    private Long parkingId;
    private String carNumber;
    private LocalDateTime entry;
    private int paymentBalance;

    public static ParkingEntryTimeResponse from(Parking parking) {
        return ParkingEntryTimeResponse.builder()
                .parkingLotName("진평주차장")
                .parkingId(parking.getId())
                .carNumber(parking.getNumber())
                .entry(parking.getEntry())
                .paymentBalance(ParkingUtil.getPaymentBalance(parking.getEntry(), LocalDateTime.now()))
                .build();
    }
}
