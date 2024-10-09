package com.example.merchant.domain.menu;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.merchant.MerchantApplication;
import com.example.merchant.domain.cancel.CancelTransactionTest;
import com.example.merchant.util.credential.CredentialUtil;
import com.example.merchant.util.mobipay.MobiPay;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Menu;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = MerchantApplication.class)
public class MenuTest {

    private static final Logger log = LoggerFactory.getLogger(CancelTransactionTest.class);

    private final WebApplicationContext context;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final CredentialUtil credentialUtil;
    private String POS_MER_API_KEY;

    @MockBean
    private MobiPay mobiPay;

    @Autowired
    public MenuTest(WebApplicationContext context, MockMvc mockMvc, ObjectMapper objectMapper, CredentialUtil credentialUtil) {
        this.context = context;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.credentialUtil = credentialUtil;
    }

    @BeforeEach
    public void setUp() {
        POS_MER_API_KEY = credentialUtil.getPOS_MER_API_KEY();
        Mockito.reset(mobiPay);
    }

    @AfterEach
    public void tearDown() {
    }

    @Nested
    @DisplayName("400 Bad Request")
    class BadRequest {

        @Test
        @DisplayName("자동차 번호가 없을 때")
        void test1() throws Exception {
            // given
            final String url = "/api/v1/merchants/food/menu-list";
            final String requestBody ="""
                    {
	                    "info" : "메뉴1, 메뉴2",
	                    "roomId" : 123
                    }
                    """;

            // when
            ResultActions resultActions = mockMvc.perform(post(url)
                .header("merApiKey", POS_MER_API_KEY)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("자동차 번호가 7보다 작을 때")
        void test2() throws Exception {
            // given
            final String url = "/api/v1/merchants/food/menu-list";
            final String requestBody ="""
                    {
                        "carNumber" : "123456",
                        "info" : "메뉴1, 메뉴2",
                        "roomId" : 123
                    }
                    """;

            // when
            ResultActions resultActions = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("자동차 번호가 8보다 클 때")
        void test3() throws Exception {
            // given
            final String url = "/api/v1/merchants/food/menu-list";
            final String requestBody ="""
                    {
                        "carNumber" : "123가45678",
                        "info" : "메뉴1, 메뉴2",
                        "roomId" : 123
                    }
                    """;

            // when
            ResultActions resultActions = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("roomId가 숫자가 아닐 때")
        void test4() throws Exception {
            // given
            final String url = "/api/v1/merchants/food/menu-list";
            final String requestBody ="""
                    {
                        "carNumber" : "123가4567",
                        "info" : "메뉴1, 메뉴2",
                        "roomId" : "일이삼"
                    }
                    """;

            // when
            ResultActions resultActions = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("info가 없을 때")
        void test5() throws Exception {
            // given
            final String url = "/api/v1/merchants/food/menu-list";
            final String requestBody ="""
                    {
                        "carNumber" : "123가4567",
                        "roomId" : 123
                    }
                    """;

            // when
            ResultActions resultActions = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("roomId가 없을 때")
        void test6() throws Exception {
            // given
            final String url = "/api/v1/merchants/food/menu-list";
            final String requestBody ="""
                    {
                        "carNumber" : "123가4567",
                        "info" : "메뉴1, 메뉴2"
                    }
                    """;

            // when
            ResultActions resultActions = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("merApiKey가 없을 때")
        void test7() throws Exception {
            // given
            final String url = "/api/v1/merchants/food/menu-list";
            final String requestBody ="""
                    {
                        "carNumber" : "123가4567",
                        "info" : "메뉴1, 메뉴2",
                        "roomId" : 123
                    }
                    """;

            // when
            ResultActions resultActions = mockMvc.perform(post(url)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("merchantType이 parking, oil, food, washing, motel, street이 아닌 경우")
        void unknown_merchantType() throws Exception {
            // given
            final String url = "/api/v1/merchants/emart/menu-list";
            final String requestBody ="""
                    {
                        "carNumber" : "123가4567",
                        "info" : "메뉴1, 메뉴2",
                        "roomId" : 123
                    }
                    """;

            // when
            ResultActions resultActions = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isBadRequest());
        }

    }

    //// 401 Unauthorized
    @Test
    @DisplayName("401 Unauthorized - merApiKey가 틀렸을 때")
    void fail_401_invalid_merApiKey() throws Exception {
        // given
        final String url = "/api/v1/merchants/food/menu-list";
        final String requestBody ="""
                {
                    "carNumber" : "123가4567",
                    "info" : "메뉴1, 메뉴2",
                    "roomId" : 123
                }
                """;

        // when
        ResultActions resultActions = mockMvc.perform(post(url)
                .header("merApiKey", "invalid_merApiKey")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    //// 404 Not Found
    @Test
    @DisplayName("404 Not Found - 등록되지 않은 자동차 번호")
        void fail_404_not_registered_carNumber() throws Exception {
            // given
           Mockito.when(mobiPay.sendMenuList(Mockito.any(), Mockito.any()))
                   .thenReturn(ResponseEntity.notFound().build());

            final String url = "/api/v1/merchants/food/menu-list";
            final String requestBody ="""
                    {
                        "carNumber" : "123가4567",
                        "info" : "메뉴1, 메뉴2",
                        "roomId" : 123
                    }
                    """;

            // when
            ResultActions resultActions = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            resultActions.andExpect(status().isNotFound());
        }

    @Test
    @DisplayName("500 Internal Server Error - MobiPay 서버 500 오류")
    void fail_500_mobiPay_server_error() throws Exception {
        // given
        Mockito.when(mobiPay.sendMenuList(Mockito.any(), Mockito.any()))
                .thenReturn(ResponseEntity.status(500).build());

        final String url = "/api/v1/merchants/food/menu-list";
        final String requestBody ="""
                {
                    "carNumber" : "123가4567",
                    "info" : "메뉴1, 메뉴2",
                    "roomId" : 123
                }
                """;

        // when
        ResultActions resultActions = mockMvc.perform(post(url)
                .header("merApiKey", POS_MER_API_KEY)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("200 OK")
    void success_200() throws Exception {
        // given
        Mockito.when(mobiPay.sendMenuList(Mockito.any(), Mockito.any()))
                .thenReturn(ResponseEntity.ok().build());

        final String url = "/api/v1/merchants/food/menu-list";
        final String requestBody ="""
                {
                    "carNumber" : "123가4567",
                    "info" : "메뉴1, 메뉴2",
                    "roomId" : 123
                }
                """;

        // when
        ResultActions resultActions = mockMvc.perform(post(url)
                .header("merApiKey", POS_MER_API_KEY)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
    }

}
