package com.example.merchant.domain.parking.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ParkingUtil {

    public static int getPaymentBalance(LocalDateTime from, LocalDateTime to) {
        long minutes = ChronoUnit.MINUTES.between(from, to);
        return (int) Math.ceil(minutes / 10.0) * 1000; // 10분당 천원
    }

}
