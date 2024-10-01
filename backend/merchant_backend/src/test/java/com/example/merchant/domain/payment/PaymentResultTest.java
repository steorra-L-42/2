package com.example.merchant.domain.payment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.merchant.MerchantApplication;
import com.example.merchant.util.credential.CredentialUtil;
import com.example.merchant.util.pos.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ContextConfiguration(classes = MerchantApplication.class)
@AutoConfigureMockMvc
    public class PaymentResultTest {

    private static final Logger log = LoggerFactory.getLogger(PaymentRequestTest.class);

    private final WebApplicationContext context;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final CredentialUtil credentialUtil;
    private String MOBI_MER_API_KEY;

    @MockBean
    private final WebSocketHandler webSocketHandler;

    @Autowired
    public PaymentResultTest(WebApplicationContext context, MockMvc mockMvc,
                             ObjectMapper objectMapper, CredentialUtil credentialUtil,
                             WebSocketHandler webSocketHandler) {
        this.context = context;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.credentialUtil = credentialUtil;
        this.webSocketHandler = webSocketHandler;
    }

    @BeforeEach
    public void setUp() {
        MOBI_MER_API_KEY = credentialUtil.getMOBI_MER_API_KEY();
    }

    @Nested
    @DisplayName("실패: 400 Bad Request")
    class fail400_BadRequest {

        @DisplayName("success가 null인 경우")
        @org.junit.jupiter.api.Test
        void fail400_BadRequest_success_null() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/result";
            final String requestBody = """
                    {
                        "success": null,
                        "merchantId": "1911",
                        "paymentBalance": 5000,
                        "info": "A hot americano with a piece of pineapple"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", MOBI_MER_API_KEY)
                    .contentType("application/json")
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("success가 true나 false가 아닌 경우")
        @org.junit.jupiter.api.Test
        void fail400_BadRequest_success_invalid() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/result";
            final String requestBody = """
                    {
                        "success": "success",
                        "merchantId": "1911",
                        "paymentBalance": 5000,
                        "info": "A hot americano with a piece of pineapple"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", MOBI_MER_API_KEY)
                    .contentType("application/json")
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("merchantId null인 경우")
        @org.junit.jupiter.api.Test
        void fail400_BadRequest_merchantId_null() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/result";
            final String requestBody = """
                    {
                        "success": true,
                        "merchantId": null,
                        "paymentBalance": 5000,
                        "info": "A hot americano with a piece of pineapple"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", MOBI_MER_API_KEY)
                    .contentType("application/json")
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("존재하지 않는 merchantId인 경우")
        @org.junit.jupiter.api.Test
        void fail400_BadRequest_merchantId_notNumber() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/result";
            final String requestBody = """
                    {
                        "success": true,
                        "merchantId": "천구백십일",
                        "paymentBalance": 5000,
                        "info": "A hot americano with a piece of pineapple"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", MOBI_MER_API_KEY)
                    .contentType("application/json")
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("존재하지 않는 merchantId인 경우")
        @org.junit.jupiter.api.Test
        void fail400_BadRequest_merchantId_invalid() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/result";
            final String requestBody = """
                    {
                        "success": true,
                        "merchantId": "9999",
                        "paymentBalance": 5000,
                        "info": "A hot americano with a piece of pineapple"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", MOBI_MER_API_KEY)
                    .contentType("application/json")
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("paymentBalance가 null인 경우")
        @org.junit.jupiter.api.Test
        void fail400_BadRequest_paymentBalance_null() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/result";
            final String requestBody = """
                    {
                        "success": true,
                        "merchantId": "1911",
                        "paymentBalance": null,
                        "info": "A hot americano with a piece of pineapple"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", MOBI_MER_API_KEY)
                    .contentType("application/json")
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("paymentBalance가 0인 경우")
        @org.junit.jupiter.api.Test
        void fail400_BadRequest_paymentBalance_zero() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/result";
            final String requestBody = """
                    {
                        "success": true,
                        "merchantId": "1911",
                        "paymentBalance": 0,
                        "info": "A hot americano with a piece of pineapple"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", MOBI_MER_API_KEY)
                    .contentType("application/json")
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("paymentBalance가 음수인 경우")
        @org.junit.jupiter.api.Test
        void fail400_BadRequest_paymentBalance_negative() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/result";
            final String requestBody = """
                    {
                        "success": true,
                        "merchantId": "1911",
                        "paymentBalance": -5000,
                        "info": "A hot americano with a piece of pineapple"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", MOBI_MER_API_KEY)
                    .contentType("application/json")
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("paymentBalance가 숫자가 아닌 경우")
        @org.junit.jupiter.api.Test
        void fail400_BadRequest_paymentBalance_invalid() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/result";
            final String requestBody = """
                    {
                        "success": true,
                        "merchantId": "1911",
                        "paymentBalance": "오천원",
                        "info": "A hot americano with a piece of pineapple"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", MOBI_MER_API_KEY)
                    .contentType("application/json")
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("info가 null인 경우")
        @org.junit.jupiter.api.Test
        void fail400_BadRequest_info_null() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/result";
            final String requestBody = """
                    {
                        "success": true,
                        "merchantId": "1911",
                        "paymentBalance": 5000,
                        "info": null
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", MOBI_MER_API_KEY)
                    .contentType("application/json")
                    .content(requestBody));
            // then
            result.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("실패: 401 Unauthorized")
    class  fail401_Unauthorized {

        @DisplayName("잘못된 MOBI_MER_API_KEY인 경우")
        @org.junit.jupiter.api.Test
        void fail401_Unauthorized_invalid_MOBI_MER_API_KEY() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/result";
            final String requestBody = """
                    {
                        "success": true,
                        "merchantId": "1911",
                        "paymentBalance": 5000,
                        "info": "A hot americano with a piece of pineapple"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", "INVALID_MOBI_MER_API_KEY")
                    .contentType("application/json")
                    .content(requestBody));
            // then
            result.andExpect(status().isUnauthorized());
        }

        @DisplayName("비어있는 MOBI_MER_API_KEY인 경우")
        @org.junit.jupiter.api.Test
        void fail401_Unauthorized_empty_MOBI_MER_API_KEY() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/result";
            final String requestBody = """
                    {
                        "success": true,
                        "merchantId": "1911",
                        "paymentBalance": 5000,
                        "info": "A hot americano with a piece of pineapple"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", "")
                    .contentType("application/json")
                    .content(requestBody));
            // then
            result.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("성공: 200 OK")
    class success200_OK {

        @DisplayName("결제 성공")
        @org.junit.jupiter.api.Test
        void success200_OK_payment_success() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/result";
            final String requestBody = """
                    {
                        "success": true,
                        "merchantId": "1911",
                        "paymentBalance": 5000,
                        "info": "A hot americano with a piece of pineapple"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", MOBI_MER_API_KEY)
                    .contentType("application/json")
                    .content(requestBody));
            // then
            result.andExpect(status().isOk());
        }

        @DisplayName("결제 실패")
        @org.junit.jupiter.api.Test
        void success200_OK_payment_fail() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/result";
            final String requestBody = """
                    {
                        "success": false,
                        "merchantId": "1911",
                        "paymentBalance": 5000,
                        "info": "A hot americano with a piece of pineapple"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", MOBI_MER_API_KEY)
                    .contentType("application/json")
                    .content(requestBody));
            // then
            result.andExpect(status().isOk());
        }
    }
}
