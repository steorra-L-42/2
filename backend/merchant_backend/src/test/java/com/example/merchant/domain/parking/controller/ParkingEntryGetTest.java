package com.example.merchant.domain.parking.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.example.merchant.MerchantApplication;
import com.example.merchant.domain.parking.dto.ParkingEntryTimeResponse;
import com.example.merchant.domain.parking.entity.Parking;
import com.example.merchant.domain.parking.repository.ParkingRepository;
import com.example.merchant.domain.parking.util.ParkingUtil;
import com.example.merchant.util.TimeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = MerchantApplication.class)
public class ParkingEntryGetTest {

    private final WebApplicationContext context;
    private final ParkingRepository parkingRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    public ParkingEntryGetTest(ParkingRepository parkingRepository, ObjectMapper objectMapper,
                               WebApplicationContext context) {
        this.parkingRepository = parkingRepository;
        this.objectMapper = objectMapper;
        this.context = context;
    }

    @BeforeEach
    void setUp() {
        parkingRepository.deleteAll();
    }
    @BeforeEach()
    void EncodingSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }

    @AfterEach
    void tearDown() {
        parkingRepository.deleteAll();
    }

    @Test
    @DisplayName(("실패 400 Bad Request - 차량 번호 7글자 미만"))
    void fail400_car_number_under7() throws Exception {
        // given
        String carNumber = "123456";
        final String url = "/api/v1/merchants/parking/cars/" + carNumber + "/entry";

        // when
        ResultActions result = mockMvc.perform(get(url));

        // then
        result.andExpect(status().isBadRequest());
    }
    //// 8글자 초과
    @Test
    @DisplayName(("실패 400 Bad Request - 차량 번호 8글자 초과"))
    void fail400_car_number_over8() throws Exception {
        // given
        String carNumber = "123456789";
        final String url = "/api/v1/merchants/parking/cars/" + carNumber + "/entry";

        // when
        ResultActions result = mockMvc.perform(get(url));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("실패 400 Bad Request - 2번 이상 미납된 차량")
    void fail400_multiple_not_paid() throws Exception {
        // given
        Parking parking1 = Parking.builder()
                .number("123가4567")
                .entry(LocalDateTime.now().minusDays(3))
                .build();
        parkingRepository.save(parking1);

        Parking parking2 = Parking.builder()
                .number("123가4567")
                .entry(LocalDateTime.now())
                .build();
        parkingRepository.save(parking2);

        String carNumber = "123가4567";
        final String url = "/api/v1/merchants/parking/cars/" + carNumber + "/entry";

        // when
        ResultActions result = mockMvc.perform(get(url));

        // then
        result.andExpect(status().isBadRequest());
    }

    // 404 Not Found
    //// 존재하지 않는 차량번호
    @Test
    @DisplayName(("실패 404 Not Found - 존재하지 않는 차량번호"))
    void fail404_car_number_not_found() throws Exception {
        // given
        String carNumber = "123가4567";
        final String url = "/api/v1/merchants/parking/cars/" + carNumber + "/entry";

        // when
        ResultActions result = mockMvc.perform(get(url));

        // then
        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("성공 200 OK")
    void success200() throws Exception {
        // given
        Parking parking = Parking.builder()
                .number("123가4567")
                .entry(LocalDateTime.now())
                .build();
        parking = parkingRepository.save(parking);

        final String carNumber = "123가4567";
        final String url = "/api/v1/merchants/parking/cars/" + carNumber + "/entry";
        final int expectedPaymentBalance = ParkingUtil.getPaymentBalance(parking.getEntry(), LocalDateTime.now());

        // when
        ResultActions result = mockMvc.perform(get(url));

        // then
        Parking finalParking = parking;
        result.andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    String content = mvcResult.getResponse().getContentAsString();
                    ParkingEntryTimeResponse actual = objectMapper.readValue(content, ParkingEntryTimeResponse.class);
                    assertThat(actual.getParkingLotName()).isEqualTo("진평주차장");
                    assertThat(actual.getParkingId()).isEqualTo(finalParking.getId());
                    assertThat(actual.getCarNumber()).isEqualTo(finalParking.getNumber());
                    assertThat(TimeUtil.isSimilarDateTime(actual.getEntry(), finalParking.getEntry())).isTrue();
                    assertThat(isWithinOneHundredWon(actual.getPaymentBalance(), expectedPaymentBalance)).isTrue();
                });
    }

    // 100원 이내로 요금이 차이나는 경우 true를 반환
    private boolean isWithinOneHundredWon(int actual, int expected) {
        return expected - 100 <= actual && actual <= expected + 100;
    }
}
