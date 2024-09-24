package com.example.merchant.util;

import java.time.LocalDateTime;

public class TimeUtil {

    /*
    * @param expected 예상 시간
    * @param actual 실제 시간
    * @return 예상 시간과 실제 시간이 +-1분 이내로 차이가 나는 경우 true 반환
     */
    public static boolean isSimilarDateTime(LocalDateTime expected, LocalDateTime actual) {
        return expected.minusMinutes(1).isBefore(actual) && expected.plusMinutes(1).isAfter(actual);
    }
}
