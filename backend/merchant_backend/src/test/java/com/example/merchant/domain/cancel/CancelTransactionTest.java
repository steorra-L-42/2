package com.example.merchant.domain.cancel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.merchant.MerchantApplication;
import com.example.merchant.domain.cancel.dto.CancelTransactionResponse;
import com.example.merchant.util.credential.CredentialUtil;
import com.example.merchant.util.mobipay.MobiPay;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ContextConfiguration(classes = MerchantApplication.class)
@AutoConfigureMockMvc
public class CancelTransactionTest {

    private static final Logger log = LoggerFactory.getLogger(CancelTransactionTest.class);

    private final WebApplicationContext context;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final CredentialUtil credentialUtil;
    private String POS_MER_API_KEY;

    @MockBean
    private MobiPay mobiPay;

    @Autowired
    public CancelTransactionTest(WebApplicationContext context, MockMvc mockMvc, ObjectMapper objectMapper, CredentialUtil credentialUtil) {
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

    // 실패: 400 Bad Request
    @Nested
    @DisplayName("실패: 400 Bad Request")
    class Fail400 {
        @Test
        @DisplayName("merchantType이 null인 경우")
        void merchantType_Null() throws Exception {
            // given
            final String url = "/api/v1/merchants/null/cancelled-transactions/1";
            // when
            ResultActions result = mockMvc.perform(patch(url)
                    .header("merApiKey", POS_MER_API_KEY));
            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("merchantType이 parking, oil, food, washing, motel, street이 아닌 경우")
        void unknown_merchantType() throws Exception {
            // given
            final String url = "/api/v1/merchants/unknown/cancelled-transactions/1";
            // when
            ResultActions result = mockMvc.perform(patch(url)
                    .header("merApiKey", POS_MER_API_KEY));
            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("posMerApiKey가 null인 경우")
        void posMerApiKey_Null() throws Exception {
            // given
            final String url = "/api/v1/merchants/parking/cancelled-transactions/1";
            // when
            ResultActions result = mockMvc.perform(patch(url));
            // then
            result.andExpect(status().isBadRequest());
        }
    }

    // 실패: 404 Not Found
    @Nested
    @DisplayName("실패: 404 Not Found")
    class Fail404 {
        @Test
        @DisplayName("merchantType이 empty인 경우")
        void merchantType_Empty() throws Exception {
            // given
            final String url = "/api/v1/merchants//cancelled-transactions/1";
            // when
            ResultActions result = mockMvc.perform(patch(url)
                    .header("merApiKey", POS_MER_API_KEY));
            // then
            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("transactionUniqueNo가 없는 경우")
        void transactionUniqueNo_Null() throws Exception {
            // given
            final String url = "/api/v1/merchants/parking/cancelled-transactions/";
            // when
            ResultActions result = mockMvc.perform(patch(url)
                    .header("merApiKey", POS_MER_API_KEY));
            // then
            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("transactionUniqueNo가 존재하지 않는 경우")
        void transactionUniqueNo_NotFound() throws Exception {
            // given
            Mockito.when(mobiPay.cancelTransaction(Mockito.any(), Mockito.any(), Mockito.any()))
                    .thenReturn(ResponseEntity.notFound().build());

            final String url = "/api/v1/merchants/parking/cancelled-transactions/999";
            // when
            ResultActions result = mockMvc.perform(patch(url)
                    .header("merApiKey", POS_MER_API_KEY));
            // then
            result.andExpect(status().isNotFound());
        }
    }

    @Test
    @DisplayName("실패: 401 Unauthorized : posMerApiKey가 POS_MER_API_KEY와 일치하지 않는 경우")
    void posMerApiKey_NotMatch() throws Exception {
        // given
        final String url = "/api/v1/merchants/parking/cancelled-transactions/1";
        // when
        ResultActions result = mockMvc.perform(patch(url)
                .header("merApiKey", "INVALID_API_KEY"));
        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("실패: 403 Forbidden : 해당 가맹점의 거래내역이 아닌 경우")
    void not_this_merchant_transaction() throws Exception {
        // given
        Mockito.when(mobiPay.cancelTransaction(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(ResponseEntity.status(403).build());
        final String url = "/api/v1/merchants/parking/cancelled-transactions/1";
        // when
        ResultActions result = mockMvc.perform(patch(url)
                .header("merApiKey", POS_MER_API_KEY));
        // then
        result.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("실패: 500 Internal Server Error : mobipay 서버 내부 오류")
    void mobipay_server_error() throws Exception {
        // given
        Mockito.when(mobiPay.cancelTransaction(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(ResponseEntity.status(500).build());
        final String url = "/api/v1/merchants/parking/cancelled-transactions/1";
        // when
        ResultActions result = mockMvc.perform(patch(url)
                .header("merApiKey", POS_MER_API_KEY));
        // then
        result.andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("성공: 200 OK")
    void success_200() throws Exception {
        // given
        CancelTransactionResponse response = new CancelTransactionResponse(true, "결제 성공");
        Mockito.when(mobiPay.cancelTransaction(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(ResponseEntity.ok(response));
        final String url = "/api/v1/merchants/parking/cancelled-transactions/1";
        // when
        ResultActions result = mockMvc.perform(patch(url)
                .header("merApiKey", POS_MER_API_KEY));
        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("결제 성공"));
    }
}
