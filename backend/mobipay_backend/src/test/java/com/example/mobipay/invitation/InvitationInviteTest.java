package com.example.mobipay.invitation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobipay.MobiPayApplication;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.cargroup.entity.CarGroup;
import com.example.mobipay.domain.cargroup.repository.CarGroupRepository;
import com.example.mobipay.domain.fcmtoken.entity.FcmToken;
import com.example.mobipay.domain.fcmtoken.error.FCMException;
import com.example.mobipay.domain.fcmtoken.repository.FcmTokenRepository;
import com.example.mobipay.domain.fcmtoken.service.FcmService;
import com.example.mobipay.domain.invitation.repository.InvitationRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.util.SecurityTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


@SpringBootTest
@ContextConfiguration(classes = MobiPayApplication.class)
@AutoConfigureMockMvc
public class InvitationInviteTest {

    private final MockMvc mockMvc;

    private final InvitationRepository invitationRepository;
    private final CarGroupRepository carGroupRepository;
    private final MobiUserRepository mobiUserRepository;
    private final CarRepository carRepository;
    private final FcmTokenRepository fcmTokenRepository;

    @MockBean
    private FcmService fcmServiceImpl;

    @MockBean
    private CustomOAuth2User customOAuth2User;

    @Autowired
    public InvitationInviteTest(MockMvc mockMvc, InvitationRepository invitationRepository,
                                CarGroupRepository carGroupRepository,
                                MobiUserRepository mobiUserRepository, CarRepository carRepository,
                                FcmTokenRepository fcmTokenRepository) {
        this.mockMvc = mockMvc;
        this.invitationRepository = invitationRepository;
        this.carGroupRepository = carGroupRepository;
        this.mobiUserRepository = mobiUserRepository;
        this.carRepository = carRepository;
        this.fcmTokenRepository = fcmTokenRepository;
    }

    @BeforeEach
    public void setUp() {
        invitationRepository.deleteAll();
        carGroupRepository.deleteAll();
        carRepository.deleteAll();
        mobiUserRepository.deleteAll();
        fcmTokenRepository.deleteAll();
        Mockito.reset(fcmServiceImpl);
        Mockito.reset(customOAuth2User);
    }

    @AfterEach
    public void tearDown() {
        invitationRepository.deleteAll();
        carGroupRepository.deleteAll();
        carRepository.deleteAll();
        mobiUserRepository.deleteAll();
        fcmTokenRepository.deleteAll();
        Mockito.reset(fcmServiceImpl);
        Mockito.reset(customOAuth2User);
    }

