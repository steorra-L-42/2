package com.example.mobipay.car;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ContextConfiguration(classes = MobiPayApplication.class)
@AutoConfigureMockMvc
public class CarMemberListTest {

    private static final Logger log = LoggerFactory.getLogger(CarMemberListTest.class);
    @Mock
    CustomOAuth2User customOAuth2User;
    @Autowired
    private WebApplicationContext contest;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private MobiUserRepository mobiUserRepository;
    @Autowired
    private CarGroupRepository carGroupRepository;

    @BeforeEach
    void setUp() {
        carGroupRepository.deleteAll();
        carRepository.deleteAll();
        mobiUserRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        carGroupRepository.deleteAll();
        carRepository.deleteAll();
        mobiUserRepository.deleteAll();
    }

    @Test
    @DisplayName("실패: 403 Forbidden : 차량의 멤버가 아닌 경우")
    void fail_403_not_car_member() throws Exception {
        // given
        MobiUser owner = mobiUserRepository.save(MobiUser.of(
                "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
        MobiUser nobody = mobiUserRepository.save(MobiUser.of(
                "nobody@gmail.com", "nobody", "010-2222-2222", "noBodyPicture"));

        Car car = Car.of("123가4567", "carModel");
        car.setOwner(owner);
        car = carRepository.save(car);
        carGroupRepository.save(CarGroup.of(car, owner));

        SecurityTestUtil.setUpMockUser(customOAuth2User, nobody.getId());
        final String url = "/api/v1/cars/" + car.getId() + "/members";

        // when
        ResultActions result = mockMvc.perform(get(url));

        // then
        result.andExpect(status().isForbidden());
    }

    // 실패: 400 Bad Request : carId가 null인 경우
    // 실패: 400 Bad Request : carId가 숫자가 아닌 경우
    @Nested
    @DisplayName("실패: 400 Bad Request")
    class Fail_400 {
        @Test
        @DisplayName("carId가 null인 경우")
        void carId_is_null() throws Exception {
            // given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));

            SecurityTestUtil.setUpMockUser(customOAuth2User, owner.getId());
            final String url = "/api/v1/cars/null/members";
            // when
            ResultActions result = mockMvc.perform(get(url));

            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("carId가 숫자가 아닌 경우")
        void carId_is_not_a_number() throws Exception {
            // given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));

            SecurityTestUtil.setUpMockUser(customOAuth2User, owner.getId());
            final String url = "/api/v1/cars/NotNumber/members";
            // when
            ResultActions result = mockMvc.perform(get(url));

            // then
            result.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("실패: 404 Not Found")
    class Fail_404 {
        @Test
        @DisplayName("가입되지 않은 유저가 보내는 요청")
        void not_joined_mobiUser() throws Exception {
            // given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));

            Car car = Car.of("123가4567", "carModel");
            car.setOwner(owner);
            car = carRepository.save(car);
            carGroupRepository.save(CarGroup.of(car, owner));

            final Long NotJoinedMobiUserId = owner.getId() + 999L;
            SecurityTestUtil.setUpMockUser(customOAuth2User, NotJoinedMobiUserId);
            final String url = "/api/v1/cars/" + car.getId() + "/members";

            // when
            ResultActions result = mockMvc.perform(get(url));

            // then
            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("carId가 없는 경우")
        void carId_is_empty() throws Exception {
            // given
            MobiUser user = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));

            SecurityTestUtil.setUpMockUser(customOAuth2User, user.getId());
            final String url = "/api/v1/cars//members";

            // when
            ResultActions result = mockMvc.perform(get(url));

            // then
            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("존재하지 않는 carId")
        void carId_not_exist() throws Exception {
            // given
            MobiUser user = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));

            SecurityTestUtil.setUpMockUser(customOAuth2User, user.getId());
            final String url = "/api/v1/cars/999/members";

            // when
            ResultActions result = mockMvc.perform(get(url));

            // then
            result.andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("성공: 200 OK")
    class Success_200 {
        @Test
        @DisplayName("차주 본인")
        void owner() throws Exception {
            // given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));

            Car car = Car.of("123가4567", "carModel");
            car.setOwner(owner);
            car = carRepository.save(car);
            carGroupRepository.save(CarGroup.of(car, owner));

            SecurityTestUtil.setUpMockUser(customOAuth2User, owner.getId());
            final String url = "/api/v1/cars/" + car.getId() + "/members";

            // when
            ResultActions result = mockMvc.perform(get(url));

            // then
            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$.items.size()").value(1));
            result.andExpect(jsonPath("$.items[0].memberId").value(owner.getId()));
            result.andExpect(jsonPath("$.items[0].name").value(owner.getName()));
            result.andExpect(jsonPath("$.items[0].picture").value(owner.getPicture()));
            result.andExpect(jsonPath("$.items[0].phoneNumber").value(owner.getPhoneNumber()));
        }

        @Test
        @DisplayName("차주가 아닌 경우")
        void not_owner() throws Exception {
            // given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
            MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                    "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

            Car car = Car.of("123가4567", "carModel");
            car.setOwner(owner);
            car = carRepository.save(car);
            carGroupRepository.save(CarGroup.of(car, owner));
            carGroupRepository.save(CarGroup.of(car, invitee));

            SecurityTestUtil.setUpMockUser(customOAuth2User, invitee.getId());
            final String url = "/api/v1/cars/" + car.getId() + "/members";

            // when
            ResultActions result = mockMvc.perform(get(url));

            // then
            result.andExpect(status().isOk());
            result.andExpect(jsonPath("$.items.size()").value(2));
            result.andExpect(jsonPath("$.items[0].memberId").value(owner.getId()));
            result.andExpect(jsonPath("$.items[0].name").value(owner.getName()));
            result.andExpect(jsonPath("$.items[0].picture").value(owner.getPicture()));
            result.andExpect(jsonPath("$.items[0].phoneNumber").value(owner.getPhoneNumber()));
            result.andExpect(jsonPath("$.items[1].memberId").value(invitee.getId()));
            result.andExpect(jsonPath("$.items[1].name").value(invitee.getName()));
            result.andExpect(jsonPath("$.items[1].picture").value(invitee.getPicture()));
            result.andExpect(jsonPath("$.items[1].phoneNumber").value(invitee.getPhoneNumber()));
        }
    }
}
