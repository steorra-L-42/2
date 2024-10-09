package com.example.merchant.domain.parking.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ParkingUtil {

    public static int getPaymentBalance(LocalDateTime from, LocalDateTime to) {
        long seconds = ChronoUnit.SECONDS.between(from, to);
        long minutes = (long) Math.ceil(seconds / 60.0); // 초 단위를 60으로 나눠서 올림 처리
        return (int) Math.ceil(minutes / 10.0) * 1000; // 10분당 천원
    }
}
