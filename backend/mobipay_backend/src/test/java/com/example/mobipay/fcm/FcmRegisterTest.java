package com.example.mobipay.fcm;

import static com.example.mobipay.global.error.ErrorCode.MOBI_USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobipay.MobiPayApplication;
import com.example.mobipay.domain.fcmtoken.dto.FcmTokenRequestDto;
import com.example.mobipay.domain.fcmtoken.entity.FcmToken;
import com.example.mobipay.domain.fcmtoken.repository.FcmTokenRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.util.SecurityTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ContextConfiguration(classes = MobiPayApplication.class)
@AutoConfigureMockMvc
public class FcmRegisterTest {

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
    private FcmTokenRepository fcmTokenRepository;

    @BeforeEach
    void setUp() {
        mobiUserRepository.deleteAll();
        fcmTokenRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        mobiUserRepository.deleteAll();
        fcmTokenRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("[OK] register fcm token: FCM 토큰 저장")
    void 올바른_FcmToken_등록_테스트() throws Exception {
        // given
        MobiUser mobiUser = mobiUserRepository.save(MobiUser.of(
                "bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture"));

        SecurityTestUtil.setUpMockUser(customOAuth2User, mobiUser.getId());
        final String url = "/api/v1/fcm/registertoken";
        final String requestBody = objectMapper.writeValueAsString(new FcmTokenRequestDto("fcmTokenValue"));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        FcmToken createdFcmToken = fcmTokenRepository.findByValue("fcmTokenValue").get();

        // then
        result.andExpect(status().isOk());
        assertEquals(mobiUser.getFcmToken(), createdFcmToken);
    }

    @Test
    @DisplayName("[BadRequest] register fcm token: FCM 토큰 저장(토큰 값 null)")
    void FcmToken이_null인경우_테스트() throws Exception {
        // given
        MobiUser mobiUser = mobiUserRepository.save(MobiUser.of(
                "bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture"));

        SecurityTestUtil.setUpMockUser(customOAuth2User, mobiUser.getId());
        final String url = "/api/v1/fcm/registertoken";
        final String requestBody = objectMapper.writeValueAsString(new FcmTokenRequestDto(null));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[NotFound] register fcm token: FCM 토큰 저장(존재하지 않는 유저)")
    void 존재하지_않는_유저_FcmToken_등록_테스트() throws Exception {
        // given
        MobiUser mobiUser = mobiUserRepository.save(MobiUser.of(
                "bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture"));

        SecurityTestUtil.setUpMockUser(customOAuth2User, 123_456_789L);
        final String url = "/api/v1/fcm/registertoken";
        final String requestBody = objectMapper.writeValueAsString(new FcmTokenRequestDto("fcmTokenValue"));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MOBI_USER_NOT_FOUND.getMessage()));
    }

}
