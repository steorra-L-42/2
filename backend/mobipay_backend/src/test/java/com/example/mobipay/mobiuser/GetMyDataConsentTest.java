package com.example.mobipay.mobiuser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobipay.MobiPayApplication;
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

@SpringBootTest
@ContextConfiguration(classes = MobiPayApplication.class)
@AutoConfigureMockMvc
public class GetMyDataConsentTest {

    @Mock
    CustomOAuth2User customOAuth2User;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MobiUserRepository mobiUserRepository;

    @BeforeEach
    void setUp() {
        mobiUserRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        mobiUserRepository.deleteAll();
    }

    @Test
    @DisplayName("[OK] approve my data consent: 소유한 카드 조회 동의 상태 조회")
    void 올바른_소유한_카드_조회_동의_상태_조회_테스트() throws Exception {

        //given
        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser1", "010-1111-1111", "mobiUser1Picture");
        mobiUserRepository.save(mobiUser);

        SecurityTestUtil.setUpMockUser(customOAuth2User, mobiUser.getId());
        final String url = "/api/v1/users/mydata-consent";

        // when
        ResultActions result = mockMvc.perform(get(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.mobiUserId").value(mobiUser.getId()))
                .andExpect(jsonPath("$.myDataConsent").value(false));
    }

    @Test
    @DisplayName("[Not Found] approve my data consent: 소유한 카드 조회 동의 상태 조회(존재하지 않는 유저)")
    void 존재하지_않는_유저_소유한_카드_조회_동의_테스트() throws Exception {

        //given
        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser1", "010-1111-1111", "mobiUser1Picture");
        mobiUserRepository.save(mobiUser);

        SecurityTestUtil.setUpMockUser(customOAuth2User, 123456789L);
        final String url = "/api/v1/users/mydata-consent";

        // when
        ResultActions result = mockMvc.perform(get(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isNotFound());
    }
}
