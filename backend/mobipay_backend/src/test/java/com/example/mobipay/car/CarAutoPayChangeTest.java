package com.example.mobipay.car;

import static com.example.mobipay.global.error.ErrorCode.CAR_NOT_FOUND;
import static com.example.mobipay.global.error.ErrorCode.MOBI_USER_NOT_FOUND;
import static com.example.mobipay.global.error.ErrorCode.NOT_OWNER;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobipay.MobiPayApplication;
import com.example.mobipay.domain.car.dto.CarAutoPayChangeRequest;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.cargroup.repository.CarGroupRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.util.SecurityTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
public class CarAutoPayChangeTest {

    private static final String CREATED_CAR_NUMBER = "TEST_CAR";
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
                Arguments.of("자동결제 상태 true로 변경", true),
                Arguments.of("자동결제 상태 false로 변경", false)
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
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("validParameter")
    @DisplayName("[OK] car change autoPayStatus : 자동차 자동결제 변경")
    void 올바른_자동결제_변경_테스트(String testName, Boolean autoPayStatus) throws Exception {
        SecurityTestUtil.setUpMockUser(customOAuth2User, testUser.getId());
        //when
        ResultActions carRegisterResult = CarTestUtil.performCarRegistration(mockMvc, objectMapper, CREATED_CAR_NUMBER);
        carRegisterResult.andExpect(status().isOk());

        Car createdCar = carRepository.findByNumber(CREATED_CAR_NUMBER).get();
        ResultActions changeAutoPayStatusResult = performChangeAutoPayStatus(createdCar.getId(), autoPayStatus);
        //then
        changeAutoPayStatusResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.carId").value(createdCar.getId()))
                .andExpect(jsonPath("$.number").value(CREATED_CAR_NUMBER))
                .andExpect(jsonPath("$.autoPayStatus").value(autoPayStatus))
                .andExpect(jsonPath("$.ownerId").value(testUser.getId()));

    }

    @Test
    @DisplayName("[NotFound] car change autoPayStatus : 자동차 자동결제 변경(존재하지 않는 유저)")
    void 존재하지_않는_유저_자동결제_변경_테스트() throws Exception {
        SecurityTestUtil.setUpMockUser(customOAuth2User, testUser.getId());
        //when
        ResultActions carRegisterResult = CarTestUtil.performCarRegistration(mockMvc, objectMapper, CREATED_CAR_NUMBER);
        carRegisterResult.andExpect(status().isOk());

        Car createdCar = carRepository.findByNumber(CREATED_CAR_NUMBER).get();
        // 존재하지 않는 유저 테스트이므로 customOAuth2User의 Id를 임의로 바꿔준다.
        SecurityTestUtil.setUpMockUser(customOAuth2User, 123456789L);
        ResultActions changeAutoPayStatusResult = performChangeAutoPayStatus(createdCar.getId(), true);
        //then
        changeAutoPayStatusResult.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MOBI_USER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("[NotFound] car change autoPayStatus : 자동차 자동결제 변경(존재하지 않는 자동차)")
    void 존재하지_않는_자동차_자동결제_변경_테스트() throws Exception {
        SecurityTestUtil.setUpMockUser(customOAuth2User, testUser.getId());
        //when
        ResultActions carRegisterResult = CarTestUtil.performCarRegistration(mockMvc, objectMapper, CREATED_CAR_NUMBER);
        carRegisterResult.andExpect(status().isOk());

        ResultActions changeAutoPayStatusResult = performChangeAutoPayStatus(123456789L, true);
        //then
        changeAutoPayStatusResult.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(CAR_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("[Forbidden] car change autoPayStatus : 자동차 자동결제 변경(차주가 아닌 경우)")
    void 차주가_아닌경우_자동결제_변경_테스트() throws Exception {
        SecurityTestUtil.setUpMockUser(customOAuth2User, testUser.getId());
        //when
        ResultActions carRegisterResult = CarTestUtil.performCarRegistration(mockMvc, objectMapper, CREATED_CAR_NUMBER);
        carRegisterResult.andExpect(status().isOk());

        // 기존 차주와 다른 User를 만들고 customOAuth2User의 Id를 임의로 바꿔준다.
        MobiUser anotherMobiUser = MobiUser.of("email2", "name2", "phoneNumber2", "picture2");
        mobiUserRepository.save(anotherMobiUser);
        SecurityTestUtil.setUpMockUser(customOAuth2User, anotherMobiUser.getId());

        Car createdCar = carRepository.findByNumber(CREATED_CAR_NUMBER).get();
        ResultActions changeAutoPayStatusResult = performChangeAutoPayStatus(createdCar.getId(), true);
        //then
        changeAutoPayStatusResult.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(NOT_OWNER.getMessage()));
    }

    private String createChangeAutoPayStatusRequest(Long carId, Boolean autoPayStatus) throws Exception {
        CarAutoPayChangeRequest carAutoPayChangeRequest = new CarAutoPayChangeRequest(carId, autoPayStatus);
        return objectMapper.writeValueAsString(carAutoPayChangeRequest);
    }

    private ResultActions performChangeAutoPayStatus(Long carId, Boolean autoPayStatus)
            throws Exception {
        String requestBody = createChangeAutoPayStatusRequest(carId, autoPayStatus);
        return mockMvc.perform(patch("/api/v1/cars/auto-pay")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));
    }
}
