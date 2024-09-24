package com.example.merchant.domain.parking.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.merchant.MerchantApplication;
import com.example.merchant.domain.parking.dto.ParkingEntryRequest;
import com.example.merchant.domain.parking.entity.Parking;
import com.example.merchant.domain.parking.repository.ParkingRepository;
import com.example.merchant.util.TimeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootTest
@ContextConfiguration(classes = MerchantApplication.class)
@AutoConfigureMockMvc
class ParkingEntryTest {

    private static final Logger log = LoggerFactory.getLogger(ParkingEntryTest.class);

    @Value("${merchant.api.key}")
    private String validMerApiKey;

    private final WebApplicationContext context;
    private final ParkingRepository parkingRepository;
    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;

    @Autowired
    public ParkingEntryTest(WebApplicationContext context, MockMvc mockMvc,
                            ObjectMapper objectMapper, ParkingRepository parkingRepository) {
        this.context = context;
        this.parkingRepository = parkingRepository;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    private static Stream<Arguments> BadRequestParameter() {
        return Stream.of(
                Arguments.of("실패: carNumber가 null일 경우 400 Bad Request", null, LocalDateTime.now().toString()),
                Arguments.of("실패: carNumber가 빈 문자열일 경우 400 Bad Request", "", LocalDateTime.now().toString()),
                Arguments.of("실패: carNumber가 8자를 초과할 경우 400", "123456789", LocalDateTime.now().toString()),
                Arguments.of("실패: entry가 null인 경우 400 Bad Request", "123가4567", "null"),
                Arguments.of("실패: entry가 미래인 경우 400 Bad Request", "123가4567", LocalDateTime.now().plusDays(1).toString())
        );
    }

    private static Stream<Arguments> UnauthorizedParameter() {
        return Stream.of(
                Arguments.of("실패: merApiKey가 올바르지 않은 경우 401 Unauthorized", "invalidMerApiKey"),
                Arguments.of("실패: merApiKey가 빈 문자열일 경우 401 Unauthorized", "")
        );
    }

    private static Stream<Arguments> validParameter() {
        return Stream.of(
                Arguments.of("성공: 모든 값이 올바른 경우 200 OK", "123가4567", LocalDateTime.now().toString())
        );
    }

    @BeforeEach()
    void EncodingSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }

    @BeforeEach
    public void setUp() {
        parkingRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        parkingRepository.deleteAll();
    }

    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("BadRequestParameter")
    @DisplayName("entry: 400 BadRequest")
    public void entryFail(String testName, String carNumber, String entry)
            throws Exception {
        // given
        final String url = "/api/v1/merchants/parking/entry";
        final ParkingEntryRequest parkingEntryRequest = new ParkingEntryRequest(carNumber, TimeUtil.parseDateTime(entry));
        final String requestBody = objectMapper.writeValueAsString(parkingEntryRequest);

        // when
        ResultActions result = mockMvc.perform(post(url)
                        .header("merApiKey", validMerApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
        List<Parking> parkingList = parkingRepository.findAll();
        assertThat(parkingList.size()).isZero();
    }

    @Test
    @DisplayName("entry: 400 BadRequest - 실패: carNumber가 이미 주차된 차량일 경우")
    public void entryFailCarNumberAlreadyParked() throws Exception {
        // given
        final String url = "/api/v1/merchants/parking/entry";
        final ParkingEntryRequest parkingEntryRequest = new ParkingEntryRequest("123가4567", LocalDateTime.now());
        final String requestBody = objectMapper.writeValueAsString(parkingEntryRequest);
        parkingRepository.save(new Parking("123가4567", LocalDateTime.now()));

        // when
        ResultActions result = mockMvc.perform(post(url)
                        .header("merApiKey", validMerApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
        List<Parking> parkingList = parkingRepository.findAll();
        assertThat(parkingList.size()).isEqualTo(1);
    }

    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("UnauthorizedParameter")
    @DisplayName("entry: 401 Unauthorized")
    public void entryFailUnauthorized(String testName, String invalidMerApiKey)
            throws Exception {
        // given
        final String url = "/api/v1/merchants/parking/entry";
        final ParkingEntryRequest parkingEntryRequest = new ParkingEntryRequest(
//                "123가4567", LocalDateTime.now().minusMinutes(1));
                "123가4567", LocalDateTime.now()); // TODO: 1분 마진
        final String requestBody = objectMapper.writeValueAsString(parkingEntryRequest);

        // when
        ResultActions result = mockMvc.perform(post(url)
                        .header("merApiKey", invalidMerApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        // then
        result.andExpect(status().isUnauthorized());
        List<Parking> parkingList = parkingRepository.findAll();
        assertThat(parkingList.size()).isZero();
    }

    // 성공: 모든 값이 올바른 경우 200 OK
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("validParameter")
    @DisplayName("entry: 200 OK")
    public void entrySuccess(String testName, String carNumber, String entry)
            throws Exception {
        // given
        final String url = "/api/v1/merchants/parking/entry";
        final ParkingEntryRequest parkingEntryRequest = new ParkingEntryRequest(carNumber, TimeUtil.parseDateTime(entry));
        final String requestBody = objectMapper.writeValueAsString(parkingEntryRequest);

        // when
        ResultActions result = mockMvc.perform(post(url)
                        .header("merApiKey", validMerApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        // then
        result.andExpect(status().isOk());
        List<Parking> parkingList = parkingRepository.findAll();
        assertThat(parkingList.size()).isEqualTo(1);
        assertThat(TimeUtil.isSimilarDateTime(parkingList.get(0).getEntry(), TimeUtil.parseDateTime(entry))).isTrue();
        assertThat(parkingList.get(0).getPaid()).isFalse();
        assertThat(parkingList.get(0).getExit()).isNull();
    }
}