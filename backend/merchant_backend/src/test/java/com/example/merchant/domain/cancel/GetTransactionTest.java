package com.example.merchant.domain.cancel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.merchant.MerchantApplication;
import com.example.merchant.domain.cancel.dto.MerchantTransaction;
import com.example.merchant.domain.cancel.dto.MerchantTransactionResponse;
import com.example.merchant.util.credential.CredentialUtil;
import com.example.merchant.util.mobipay.MobiPay;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.util.List;
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
public class GetTransactionTest {

    private static final Logger log = LoggerFactory.getLogger(GetTransactionTest.class);

    private final WebApplicationContext context;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final CredentialUtil credentialUtil;
    private String POS_MER_API_KEY;

    @MockBean
    private MobiPay mobiPay;

    @Autowired
    public GetTransactionTest(WebApplicationContext context, MockMvc mockMvc, ObjectMapper objectMapper, CredentialUtil credentialUtil) {
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
    class fail_400 {
        @Test
        @DisplayName("merchantType이 null인 경우")
        void merchantType_null() throws Exception {
            //given
            String url = "/api/v1/merchants/null/transactions";
            //when
            ResultActions result = mockMvc.perform(get(url)
                    .header("merApiKey", POS_MER_API_KEY));
            //then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("merchantType이 'parking', 'oil', food', 'washing, 'motel', 'street' 중 하나가 아닌 경우")
        void merchantType_invalid() throws Exception {
            //given
            String url = "/api/v1/merchants/invalid/transactions";
            //when
            ResultActions result = mockMvc.perform(get(url)
                    .header("merApiKey", POS_MER_API_KEY));
            //then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POS_MER_API_KEY가 null인 경우")
        void posMerApiKey_null() throws Exception {
            //given
            String url = "/api/v1/merchants/food/transactions";
            //when
            ResultActions result = mockMvc.perform(get(url));
            //then
            result.andExpect(status().isBadRequest());
        }

    }

    @Test
    @DisplayName("실패: 401 Unauthorized : POS_MER_API_KEY가 올바르지 않은 경우")
    void fail_401_posMerApiKey_invalid() throws Exception {
        //given
        String url = "/api/v1/merchants/food/transactions";
        //when
        ResultActions result = mockMvc.perform(get(url)
                .header("merApiKey", "invalid"));
        //then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("실패: 404 Not Found : merchantType이 empty인 경우")
    void fail_404_merchantType_empty() throws Exception {
        //given
        String url = "/api/v1/merchants//transactions";
        //when
        ResultActions result = mockMvc.perform(get(url)
                .header("merApiKey", ""));
        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("실패: 500 Internal Server Error : MobiPay 서버 내부 오류가 발생한 경우")
    void fail_500_mobiPay_server_error() throws Exception {
        //given
        Mockito.when(mobiPay.getTransactionList(Mockito.any(), Mockito.any()))
                .thenReturn(ResponseEntity.status(500).build());
        String url = "/api/v1/merchants/food/transactions";
        //when
        ResultActions result = mockMvc.perform(get(url)
                .header("merApiKey", POS_MER_API_KEY));
        //then
        result.andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("성공: 200 OK : merchantType이 'food'인 경우")
    void success_200() throws Exception {
        //given
        List<MerchantTransaction> transactions = List.of(
                new MerchantTransaction(1L, "20241003", "055300", 10000L, "food1", false, credentialUtil.getFOOD_MER_ID(), 1L, 11L),
                new MerchantTransaction(2L, "20241004", "055300", 10000L, "food1", false, credentialUtil.getFOOD_MER_ID(), 2L, 22L),
                new MerchantTransaction(3L, "20241005", "055300", 10000L, "food1", false, credentialUtil.getFOOD_MER_ID(), 3L, 33L),
                new MerchantTransaction(4L, "20241005", "055333", 10000L, "food1", false, credentialUtil.getFOOD_MER_ID(), 4L, 44L)
        );
        MerchantTransactionResponse response = new MerchantTransactionResponse(transactions);
        ResponseEntity<MerchantTransactionResponse> responseEntity = ResponseEntity.ok(response);

        Mockito.when(mobiPay.getTransactionList(Mockito.any(), Mockito.any()))
                .thenReturn(responseEntity);

        String url = "/api/v1/merchants/food/transactions";
        //when
        ResultActions result = mockMvc.perform(get(url)
                .header("merApiKey", POS_MER_API_KEY));
        //then
        result.andExpect(status().isOk());
        result.andExpect(mvcResult -> {
           mvcResult.getResponse().getContentAsString();
              List<MerchantTransaction> actual = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.items[*]");
              assertThat(actual.size()).isEqualTo(transactions.size());
        });
    }

    @Test
    @DisplayName("성공 : 204 No Content")
    void success_204() throws Exception {
        //given
        ResponseEntity<MerchantTransactionResponse> responseEntity = ResponseEntity.noContent().build();

        Mockito.when(mobiPay.getTransactionList(Mockito.any(), Mockito.any()))
                .thenReturn(responseEntity);

        String url = "/api/v1/merchants/food/transactions";
        //when
        ResultActions result = mockMvc.perform(get(url)
                .header("merApiKey", POS_MER_API_KEY));
        //then
        result.andExpect(status().isNoContent());
    }
}
