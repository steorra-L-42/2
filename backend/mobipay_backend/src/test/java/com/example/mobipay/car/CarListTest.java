package com.example.mobipay.car;

import static com.example.mobipay.global.error.ErrorCode.MOBI_USER_NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobipay.MobiPayApplication;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.util.SecurityTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
public class CarListTest {

    private static final Integer TEST_CAR_COUNT = 5;
    private static final String TEST_CAR_PREFIX = "testCar";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private MobiUserRepository mobiUserRepository;

    @Mock
    private CustomOAuth2User customOAuth2User;
    private MobiUser testUser;
    private Long[] carIds;

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
        carRepository.deleteAll();
        mobiUserRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @BeforeEach
    void entitySetUp() {
        testUser = MobiUser.of("email", "name", "phoneNumber", "picture");
        mobiUserRepository.save(testUser);
    }

    @Test
    @DisplayName("[OK] car list : 자동차 조회")
    void 올바른_차량_조회_테스트() throws Exception {
        SecurityTestUtil.setUpMockUser(customOAuth2User, testUser.getId());
        createCars();

        // when
        ResultActions result = performViewCarList();

        Car createdCar = carRepository.findByNumber(TEST_CAR_PREFIX + TEST_CAR_COUNT).get();
        Long ownerId = createdCar.getOwner().getId();
        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(TEST_CAR_COUNT)); // 차량 개수 테스트

        // carId, number, autoPayStatus, ownerId 테스트
        for (int i = 0; i < TEST_CAR_COUNT; i++) {
            result.andExpect(jsonPath("$.items[" + i + "].carId").value(carIds[i]))
                    .andExpect(jsonPath("$.items[" + i + "].number").value(TEST_CAR_PREFIX + (i + 1)))
                    .andExpect(jsonPath("$.items[" + i + "].autoPayStatus").value(false))
                    .andExpect(jsonPath("$.items[" + i + "].ownerId").value(ownerId));
        }
    }

    @Test
    @DisplayName("[NoContent] car list : 자동차 조회")
    void 차량_없는경우_테스트() throws Exception {
        SecurityTestUtil.setUpMockUser(customOAuth2User, testUser.getId());

        // when
        ResultActions result = performViewCarList();
        // then
        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("[NotFound] car list : 자동차 조회")
    void 존재하지_않는_유저_차량_등록_테스트() throws Exception {
        SecurityTestUtil.setUpMockUser(customOAuth2User, 123456789L);

        // when
        ResultActions result = performViewCarList();
        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MOBI_USER_NOT_FOUND.getMessage()));
    }

    private void createCars() {
        carIds = new Long[TEST_CAR_COUNT];

        for (int i = 0; i < TEST_CAR_COUNT; i++) {
            Car testCar = Car.from(TEST_CAR_PREFIX + (i + 1));
            testCar.setOwner(testUser);
            carRepository.save(testCar);
            carIds[i] = testCar.getId();
        }
    }

    private ResultActions performViewCarList() throws Exception {
        return mockMvc.perform(get("/api/v1/cars")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }
}