    @Nested
    @DisplayName("실패: 400 Bad Request")
    class fail400 {
        @Test
        @DisplayName("null인 휴대전화 번호")
        public void phoneNumber_null() throws Exception {
            //given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));

            Car car = Car.from("123가4567");
            car.setOwner(owner);
            car = carRepository.save(car);

            SecurityTestUtil.setUpMockUser(customOAuth2User, owner.getId());

            final String url = "/api/v1/invitations";
            final String requestBody = """
                    {
                        "phoneNumber": "%s",
                        "carId": %d
                    }
                    """.formatted(null, car.getId());
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(invitationRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("빈 휴대전화 번호")
        public void phoneNumber_empty() throws Exception {
            //given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));

            Car car = Car.from("123가4567");
            car.setOwner(owner);
            car = carRepository.save(car);

            SecurityTestUtil.setUpMockUser(customOAuth2User, owner.getId());

            final String url = "/api/v1/invitations";
            final String requestBody = """
                    {
                        "phoneNumber": "%s",
                        "carId": %d
                    }
                    """.formatted("", car.getId());
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(invitationRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("20글자보다 긴 휴대전화 번호")
        public void phoneNumber_long() throws Exception {
            //given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));

            Car car = Car.from("123가4567");
            car.setOwner(owner);
            car = carRepository.save(car);

            SecurityTestUtil.setUpMockUser(customOAuth2User, owner.getId());

            final String url = "/api/v1/invitations";
            final String requestBody = """
                    {
                        "phoneNumber": "%s",
                        "carId": %d
                    }
                    """.formatted("123456789012345678901234567890", car.getId());
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(invitationRepository.findAll()).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"010-9999-&999", "010-9999-a999"})
        @DisplayName("숫자 또는 - 이외의 문자가 포함된 휴대전화 번호(특수문자)")
        public void phoneNumber_specialChar(String phoneNumber) throws Exception {
            //given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));

            Car car = Car.from("123가4567");
            car.setOwner(owner);
            car = carRepository.save(car);

            SecurityTestUtil.setUpMockUser(customOAuth2User, owner.getId());

            final String url = "/api/v1/invitations";
            final String requestBody = """
                    {
                        "phoneNumber": "%s",
                        "carId": %d
                    }
                    """.formatted(phoneNumber, car.getId());
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(invitationRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("null인 carId")
        public void carId_null() throws Exception {
            //given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));

            Car car = Car.from("123가4567");
            car.setOwner(owner);
            car = carRepository.save(car);

            SecurityTestUtil.setUpMockUser(customOAuth2User, owner.getId());

            final String url = "/api/v1/invitations";
            final String requestBody = """
                    {
                        "phoneNumber": "%s",
                        "carId": null
                    }
                    """.formatted("010-9999-9999");
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(invitationRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("음수 carId")
        public void carId_negative() throws Exception {
            // given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
            Car car = Car.from("123가4567");
            car.setOwner(owner);
            car = carRepository.save(car);

            SecurityTestUtil.setUpMockUser(customOAuth2User, owner.getId());

            final String url = "/api/v1/invitations";
            final String requestBody = """
                    {
                        "phoneNumber": "%s",
                        "carId": %d
                    }
                    """.formatted("010-9999-9999", -1);
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            result.andExpect(status().isBadRequest());
            assertThat(invitationRepository.findAll()).isEmpty();
        }
    }


    @Nested
    @DisplayName("실패: 409 Conflict")
    class fail409 {
        @Test
        @DisplayName("이미 초대한 멤버")
        public void alreadyInvited() throws Exception {
            // given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
            MobiUser alreadyInvited = mobiUserRepository.save(MobiUser.of(
                    "alreadyInvited@gmail.com", "alreadyInvited", "010-2222-2222", "alreadyInvitedPicture"));

            Car car = Car.from("123가4567");
            car.setOwner(owner);
            car = carRepository.save(car);

            carGroupRepository.save(CarGroup.of(car, owner));
            carGroupRepository.save(CarGroup.of(car, alreadyInvited));

            SecurityTestUtil.setUpMockUser(customOAuth2User, owner.getId());

            final String url = "/api/v1/invitations";
            final String requestBody = """
                    {
                        "phoneNumber": "%s",
                        "carId": %d
                    }
                    """.formatted(alreadyInvited.getPhoneNumber(), car.getId());
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            result.andExpect(status().isConflict());
            assertThat(invitationRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("차주 본인을 초대")
        public void ownerInvited() throws Exception {
            // given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));

            Car car = Car.from("123가4567");
            car.setOwner(owner);
            car = carRepository.save(car);

            carGroupRepository.save(CarGroup.of(car, owner));

            SecurityTestUtil.setUpMockUser(customOAuth2User, owner.getId());

            final String url = "/api/v1/invitations";
            final String requestBody = """
                    {
                        "phoneNumber": "%s",
                        "carId": %d
                    }
                    """.formatted(owner.getPhoneNumber(), car.getId());
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            result.andExpect(status().isConflict());
            assertThat(invitationRepository.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("실패: 403 Forbidden")
    class fail403 {
        @Test
        @DisplayName("차주가 아님")
        public void notOwner() throws Exception {
            // given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
            MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                    "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));
            MobiUser notOwner = mobiUserRepository.save(MobiUser.of(
                    "notOnwer", "notOwner", "010-3333-3333", "notOwnerPicture"));

            Car car = Car.from("123가4567");
            car.setOwner(owner);
            car = carRepository.save(car);

            carGroupRepository.save(CarGroup.of(car, owner));

            SecurityTestUtil.setUpMockUser(customOAuth2User, notOwner.getId());

            final String url = "/api/v1/invitations";
            final String requestBody = """
                    {
                        "phoneNumber": "%s",
                        "carId": %d
                    }
                    """.formatted(invitee.getPhoneNumber(), car.getId());
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            result.andExpect(status().isForbidden());
            assertThat(invitationRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 사용자가 보낸 요청")
        public void notExistUser() throws Exception {
            // given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
            MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                    "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

            Car car = Car.from("123가4567");
            car.setOwner(owner);
            car = carRepository.save(car);

            carGroupRepository.save(CarGroup.of(car, owner));

            Long notExistUserId = owner.getId() + invitee.getId();
            SecurityTestUtil.setUpMockUser(customOAuth2User, notExistUserId);

            final String url = "/api/v1/invitations";
            final String requestBody = """
                    {
                        "phoneNumber": "%s",
                        "carId": %d
                    }
                    """.formatted(invitee.getPhoneNumber(), car.getId());
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            result.andExpect(status().isForbidden());
            assertThat(invitationRepository.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("실패: 404 Not Found")
    class fail404 {
        @Test
        @DisplayName("차량이 존재하지 않음")
        public void notExistCar() throws Exception {
            // given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
            MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                    "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

            Long notExistUserId = owner.getId() + invitee.getId();
            SecurityTestUtil.setUpMockUser(customOAuth2User, notExistUserId);

            final String url = "/api/v1/invitations";
            final String requestBody = """
                    {
                        "phoneNumber": "%s",
                        "carId": %d
                    }
                    """.formatted(invitee.getPhoneNumber(), 999L);
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            result.andExpect(status().isNotFound());
            assertThat(invitationRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("휴대전화 번호로 가입한 사용자 계정이 존재하지 않음")
        public void notExistUser() throws Exception {
            // given
            MobiUser owner = mobiUserRepository.save(MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));

            Car car = Car.from("123가4567");
            car.setOwner(owner);
            car = carRepository.save(car);

            SecurityTestUtil.setUpMockUser(customOAuth2User, owner.getId());

            final String url = "/api/v1/invitations";
            final String requestBody = """
                    {
                        "phoneNumber": "%s",
                        "carId": %d
                    }
                    """.formatted("010-9999-9999", car.getId());

            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            result.andExpect(status().isNotFound());
            assertThat(invitationRepository.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("실패: 500 Internal Server Error")
    class fail500 {
        @Test
        @DisplayName("FCM 전송 실패")
        public void fcmFail() throws Exception {
            // given
            doThrow(new FCMException("fcm push failed")).when(fcmServiceImpl).sendMessage(any());

            Mockito.when(customOAuth2User.getName()).thenReturn("owner");
            Mockito.when(customOAuth2User.getPicture()).thenReturn("ownerPicture");

            MobiUser owner = MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture");
            MobiUser invitee = MobiUser.of(
                    "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture");

            FcmToken ownerFcmToken = fcmTokenRepository.save(FcmToken.from("ownerToken"));
            owner.setFcmToken(ownerFcmToken);
            FcmToken inviteeFcmToken = fcmTokenRepository.save(FcmToken.from("inviteeToken"));
            invitee.setFcmToken(inviteeFcmToken);

            owner = mobiUserRepository.save(owner);
            invitee = mobiUserRepository.save(invitee);

            Car car = Car.from("123가4567");
            car.setOwner(owner);
            car = carRepository.save(car);

            SecurityTestUtil.setUpMockUser(customOAuth2User, owner.getId());

            final String url = "/api/v1/invitations";
            final String requestBody = """
                    {
                        "phoneNumber": "%s",
                        "carId": %d
                    }
                    """.formatted(invitee.getPhoneNumber(), car.getId());
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            result.andExpect(status().isInternalServerError());
            assertThat(invitationRepository.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("성공: 200 OK")
    class success200 {
        @Test
        @DisplayName("초대 요청 성공")
        public void success() throws Exception {
            // given
            Mockito.when(customOAuth2User.getName()).thenReturn("owner");
            Mockito.when(customOAuth2User.getPicture()).thenReturn("ownerPicture");

            MobiUser owner = MobiUser.of(
                    "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture");
            MobiUser invitee = MobiUser.of(
                    "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture");

            FcmToken ownerFcmToken = fcmTokenRepository.save(FcmToken.from("ownerToken"));
            owner.setFcmToken(ownerFcmToken);
            FcmToken inviteeFcmToken = fcmTokenRepository.save(FcmToken.from("inviteeToken"));
            invitee.setFcmToken(inviteeFcmToken);

            owner = mobiUserRepository.save(owner);
            invitee = mobiUserRepository.save(invitee);

            Car car = Car.from("123가4567");
            car.setOwner(owner);
            car = carRepository.save(car);

            SecurityTestUtil.setUpMockUser(customOAuth2User, owner.getId());

            final String url = "/api/v1/invitations";
            final String requestBody = """
                    {
                        "phoneNumber": "%s",
                        "carId": %d
                    }
                    """.formatted(invitee.getPhoneNumber(), car.getId());
            // when
            ResultActions result = mockMvc.perform(post(url).with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));
            // then
            result.andExpect(status().isOk());
            assertThat(invitationRepository.findAll().size()).isOne();
        }
    }
}
