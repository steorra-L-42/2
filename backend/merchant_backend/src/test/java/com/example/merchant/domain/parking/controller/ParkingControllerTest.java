package com.example.merchant.domain.parking.controller;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ContextConfiguration(classes = ParkingController.class)
@AutoConfigureMockMvc
class ParkingControllerTest {

    @Value("${merchant.api.key}")
    private static String validMerApiKey;
    private final static String invalidMerApiKey = "invalidMerApiKey";

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected WebApplicationContext context;

    private static Stream<Arguments> invalidParameter() {
        return Stream.of(
                Arguments.of("실패: carNumber가 null일 경우 400 Bad Request", validMerApiKey, null, LocalDateTime.now()),
                Arguments.of("실패: carNumber가 빈 문자열일 경우 400 Bad Request", validMerApiKey, "", LocalDateTime.now()),
                Arguments.of("실패: carNumber가 8자를 초과할 경우 400", "123456789", validMerApiKey, LocalDateTime.now()),
                Arguments.of("실패: carNumber가 이미 주차된 차량일 경우 400 Bad Request", validMerApiKey, "123가4567", LocalDateTime.now()),
                Arguments.of("실패: entry가 null일 경우 400 Bad Request", validMerApiKey, "123가4567", null),
                Arguments.of("실패: entry가 미래인 경우 400 Bad Request", validMerApiKey, "123가4567", LocalDateTime.now().plusDays(1)),
                Arguments.of("실패: merApiKey가 null일 경우 401 Unauthorized", null, "123가4567", LocalDateTime.now()),
                Arguments.of("실패: merApiKey가 빈 문자열일 경우 401 Unauthorized", "", "123가4567", LocalDateTime.now()),
                Arguments.of("실패: merApiKey가 올바르지 않은 경우 401 Unauthorized", invalidMerApiKey, "123가4567", LocalDateTime.now())
        );
    }

    private static Stream<Arguments> validParameter() {
        return Stream.of(
                Arguments.of("성공: 모든 값이 올바른 경우 200 OK", validMerApiKey, "123가4567", LocalDateTime.now())
        );
    }

    // TODO: Implement test cases for ParkingController
    // 실패 : carNumber가 null일 경우 400 Bad Request
    // 실패 : carNumber가 빈 문자열일 경우 400 Bad Request
    // 실패 : carNumber가 8자를 초과할 경우 400 Bad Request
    // 실패 : carNumber가 이미 주차된 차량일 경우 400 Bad Request
    // 실패 : entry가 null일 경우 400 Bad Request
    // 실패 : entry가 미래인 경우 400 Bad Request
    // 실패 : merApiKey가 null일 경우 401 Unauthorized
    // 실패 : merApiKey가 빈 문자열일 경우 401 Unauthorized
    // 실패 : merApiKey가 올바르지 않은 경우 401 Unauthorized
    // 성공: 모든 값이 올바른 경우 200 OK

}