package com.example.merchant.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.merchant.MerchantApplication;
import com.example.merchant.domain.payment.dto.PaymentRequest;
import com.example.merchant.domain.payment.dto.PaymentResponse;
import com.example.merchant.global.enums.MerchantType;
import com.example.merchant.util.credential.CredentialUtil;
import com.example.merchant.util.mobipay.MobiPay;
import com.example.merchant.util.mobipay.MobiPayImpl;
import com.example.merchant.util.mobipay.dto.MobiPaymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ContextConfiguration(classes = MerchantApplication.class)
@AutoConfigureMockMvc
public class PaymentRequestTest {

   private static final Logger log = LoggerFactory.getLogger(PaymentRequestTest.class);

    private final WebApplicationContext context;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final CredentialUtil credentialUtil;
    private String POS_MER_API_KEY;

    @MockBean
    private MobiPay mobiPay;

    @Autowired
    public PaymentRequestTest(WebApplicationContext context, MockMvc mockMvc, ObjectMapper objectMapper, CredentialUtil credentialUtil) {
        this.context = context;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.credentialUtil = credentialUtil;
    }

    @BeforeEach
    public void setUp() {
        POS_MER_API_KEY = credentialUtil.getPOS_MER_API_KEY();
    }

    @AfterEach
    public void tearDown() {
    }

    @Nested
    @DisplayName("실패 : 400 Bad Request : type error")
    class fail_400_type_error {

        @Test
        @DisplayName("type이 비어있는 경우")
        void fail_400_type_empty() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":"",
                        "paymentBalance":50000,
                        "carNumber":"123가4567",
                        "info":"info"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("type이 null인 경우")
        void fail_400_type_null() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":null,
                        "paymentBalance":50000,
                        "carNumber":"123가4567",
                        "info":"info"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("등록되지 않은 type")
        void fail_400_type_not_registered() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":"EMART",
                        "paymentBalance":50000,
                        "carNumber":"123가4567",
                        "info":"info"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("실패 : 400 Bad Request : paymentBalance error")
    class fail_400_paymentBalance_error {

        @Test
        @DisplayName("paymentBalance가 비어있는 경우")
        void fail_400_paymentBalance_empty() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":"OIL",
                        "paymentBalance":,
                        "carNumber":"123가4567",
                        "info":"info"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("paymentBalance가 null인 경우")
        void fail_400_paymentBalance_null() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":"OIL",
                        "paymentBalance":null,
                        "carNumber":"123가4567",
                        "info":"info"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("paymentBalance가 0인 경우")
        void fail_400_paymentBalance_zero() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":"OIL",
                        "paymentBalance":0,
                        "carNumber":"123가4567",
                        "info":"info"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("paymentBalance가 음수인 경우")
        void fail_400_paymentBalance_negative() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":"OIL",
                        "paymentBalance":-50000,
                        "carNumber":"123가4567",
                        "info":"info"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("paymentBalance가 숫자가 아닌 문자인 경우")
        void fail_400_paymentBalance_not_number() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":"OIL",
                        "paymentBalance":"오만원",
                        "carNumber":"123가4567",
                        "info":"info"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("실패 : 400 Bad Request : cardNumber error")
    class fail_400_cardNumber_error {
        @Test
        @DisplayName("cardNumber가 비어있는 경우")
        void fail_400_cardNumber_empty() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":"OIL",
                        "paymentBalance":50000,
                        "carNumber":"",
                        "info":"info"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("cardNumber가 null인 경우")
        void fail_400_cardNumber_null() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":"OIL",
                        "paymentBalance":50000,
                        "carNumber":null,
                        "info":"info"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("cardNumber가 8자를 초과하는 경우")
        void fail_400_cardNumber_exceeds_8() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":"OIL",
                        "paymentBalance":50000,
                        "carNumber":"123가45678",
                        "info":"info"
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("실패 : 400 Bad Request : info error")
    class fail_400_info_error {
        @Test
        @DisplayName("info가 없는 경우")
        void fail_400_info_empty() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":"OIL",
                        "paymentBalance":50000,
                        "carNumber":"123가4567"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("info가 null인 경우")
        void fail_400_info_null() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":"OIL",
                        "paymentBalance":50000,
                        "carNumber":"123가4567",
                        "info":null
                    }
                    """;
            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", POS_MER_API_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("실패 : 401 Uauthorized : merApiKey error")
    class fail_401_merApiKey_error {

        @Test
        @DisplayName("merApiKey가 올바르지 않은 경우")
        void fail_401_merApiKey_invalid() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":"OIL",
                        "paymentBalance":50000,
                        "carNumber":"123가4567",
                        "info":"info"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", "hahahahahahaha")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("merApiKey가 빈 문자열인 경우")
        void fail_401_merApiKey_empty() throws Exception {
            // given
            final String url = "/api/v1/merchants/payments/request";
            final String requestBody = """
                    {
                        "type":"OIL",
                        "paymentBalance":50000,
                        "carNumber":"123가4567",
                        "info":"info"
                    }
                    """;

            // when
            ResultActions result = mockMvc.perform(post(url)
                    .header("merApiKey", "")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody));

            // then
            result.andExpect(status().isUnauthorized());
        }
    }

    // 실패: 404 Not Found : 존재하지 않는 cardNumber
    @Test
    @DisplayName("실패 : 404 Not Found : 존재하지 않는 cardNumber")
    void fail_404_CarNumber_Not_Found() throws Exception {
        // given
        Mockito.when(mobiPay.request(any(), any())).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        final String url = "/api/v1/merchants/payments/request";
        final String requestBody = """
                {
                    "type":"OIL",
                    "paymentBalance":50000,
                    "carNumber":"123가4567",
                    "info":"info"
                }
                """;

        // when
        ResultActions result = mockMvc.perform(post(url)
                .header("merApiKey", POS_MER_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isNotFound());
    }

    // 성공: 200 OK : 모든 필드가 올바른 경우
    @Test
    @DisplayName("성공 : 200 OK : 모든 필드가 올바른 경우")
    void success_200_OK() throws Exception {
        // given
        final Long merchantId = credentialUtil.getMerchantIdByType(MerchantType.OIL);
        final PaymentRequest paymentRequest = new PaymentRequest(MerchantType.OIL,
                50000,
                "123가4567",
                "info");

        MobiPaymentResponse mobiPaymentResponse = new MobiPaymentResponse(
                1L, 1L, merchantId, 50000);
        Mockito.when(mobiPay.request(any(), any())).thenReturn(ResponseEntity.ok(mobiPaymentResponse));

        final String url = "/api/v1/merchants/payments/request";
        final String requestBody = objectMapper.writeValueAsString(paymentRequest);
        // when
        ResultActions result = mockMvc.perform(post(url)
                .header("merApiKey", POS_MER_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
        // then
        result.andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    String contentAsString = mvcResult.getResponse().getContentAsString();
                    PaymentResponse response = objectMapper.readValue(contentAsString, PaymentResponse.class);
                    assertThat(response.getApprovalWaitingId()).isEqualTo(1L);
                    assertThat(response.getCarId()).isEqualTo(1L);
                    assertThat(response.getMerchantId()).isEqualTo(merchantId);
                    assertThat(response.getPaymentBalance()).isEqualTo(50000);
                });

    }

}
