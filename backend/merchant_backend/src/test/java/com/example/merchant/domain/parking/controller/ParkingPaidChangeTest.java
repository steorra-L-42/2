package com.example.merchant.domain.parking.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.merchant.MerchantApplication;
import com.example.merchant.domain.parking.dto.PaidChangeRequest;
import com.example.merchant.domain.parking.dto.ParkingEntryRequest;
import com.example.merchant.domain.parking.dto.ParkingExitRequest;
import com.example.merchant.domain.parking.entity.Parking;
import com.example.merchant.domain.parking.repository.ParkingRepository;
import com.example.merchant.util.credential.CredentialUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
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
@AutoConfigureMockMvc
@ContextConfiguration(classes = MerchantApplication.class)
public class ParkingPaidChangeTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ParkingRepository parkingRepository;
    @Autowired
    private CredentialUtil credentialUtil;

    private String POS_MER_API_KEY;

    private static Stream<Arguments> BadRequestParameter() {
        return Stream.of(
                Arguments.of("실패: carNumber가 null일 경우 400 Bad Request", null),
                Arguments.of("실패: carNumber가 빈 문자열일 경우 400 Bad Request", ""),
                Arguments.of("실패: carNumber가 8자를 초과할 경우 400", "123456789")
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
                Arguments.of("성공: 모든 값이 올바른 경우 200 OK", "123가4567")
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
        POS_MER_API_KEY = credentialUtil.getPOS_MER_API_KEY();
        parkingRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        parkingRepository.deleteAll();
    }

    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("BadRequestParameter")
    @DisplayName("paid: 400 BadRequest")
    public void changePaidFail(String testName, String carNumber) throws Exception {
        // given
        final String url = "/api/v1/merchants/parking/paid";
        final PaidChangeRequest paidChangeRequest = new PaidChangeRequest(carNumber);
        final String requestBody = objectMapper.writeValueAsString(paidChangeRequest);

        // when
        ResultActions result = mockMvc.perform(patch(url)
                .header("merApiKey", POS_MER_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
        List<Parking> parkingList = parkingRepository.findAll();
        assertThat(parkingList.size()).isZero();
    }

    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("UnauthorizedParameter")
    @DisplayName("paid: 401 Unauthorized")
    public void changePaidFailUnauthorized(String testName, String invalidMerApiKey) throws Exception {
        // given
        final String url = "/api/v1/merchants/parking/paid";
        final PaidChangeRequest paidChangeRequest = new PaidChangeRequest("123가4567");
        final String requestBody = objectMapper.writeValueAsString(paidChangeRequest);

        // when
        ResultActions result = mockMvc.perform(patch(url)
                .header("merApiKey", invalidMerApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isUnauthorized());
        List<Parking> parkingList = parkingRepository.findAll();
        assertThat(parkingList.size()).isZero();
    }

    @Test
    @DisplayName("paid: 400 BadRequest - 실패: Parking한 적 있지만 미결제가 여러번인 차량일 때")
    public void changePaidFailMultipleNotPaid() throws Exception {
        // given
        final String url = "/api/v1/merchants/parking/exit";
        final ParkingExitRequest request = new ParkingExitRequest("123가4567", LocalDateTime.now());
        final String requestBody = objectMapper.writeValueAsString(request);
        parkingRepository.save(Parking.builder()
                .number("123가4567")
                .entry(LocalDateTime.now().minusDays(1))
                .build());

        parkingRepository.save(Parking.builder()
                .number("123가4567")
                .entry(LocalDateTime.now().minusDays(1))
                .build());

        // when
        ResultActions result = mockMvc.perform(patch(url)
                .header("merApiKey", POS_MER_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
        List<Parking> notPaidParkingList = parkingRepository.findAllByNumberAndPaidFalse("123가4567");
        assertThat(notPaidParkingList.size()).isEqualTo(2);
    }

    // 성공: 모든 값이 올바른 경우 200 OK
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("validParameter")
    @DisplayName("paid: 200 OK")
    public void changePaidSuccess(String testName, String carNumber) throws Exception {
        // given
        ParkingEntryRequest request = mock(ParkingEntryRequest.class);
        when(request.getCarNumber()).thenReturn(carNumber);
        when(request.getEntry()).thenReturn(LocalDateTime.now());
        Parking parking = Parking.of(request);
        parkingRepository.save(parking);

        final String url = "/api/v1/merchants/parking/paid";
        final PaidChangeRequest paidChangeRequest = new PaidChangeRequest(carNumber);
        final String requestBody = objectMapper.writeValueAsString(paidChangeRequest);

        // when
        ResultActions result = mockMvc.perform(patch(url)
                .header("merApiKey", POS_MER_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.carNumber").value(carNumber))
                .andExpect(jsonPath("$.paid").value(true));
    }
}
