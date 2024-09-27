package com.example.mobipay.invitation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.example.mobipay.MobiPayApplication;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.cargroup.repository.CarGroupRepository;
import com.example.mobipay.domain.invitation.dto.InvitationRequest;
import com.example.mobipay.domain.invitation.entity.Invitation;
import com.example.mobipay.domain.invitation.repository.InvitationRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.util.SecurityTestUtil;
import com.example.mobipay.util.TimeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootTest
@ContextConfiguration(classes = MobiPayApplication.class)
@AutoConfigureMockMvc
public class InvitationInviteTest {

   private static final Logger log = LoggerFactory.getLogger(InvitationInviteTest.class);

    @Autowired
    private WebApplicationContext context;
   @Autowired
    private MockMvc mockMvc;
   @Autowired
    private ObjectMapper objectMapper;

   @MockBean
   private CustomOAuth2User customOAuth2User;
   @MockBean
   private InvitationRepository invitationRepository;
   @MockBean
   private CarGroupRepository carGroupRepository;
    @MockBean
    private MobiUserRepository mobiUserRepository;
    @MockBean
    private CarRepository carRepository;

    @Mock
    private MobiUser owner;
    @Mock
    private MobiUser invitee;
    @Mock
    private Car car;
    @Mock
    private MobiUser alreadyInvited;

    // parameter : phoneNumber, carId
    private static final Long CAR_ID = 1L;
    private static final Long OWNER_ID = 1L;
    private static final Long INVITEE_ID = 2L;
    private static final Long ALREADY_INVITED_ID = 3L;
    private static final String OWNER_PHONE_NUMBER = "010-1111-1111";
    private static final String INVITEE_PHONE_NUMBER = "010-2222-2222";
    private static final String ALREADY_INVITED_PHONE_NUMBER = "010-3333-3333";

    private static Stream<Arguments> BadRequestParameter() {
        return Stream.of(
                Arguments.of("실패: 400 Bad Request : null인 휴대전화 번호", null, CAR_ID),
                Arguments.of("실패: 400 Bad Request : 빈 휴대전화 번호", "", CAR_ID),
                Arguments.of("실패: 400 Bad Request : 20글자보다 긴 휴대전화 번호", "010-1234-5678-1234-5678", CAR_ID),
                Arguments.of("실패: 400 Bad Request : 숫자 또는 - 이외의 문자가 포함된 휴대전화 번호(특수문자)", "010-9999-&999", CAR_ID),
                Arguments.of("실패: 400 Bad Request : 숫자 또는 - 이외의 문자가 포함된 휴대전화 번호(특수문자)", "010-9999-a999", CAR_ID),
                Arguments.of("실패: 400 Bad Request : null인 carId", INVITEE_PHONE_NUMBER, null),
                Arguments.of("실패: 400 Bad Request : 음수 carId", INVITEE_PHONE_NUMBER, -1L)
        );
    }
    private static Stream<Arguments> ConflictParameter() {
        return Stream.of(
                Arguments.of("실패: 409 Conflict : 이미 초대한 멤버", ALREADY_INVITED_PHONE_NUMBER, CAR_ID),
                Arguments.of("실패: 403 Conflict : 차주 본인을 초대", OWNER_PHONE_NUMBER, CAR_ID)
        );
    }

    private static Stream<Arguments> ForbiddenParameter() {
        return Stream.of(
                Arguments.of("실패: 403 Forbidden : 차주가 아님", INVITEE_ID),
                Arguments.of("실패: 403 Forbidden : 존재하지 않는 사용자가 보낸 요청", 999L)
        );
    }

    private static Stream<Arguments> NotFoundParameter() {
        return Stream.of(
                Arguments.of("실패: 404 Not Found : 차량이 존재하지 않음", INVITEE_PHONE_NUMBER, 999L),
                Arguments.of("실패: 404 Not Found : 휴대전화 번호로 가입한 사용자 계정이 존재하지 않음", "010-9999-9999", CAR_ID)
        );
    }

