package com.example.merchant.domain.parking.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ParkingUtil {

    public static int getPaymentBalance(LocalDateTime from, LocalDateTime to) {
        long minutes = ChronoUnit.MINUTES.between(from, to);
        return (int) (minutes / 10) * 1000; // 10분 당 1000원
    }

}
