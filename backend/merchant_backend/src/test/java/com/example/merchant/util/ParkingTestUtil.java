package com.example.merchant.util;

public class ParkingTestUtil {

    // 100원 이내로 요금이 차이나는 경우 true를 반환
    public static boolean isWithinOneHundredWon(int actual, int expected) {
        return expected - 100 <= actual && actual <= expected + 100;
    }
}
