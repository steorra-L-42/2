package com.example.mobipay.car;

import static com.example.mobipay.global.error.ErrorCode.DUPLICATED_CAR_NUMBER;
import static com.example.mobipay.global.error.ErrorCode.MOBI_USER_NOT_FOUND;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobipay.MobiPayApplication;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.cargroup.repository.CarGroupRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.util.SecurityTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootTest
@ContextConfiguration(classes = MobiPayApplication.class)
@AutoConfigureMockMvc

public class CarRegisterTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CarGroupRepository carGroupRepository;
    @Autowired
    private MobiUserRepository mobiUserRepository;

    @Mock
    private CustomOAuth2User customOAuth2User;
    private MobiUser testUser;

    private static Stream<Arguments> validParameter() {
        return Stream.of(
                Arguments.of("차량 등록 테스트", "09너3649"),
                Arguments.of("차량 등록 테스트", "77칠7777")
        );
    }

    private static Stream<Arguments> ConflictParameter() {
        return Stream.of(
                Arguments.of("중복된 차량 등록 테스트", "testCar")
        );
    }

    private static Stream<Arguments> NotFoundParameter() {
        return Stream.of(
                Arguments.of("존재하지 않는 유저 차량 등록 테스트", "12삼4567")
        );
    }

    @BeforeEach()
    void EncodingSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
    }

    @Transactional
    @BeforeEach
    void mockMvcSetUp() {
        carGroupRepository.deleteAll();
        carRepository.deleteAll();
        mobiUserRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @BeforeEach
    void entitySetUp() {
        testUser = MobiUser.of("email", "name", "phoneNumber", "picture");
        mobiUserRepository.save(testUser);

        Car testCar = Car.from("testCar");
        testCar.setOwner(testUser);
        carRepository.save(testCar);
    }

    @AfterEach
    void cleanUp() {
        carGroupRepository.deleteAll();
        carRepository.deleteAll();
        mobiUserRepository.deleteAll();
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("validParameter")
    @DisplayName("[OK] car register : 자동차 등록")
    void 올바른_차량_등록_테스트(String testName, String carNumber) throws Exception {
        SecurityTestUtil.setUpMockUser(customOAuth2User, testUser.getId());
        // when
        ResultActions result = CarTestUtil.performCarRegistration(mockMvc, objectMapper, carNumber);
        Car createdCar = carRepository.findByNumber(carNumber).get();
        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.carId").value(createdCar.getId()))
                .andExpect(jsonPath("$.number").value(carNumber))
                .andExpect(jsonPath("$.autoPayStatus").value(false))
                .andExpect(jsonPath("$.ownerId").value(testUser.getId()));
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("ConflictParameter")
    @DisplayName("[Conflict] car register : 자동차 등록")
    void 중복된_차량_등록_테스트(String testName, String carNumber) throws Exception {
        SecurityTestUtil.setUpMockUser(customOAuth2User, testUser.getId());
        // when
        ResultActions result = CarTestUtil.performCarRegistration(mockMvc, objectMapper, carNumber);
        // then
        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(DUPLICATED_CAR_NUMBER.getMessage()));
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("NotFoundParameter")
    @DisplayName("[NotFound] car register : 자동차 등록")
    void 존재하지_않는_유저_차량_등록_테스트(String testName, String carNumber) throws Exception {
        SecurityTestUtil.setUpMockUser(customOAuth2User, 123456789L);
        // when
        ResultActions result = CarTestUtil.performCarRegistration(mockMvc, objectMapper, carNumber);
        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MOBI_USER_NOT_FOUND.getMessage()));
    }
}
