package com.example.mobipay.invitation;

import static com.example.mobipay.domain.invitation.enums.ApproveStatus.APPROVED;
import static com.example.mobipay.domain.invitation.enums.ApproveStatus.REJECTED;
import static com.example.mobipay.domain.invitation.enums.ApproveStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobipay.MobiPayApplication;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.invitation.dto.InvitationDecisionRequest;
import com.example.mobipay.domain.invitation.entity.Invitation;
import com.example.mobipay.domain.invitation.enums.ApproveStatus;
import com.example.mobipay.domain.invitation.repository.InvitationRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.util.SecurityTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ContextConfiguration(classes = MobiPayApplication.class)
@AutoConfigureMockMvc
public class InvitationDecideTest {

    private static final Logger log = LoggerFactory.getLogger(InvitationDecideTest.class);
    @Mock
    CustomOAuth2User customOAuth2User;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MobiUserRepository mobiUserRepository;
    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private CarRepository carRepository;

    // parameter : invitationId, approved

    @BeforeEach
    void setUp() {
        invitationRepository.deleteAll();
        carRepository.deleteAll();
        mobiUserRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        invitationRepository.deleteAll();
        carRepository.deleteAll();
        mobiUserRepository.deleteAll();
    }

    @Test
    @DisplayName("실패: 400 Bad Request : null인 invitationId")
    void fail400_null_invitationId() throws Exception {
        // given
        MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

        SecurityTestUtil.setUpMockUser(customOAuth2User, invitee.getId());
        final String url = "/api/v1/invitations/null/response";
        final String requestBody = objectMapper.writeValueAsString(new InvitationDecisionRequest(APPROVED));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("실패: 400 Bad Request : 숫자가 아닌 invitationId")
    void fail400_NotNumber_invitationId() throws Exception {
        // given
        MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

        SecurityTestUtil.setUpMockUser(customOAuth2User, invitee.getId());
        final String url = "/api/v1/invitations/one/response";
        final String requestBody = objectMapper.writeValueAsString(new InvitationDecisionRequest(APPROVED));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @EnumSource(value = ApproveStatus.class, names = {"APPROVED", "REJECTED"})
    @DisplayName("실패: 400 Bad Request : 이미 승인 처리된 invitationId")
    void fail400_already_approved_invitationId(ApproveStatus approveStatus) throws Exception {
        // given
        MobiUser owner = mobiUserRepository.save(MobiUser.of(
                "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
        MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

        Car car = Car.of("123가4567", "carModel");
        car.setOwner(owner);
        car = carRepository.save(car);

        Invitation invitation = Invitation.of(car, invitee);
        invitation.approve();
        invitationRepository.save(invitation);

        SecurityTestUtil.setUpMockUser(customOAuth2User, invitee.getId());
        final String url = "/api/v1/invitations/" + invitation.getId() + "/response";
        final String requestBody = objectMapper.writeValueAsString(new InvitationDecisionRequest(approveStatus));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
        invitationRepository.findById(invitation.getId())
                .ifPresent(storedInvitation -> assertThat(storedInvitation.getApproved()).isEqualTo(APPROVED));
    }

    @ParameterizedTest
    @EnumSource(value = ApproveStatus.class, names = {"APPROVED", "REJECTED"})
    @DisplayName("실패: 400 Bad Request : 이미 거절 처리된 invitationId")
    void fail400_already_rejected_invitationId(ApproveStatus approveStatus) throws Exception {
        // given
        MobiUser owner = mobiUserRepository.save(MobiUser.of(
                "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
        MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

        Car car = Car.of("123가4567", "carModel");
        car.setOwner(owner);
        car = carRepository.save(car);

        Invitation invitation = Invitation.of(car, invitee);
        invitation.reject();
        invitationRepository.save(invitation);

        SecurityTestUtil.setUpMockUser(customOAuth2User, invitee.getId());
        final String url = "/api/v1/invitations/" + invitation.getId() + "/response";
        final String requestBody = objectMapper.writeValueAsString(new InvitationDecisionRequest(approveStatus));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
        invitationRepository.findById(invitation.getId())
                .ifPresent(storedInvitation -> assertThat(storedInvitation.getApproved()).isEqualTo(REJECTED));
    }

    @Test
    @DisplayName("실패: 400 Bad Request : null인 approved")
    void fail400_null_approved() throws Exception {
        // given
        MobiUser owner = mobiUserRepository.save(MobiUser.of(
                "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
        MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

        Car car = Car.of("123가4567", "carModel");
        car.setOwner(owner);
        car = carRepository.save(car);

        Invitation invitation = invitationRepository.save(Invitation.of(car, invitee));

        SecurityTestUtil.setUpMockUser(customOAuth2User, invitee.getId());
        final String url = "/api/v1/invitations/" + invitation.getId() + "/response";
        final String requestBody = "{ \"approved\": null }";

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
        invitationRepository.findById(invitation.getId())
                .ifPresent(storedInvitation -> assertThat(storedInvitation.getApproved()).isEqualTo(WAITING));
    }

    @Test
    @DisplayName("실패: 400 Bad Request : 비어있는 approved")
    void fail400_empty_approved() throws Exception {
        // given
        MobiUser owner = mobiUserRepository.save(MobiUser.of(
                "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
        MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

        Car car = Car.of("123가4567", "carModel");
        car.setOwner(owner);
        car = carRepository.save(car);

        Invitation invitation = invitationRepository.save(Invitation.of(car, invitee));

        SecurityTestUtil.setUpMockUser(customOAuth2User, invitee.getId());
        final String url = "/api/v1/invitations/" + invitation.getId() + "/response";
        final String requestBody = "{ \"approved\": \"\" }";

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
        invitationRepository.findById(invitation.getId())
                .ifPresent(storedInvitation -> assertThat(storedInvitation.getApproved()).isEqualTo(WAITING));
    }

    @ParameterizedTest
    @ValueSource(strings = {"WAITING", "DECIDED"})
    @DisplayName("실패: 400 Bad Request : approved가 APPROVED, REJECTED가 아닌 경우")
    void fail400_invalid_approved(String approved) throws Exception {
        // given
        MobiUser owner = mobiUserRepository.save(MobiUser.of(
                "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
        MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

        Car car = Car.of("123가4567", "carModel");
        car.setOwner(owner);
        car = carRepository.save(car);

        Invitation invitation = invitationRepository.save(Invitation.of(car, invitee));

        SecurityTestUtil.setUpMockUser(customOAuth2User, invitee.getId());
        final String url = "/api/v1/invitations/" + invitation.getId() + "/response";
        final String requestBody = "{ \"approved\": \"" + approved + "\" }";

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
        invitationRepository.findById(invitation.getId())
                .ifPresent(storedInvitation -> assertThat(storedInvitation.getApproved()).isEqualTo(WAITING));
    }

    @Test
    @DisplayName("실패: 403 Forbidden : 초대 받은 사람이 아닌 경우")
    void fail403_not_invitee() throws Exception {
        // given
        MobiUser owner = mobiUserRepository.save(MobiUser.of(
                "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
        MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));
        MobiUser notInvitee = mobiUserRepository.save(MobiUser.of(
                "notInvitee@gmail.com", "notInvitee", "010-3333-3333", "notInviteePicture"));

        Car car = Car.of("123가4567", "carModel");
        car.setOwner(owner);
        car = carRepository.save(car);

        Invitation invitation = invitationRepository.save(Invitation.of(car, invitee));

        SecurityTestUtil.setUpMockUser(customOAuth2User, notInvitee.getId());
        final String url = "/api/v1/invitations/" + invitation.getId() + "/response";
        final String requestBody = objectMapper.writeValueAsString(new InvitationDecisionRequest(APPROVED));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isForbidden());
        invitationRepository.findById(invitation.getId())
                .ifPresent(storedInvitation -> assertThat(storedInvitation.getApproved()).isEqualTo(WAITING));
    }

    @Test
    @DisplayName("실패: 404 Bad Request : 비어있는 invitationId")
    void fail404_empty_invitationId() throws Exception {
        // given
        MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

        SecurityTestUtil.setUpMockUser(customOAuth2User, invitee.getId());
        final String url = "/api/v1/invitations//response";
        final String requestBody = objectMapper.writeValueAsString(new InvitationDecisionRequest(APPROVED));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("실패: 404 Not Found : 존재하지 않는 음수인 invitationId")
    void fail404_negative_invitationId() throws Exception {
        // given
        MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

        SecurityTestUtil.setUpMockUser(customOAuth2User, invitee.getId());
        final String url = "/api/v1/invitations/" + -1L + "/response";
        final String requestBody = objectMapper.writeValueAsString(new InvitationDecisionRequest(APPROVED));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("실패: 404 존재하지 않는 invitationId")
    void fail404_not_found_invitationId() throws Exception {
        // given
        MobiUser owner = mobiUserRepository.save(MobiUser.of(
                "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
        MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

        SecurityTestUtil.setUpMockUser(customOAuth2User, invitee.getId());
        final String url = "/api/v1/invitations/" + "999" + "/response";
        final String requestBody = objectMapper.writeValueAsString(new InvitationDecisionRequest(APPROVED));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("성공: 200 OK : APPROVED")
    void success200_approved() throws Exception {
        // given
        MobiUser owner = mobiUserRepository.save(MobiUser.of(
                "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
        MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

        Car car = Car.of("123가4567", "carModel");
        car.setOwner(owner);
        car = carRepository.save(car);

        Invitation invitation = invitationRepository.save(Invitation.of(car, invitee));

        SecurityTestUtil.setUpMockUser(customOAuth2User, invitee.getId());
        final String url = "/api/v1/invitations/" + invitation.getId() + "/response";
        final String requestBody = objectMapper.writeValueAsString(new InvitationDecisionRequest(APPROVED));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk());
        invitationRepository.findById(invitation.getId())
                .ifPresent(storedInvitation -> assertThat(storedInvitation.getApproved()).isEqualTo(APPROVED));
    }


    @Test
    @DisplayName("성공: 200 OK : REJECTED")
    void success200_rejected() throws Exception {
        // given
        MobiUser owner = mobiUserRepository.save(MobiUser.of(
                "owner@gmail.com", "owner", "010-1111-1111", "ownerPicture"));
        MobiUser invitee = mobiUserRepository.save(MobiUser.of(
                "invitee@gmail.com", "invitee", "010-2222-2222", "inviteePicture"));

        Car car = Car.of("123가4567", "carModel");
        car.setOwner(owner);
        car = carRepository.save(car);

        Invitation invitation = invitationRepository.save(Invitation.of(car, invitee));

        SecurityTestUtil.setUpMockUser(customOAuth2User, invitee.getId());
        final String url = "/api/v1/invitations/" + invitation.getId() + "/response";
        final String requestBody = objectMapper.writeValueAsString(new InvitationDecisionRequest(REJECTED));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk());
        invitationRepository.findById(invitation.getId())
                .ifPresent(storedInvitation -> assertThat(storedInvitation.getApproved()).isEqualTo(REJECTED));
    }
}
