package com.example.mobipay.car;

import static com.example.mobipay.global.error.ErrorCode.MOBI_USER_NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobipay.MobiPayApplication;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.cargroup.entity.CarGroup;
import com.example.mobipay.domain.cargroup.repository.CarGroupRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.util.SecurityTestUtil;
import org.junit.jupiter.api.AfterEach;
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

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private MobiUserRepository mobiUserRepository;
    @Autowired
    private CarGroupRepository carGroupRepository;

    @Mock
    private CustomOAuth2User customOAuth2User;
    private MobiUser testUser;

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

    @AfterEach
    void cleanUp() {
        carGroupRepository.deleteAll();
        carRepository.deleteAll();
        mobiUserRepository.deleteAll();
    }

    @Test
    @DisplayName("[OK] car list : 자동차 조회")
    void 올바른_차량_조회_테스트() throws Exception {
        SecurityTestUtil.setUpMockUser(customOAuth2User, testUser.getId());

        // testUser의 차량
        Car testUserCar = Car.of("11가1111", "carModel1");
        testUserCar.setOwner(testUser);
        carRepository.save(testUserCar);

        CarGroup testUserCarGroup1 = CarGroup.of(testUserCar, testUser);
        carGroupRepository.save(testUserCarGroup1);

        // mobiUser의 차량
        MobiUser mobiUser = MobiUser.of("email2", "name2", "phoneNumber2", "picture2");
        mobiUserRepository.save(mobiUser);

        Car mobiUserCar = Car.of("22나2222", "carModel2");
        mobiUserCar.setOwner(mobiUser);
        carRepository.save(mobiUserCar);

        CarGroup mobiUserCarGroup = CarGroup.of(mobiUserCar, mobiUser);
        carGroupRepository.save(mobiUserCarGroup);
        /**
         * testUser가 mobiUser의 차량그룹에 추가된 경우
         */
        CarGroup testUserCarGroup2 = CarGroup.of(mobiUserCar, testUser);
        carGroupRepository.save(testUserCarGroup2);

        // when
        ResultActions result = performViewCarList();

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2)); // 차량 개수 테스트

        // carId, number, autoPayStatus, ownerId 테스트
        result.andExpect(jsonPath("$.items[0].carId").value(testUserCar.getId()))
                .andExpect(jsonPath("$.items[0].number").value("11가1111"))
                .andExpect(jsonPath("$.items[0].autoPayStatus").value(false))
                .andExpect(jsonPath("$.items[0].ownerId").value(testUser.getId()))
                .andExpect(jsonPath("$.items[0].carModel").value("carModel1"))

                // testUser가 mobiUser의 차량에 초대받은 경우
                .andExpect(jsonPath("$.items[1].carId").value(mobiUserCar.getId()))
                .andExpect(jsonPath("$.items[1].number").value("22나2222"))
                .andExpect(jsonPath("$.items[1].autoPayStatus").value(false))
                .andExpect(jsonPath("$.items[1].ownerId").value(mobiUser.getId()))
                .andExpect(jsonPath("$.items[1].carModel").value("carModel2"));

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

    private ResultActions performViewCarList() throws Exception {
        return mockMvc.perform(get("/api/v1/cars")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }
}
