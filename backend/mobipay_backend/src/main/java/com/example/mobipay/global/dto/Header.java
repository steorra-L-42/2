package com.example.mobipay.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Header {

    private static final String INSTITUTION_CODE = "00100";
    private static final String FINTECH_APP_NO = "001";

    private final String apiName;
    private final String transmissionDate;
    private final String transmissionTime;
    private final String institutionCode;
    private final String fintechAppNo;
    private final String apiServiceCode;
    private final String institutionTransactionUniqueNo;
    private final String apiKey;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String userKey;

    private Header(String apiName, String apiKey, String userKey) {
        this.apiName = apiName;
        this.transmissionDate = getCurrentDate();
        this.transmissionTime = getCurrentTime();
        this.institutionCode = INSTITUTION_CODE;
        this.fintechAppNo = FINTECH_APP_NO;
        this.apiServiceCode = apiName;
        this.institutionTransactionUniqueNo = generateUniqueNo();
        this.apiKey = apiKey;
        this.userKey = userKey;
    }

    public static Header of(String apiName, String apiKey, String userKey) {
        return new Header(apiName, apiKey, userKey);
    }

    public static Header of(String apiName, String apiKey) {
        return new Header(apiName, apiKey, null);
    }

    private String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
    }

    private String generateUniqueNo() {
        String dateTimePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuidPart = generateRandomSixDigitNumber();
        return dateTimePart + uuidPart;
    }

    private String generateRandomSixDigitNumber() {
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000);
        return String.valueOf(randomNumber);
    }
}
