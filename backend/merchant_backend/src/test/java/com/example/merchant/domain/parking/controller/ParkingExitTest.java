package com.example.merchant.domain.parking.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.merchant.MerchantApplication;
import com.example.merchant.domain.parking.dto.ParkingExitRequest;
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
class ParkingExitTest {

    private static final Logger log = LoggerFactory.getLogger(ParkingExitTest.class);

    @Value("${merchant.api.key}")
    private String validMerApiKey;

    private final WebApplicationContext context;
    private final ParkingRepository parkingRepository;
    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;

    @Autowired
    public ParkingExitTest(WebApplicationContext context, MockMvc mockMvc,
                           ObjectMapper objectMapper, ParkingRepository parkingRepository) {
        this.context = context;
        this.parkingRepository = parkingRepository;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @BeforeEach
    void setup() {
        parkingRepository.deleteAll();
        parkingRepository.save(Parking.builder()
                .number("123가4567")
                .entry(LocalDateTime.now().minusHours(1))
                .build());
    }

    @AfterEach
    void tearDown() {
        parkingRepository.deleteAll();
    }

    private static Stream<Arguments> BadRequestParameter(){
        return Stream.of(
                Arguments.of("실패: carNumber가 null일 경우 400 Bad Request", null, LocalDateTime.now().toString()),
                Arguments.of("실패: carNumber가 빈 문자열일 경우 400 Bad Request", "", LocalDateTime.now().toString()),
                Arguments.of("실패: carNumber가 8자리가 아닐 경우 400 Bad Request", "123456", LocalDateTime.now().toString()),
                Arguments.of("실패: exit가 null일 경우 400 Bad Request", "123가4567", "null"),
                Arguments.of("실패: exit가 하루 전인 경우 400 Bad Request", "123가4567", LocalDateTime.now().minusDays(1).toString()),
                Arguments.of("실패: exit가 입차 시간보다 과거인 경우 400 Bad Request", "123가4567", LocalDateTime.now().minusDays(5).toString())
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
                Arguments.of("성공: Parking한 차량이 나갈 때 200 OK", "123가4567", LocalDateTime.now().toString())
        );
    }

    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("BadRequestParameter")
    @DisplayName("exit: 400 BadRequest")
    public void exitFail(String testName, String carNumber, String exit) throws Exception {
        // given
        final String url = "/api/v1/merchants/parking/exit";
        final ParkingExitRequest request = new ParkingExitRequest(carNumber, TimeUtil.parseDateTime(exit));
        final String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions result = mockMvc.perform(patch(url)
                        .header("merApiKey", validMerApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
        List<Parking> notPaidParkingList = parkingRepository.findAllByNumberAndPaidFalse("123가4567");
        assertThat(notPaidParkingList.size()).isOne();
    }

    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("UnauthorizedParameter")
    @DisplayName("exit: 401 Unauthorized")
    public void exitFailUnauthorized(String testName, String merApiKey) throws Exception {
        // given
        final String url = "/api/v1/merchants/parking/exit";
        final ParkingExitRequest request = new ParkingExitRequest("123가4567", LocalDateTime.now());
        final String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions result = mockMvc.perform(patch(url)
                        .header("merApiKey", merApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        // then
        result.andExpect(status().isUnauthorized());
        List<Parking> notPaidParkingList = parkingRepository.findAllByNumberAndPaidFalse("123가4567");
        assertThat(notPaidParkingList.size()).isOne();
    }

    @Test
    @DisplayName("exit: 404 NotFound - 실패: Parking한 적 없는 차량이 나갈 때")
    public void exitFailNotFound() throws Exception {
        // given
        final String url = "/api/v1/merchants/parking/exit";
        final ParkingExitRequest request = new ParkingExitRequest("999가9999", LocalDateTime.now());
        final String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions result = mockMvc.perform(patch(url)
                        .header("merApiKey", validMerApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        // then
        result.andExpect(status().isNotFound());
        List<Parking> notPaidParkingList = parkingRepository.findAllByNumberAndPaidFalse("123가4567");
        assertThat(notPaidParkingList.size()).isOne();
    }

    @Test
    @DisplayName("exit: 400 BadRequest - 실패: Parking한 적 있지만 미결제가 여러번인 차량이 나갈 때")
    public void exitFailMultipleNotPaid() throws Exception {
        // given
        final String url = "/api/v1/merchants/parking/exit";
        final ParkingExitRequest request = new ParkingExitRequest("123가4567", LocalDateTime.now());
        final String requestBody = objectMapper.writeValueAsString(request);
        parkingRepository.save(Parking.builder()
                .number("123가4567")
                .entry(LocalDateTime.now().minusDays(1))
                .build());

        // when
        ResultActions result = mockMvc.perform(patch(url)
                        .header("merApiKey", validMerApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
        List<Parking> notPaidParkingList = parkingRepository.findAllByNumberAndPaidFalse("123가4567");
        assertThat(notPaidParkingList.size()).isEqualTo(2);
    }

    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("validParameter")
    @DisplayName("exit: 200 OK")
    public void exitSuccess(String testName, String carNumber, String exit) throws Exception {
        // given
        final String url = "/api/v1/merchants/parking/exit";
        final ParkingExitRequest request = new ParkingExitRequest(carNumber, TimeUtil.parseDateTime(exit));
        final String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions result = mockMvc.perform(patch(url)
                        .header("merApiKey", validMerApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody));

        // then
        result.andExpect(status().isOk());
        List<Parking> notPaidParkingList = parkingRepository.findAllByNumberAndPaidFalse("123가4567");
        assertThat(notPaidParkingList.size()).isOne();
        assertThat(TimeUtil.isSimilarDateTime(notPaidParkingList.get(0).getExit(), LocalDateTime.now())).isTrue();
    }
}