    private static Stream<Arguments> OkParameter() {
        return Stream.of(
                Arguments.of("성공: 200 OK : 초대 요청 성공", INVITEE_PHONE_NUMBER, CAR_ID, INVITEE_ID)
        );
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(customOAuth2User, invitationRepository, carGroupRepository, mobiUserRepository, carRepository, owner, invitee, car, alreadyInvited);

        when(car.getId()).thenReturn(CAR_ID);
        when(car.getOwner()).thenReturn(owner);
        when(carRepository.findCarById(CAR_ID)).thenReturn(Optional.of(car));

        when(owner.getId()).thenReturn(OWNER_ID);
        when(owner.getPhoneNumber()).thenReturn(OWNER_PHONE_NUMBER);
        when(mobiUserRepository.findByPhoneNumber(OWNER_PHONE_NUMBER)).thenReturn(Optional.of(owner));
        when(carGroupRepository.existsByMobiUserIdAndCarId(OWNER_ID, CAR_ID)).thenReturn(true);

        when(invitee.getId()).thenReturn(INVITEE_ID);
        when(invitee.getPhoneNumber()).thenReturn(INVITEE_PHONE_NUMBER);
        when(mobiUserRepository.findByPhoneNumber(INVITEE_PHONE_NUMBER)).thenReturn(Optional.of(invitee));
        when(carGroupRepository.existsByMobiUserIdAndCarId(INVITEE_ID, CAR_ID)).thenReturn(false);

        when(alreadyInvited.getId()).thenReturn(ALREADY_INVITED_ID);
        when(alreadyInvited.getPhoneNumber()).thenReturn(ALREADY_INVITED_PHONE_NUMBER);
        when(mobiUserRepository.findByPhoneNumber(ALREADY_INVITED_PHONE_NUMBER)).thenReturn(Optional.of(alreadyInvited));
        when(carGroupRepository.existsByMobiUserIdAndCarId(ALREADY_INVITED_ID, CAR_ID)).thenReturn(true);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("BadRequestParameter")
    @DisplayName("실패: 400 Bad Request")
    public void BadRequest(String testName, String phoneNumber, Long carId) throws Exception {
        // given
        SecurityTestUtil.setUpMockUser(customOAuth2User, OWNER_ID);
        final String url = "/api/v1/invitations";
        final String requestBody = objectMapper.writeValueAsString(new InvitationRequest(phoneNumber, carId));
        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        // then
        result.andExpect(status().isBadRequest());
        verify(invitationRepository, never()).save(any());
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("ConflictParameter")
    @DisplayName("실패: 409 Conflict")
    public void ConflictRequest(String testName, String phoneNumber, Long carId) throws Exception {
        // given
        SecurityTestUtil.setUpMockUser(customOAuth2User, OWNER_ID);
        final String url = "/api/v1/invitations";
        final String requestBody = objectMapper.writeValueAsString(new InvitationRequest(phoneNumber, carId));
        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
        // then
        result.andExpect(status().isConflict());
        verify(invitationRepository, never()).save(any());
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("ForbiddenParameter")
    @DisplayName("실패: 403 Forbidden")
    public void Forbidden(String testName, Long oauthUserId) throws Exception {
        // given
        SecurityTestUtil.setUpMockUser(customOAuth2User, oauthUserId);
        final String url = "/api/v1/invitations";
        final String requestBody = objectMapper.writeValueAsString(new InvitationRequest(INVITEE_PHONE_NUMBER, CAR_ID));
        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isForbidden());
        verify(invitationRepository, never()).save(any());
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("NotFoundParameter")
    @DisplayName("실패: 404 Not Found")
    public void NotFound(String testName, String phoneNumber, Long carId) throws Exception {
        // given
        SecurityTestUtil.setUpMockUser(customOAuth2User, OWNER_ID);
        final String url = "/api/v1/invitations";
        final String requestBody = objectMapper.writeValueAsString(new InvitationRequest(phoneNumber, carId));
        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
        // then
        result.andExpect(status().isNotFound());
        verify(invitationRepository, never()).save(any());
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("OkParameter")
    @DisplayName("성공: 200 OK")
    public void Ok(String testName, String phoneNumber, Long carId, Long inviteeId) throws Exception {
        // given
        final Long INVITATION_ID = 1L;
        final LocalDateTime CREATED = LocalDateTime.now();
        Invitation invitation = Mockito.mock(Invitation.class);
        when(invitation.getId()).thenReturn(INVITATION_ID);
        when(invitation.getCar()).thenReturn(car);
        when(invitation.getMobiUser()).thenReturn(invitee);
        when(invitation.getCreated()).thenReturn(CREATED);
        when(invitationRepository.save(any())).thenReturn(invitation);

        SecurityTestUtil.setUpMockUser(customOAuth2User, OWNER_ID);
        final String url = "/api/v1/invitations";
        final String requestBody = objectMapper.writeValueAsString(new InvitationRequest(phoneNumber, carId));
        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.invitationId").value(INVITATION_ID))
                .andExpect(jsonPath("$.carId").value(carId ))
                .andExpect(jsonPath("$.mobiUserId").value(inviteeId))
                .andExpect( mvcResult -> {
                    String createdString = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.created");
                    LocalDateTime created = LocalDateTime.parse(createdString);
                    TimeUtil.assertTimeDifference(CREATED, created);
                });

        verify(invitationRepository, Mockito.times(1)).save(any());
    }

    // TODO: 테스트에선 FCM을 사용하지 않음. Mocking 필요.
}
