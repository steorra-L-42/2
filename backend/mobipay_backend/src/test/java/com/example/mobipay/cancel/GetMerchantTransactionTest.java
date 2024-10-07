package com.example.mobipay.cancel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobipay.MobiPayApplication;
import com.example.mobipay.domain.cancel.dto.MerchantTransactionResponse;
import com.example.mobipay.domain.merchant.entity.Merchant;
import com.example.mobipay.domain.merchant.error.MerchantNotFoundException;
import com.example.mobipay.domain.merchant.repository.MerchantRepository;
import com.example.mobipay.domain.merchanttransaction.entity.MerchantTransaction;
import com.example.mobipay.domain.merchanttransaction.repository.MerchantTransactionRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.ownedcard.repository.OwnedCardRepository;
import com.example.mobipay.domain.postpayments.dto.ApprovalPaymentRequest;
import com.example.mobipay.domain.postpayments.dto.cardtransaction.CardTransactionResponse;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import com.example.mobipay.domain.registeredcard.repository.RegisteredCardRepository;
import com.example.mobipay.domain.setupdomain.account.entity.Account;
import com.example.mobipay.domain.setupdomain.account.repository.AccountRepository;
import com.example.mobipay.domain.setupdomain.card.entity.CardProduct;
import com.example.mobipay.domain.setupdomain.card.repository.CardProductRepository;
import com.example.mobipay.global.authentication.dto.AccountRec;
import com.example.mobipay.global.authentication.dto.CardRec;
import com.example.mobipay.util.RestClientUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ContextConfiguration(classes = MobiPayApplication.class)
@AutoConfigureMockMvc
public class GetMerchantTransactionTest {

    private static final Logger log = LoggerFactory.getLogger(GetMerchantTransactionTest.class);
    private static final Long STARBUCkS_ID = 1911L;

    private final WebApplicationContext context;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final MerchantRepository merchantRepository;
    private final MerchantTransactionRepository merchantTransactionRepository;
    private final MobiUserRepository mobiUserRepository;
    private final OwnedCardRepository ownedCardRepository;
    private final RegisteredCardRepository registeredCardRepository;
    private final AccountRepository accountRepository;
    private final CardProductRepository cardProductRepository;

    @Autowired
    public GetMerchantTransactionTest(WebApplicationContext context, MockMvc mockMvc,
                                      ObjectMapper objectMapper, MerchantRepository merchantRepository,
                                      MerchantTransactionRepository merchantTransactionRepository,
                                      MobiUserRepository mobiUserRepository, OwnedCardRepository ownedCardRepository,
                                      RegisteredCardRepository registeredCardRepository, AccountRepository accountRepository,
                                      CardProductRepository cardProductRepository) {
        this.context = context;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.merchantRepository = merchantRepository;
        this.merchantTransactionRepository = merchantTransactionRepository;
        this.mobiUserRepository = mobiUserRepository;
        this.ownedCardRepository = ownedCardRepository;
        this.registeredCardRepository = registeredCardRepository;
        this.accountRepository = accountRepository;
        this.cardProductRepository = cardProductRepository;
    }

    @MockBean
    RestClientUtil restClientUtil;

    @BeforeEach
    public void setUp() {
        merchantTransactionRepository.deleteAll();
        registeredCardRepository.deleteAll();
        ownedCardRepository.deleteAll();
        mobiUserRepository.deleteAll();
        Mockito.reset(restClientUtil);
    }

    @AfterEach
    public void tearDown() {
        merchantTransactionRepository.deleteAll();
        registeredCardRepository.deleteAll();
        ownedCardRepository.deleteAll();
        mobiUserRepository.deleteAll();
        Mockito.reset(restClientUtil);
    }

    @Nested
    @DisplayName("실패: 400 Bad Request")
    class Fail400 {
        @Test
        @DisplayName("merchantId가 숫자가 아닐 때")
        void Fail400_invalidMerchantId() throws Exception {
            // given
            final String mobiApiKey = merchantRepository.findById(STARBUCkS_ID)
                    .orElseThrow(MerchantNotFoundException::new).getApiKey();
            final String url = "/api/v1/merchants/abc/transactions";
            // when
            ResultActions result = mockMvc.perform(get(url)
                    .header("mobiApiKey", mobiApiKey));
            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("MobiApiKey가 없을 때")
        void Fail400_noMobiApiKey() throws Exception {
            // given
            final String url = "/api/v1/merchants/" + STARBUCkS_ID + "/transactions";
            // when
            ResultActions result = mockMvc.perform(get(url));

            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패: 400 Bad Request : merchantId에 해당하는 MobiApiKey가 아닐 때")
        void Fail400_invalidMobiApiKey() throws Exception {
            // given
            final String url = "/api/v1/merchants/" + STARBUCkS_ID + "/transactions";

            // when
            ResultActions result = mockMvc.perform(get(url)
                    .header("mobiApiKey", "invalidMobiApiKey"));

            // then
            result.andExpect(status().isBadRequest());
        }
    }


    @Nested
    @DisplayName("실패: 404 Not Found")
    class Fail404 {

        @Test
        @DisplayName("merchantId가 존재하지 않을 때")
        void Fail404_invalidMerchantId() throws Exception {
            // given
            final String mobiApiKey = merchantRepository.findById(STARBUCkS_ID)
                    .orElseThrow(MerchantNotFoundException::new).getApiKey();
            // when
            ResultActions result = mockMvc.perform(get("/api/v1/merchants/9999/transactions")
                    .header("mobiApiKey", mobiApiKey));
            // then
            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("merchantId가 존재하지 않을 때")
        void Fail404_noMerchantId() throws Exception {
            // given
            final String mobiApiKey = merchantRepository.findById(STARBUCkS_ID)
                    .orElseThrow(MerchantNotFoundException::new).getApiKey();
            final String url = "/api/v1/merchants//transactions";
            // when
            ResultActions result = mockMvc.perform(get(url)
                    .header("mobiApiKey", mobiApiKey));
            // then
            result.andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("성공")
    class Success {

        @Transactional
        @Test
        @DisplayName("성공: 200 Ok : 거래내역 조회 성공")
        void Success200() throws Exception {
            // given

            // 총 5개의 거래내역 중 해당하는 가맹점은 3개의 거래내역이 있는 경우
            // 1. mobiUser 생성
            MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
            mobiUser =mobiUserRepository.save(mobiUser);

           // 2. 계좌 생성
            AccountRec accountRec = mock(AccountRec.class);
            when(accountRec.getBankCode()).thenReturn("001");
            when(accountRec.getAccountNo()).thenReturn("12345678901234");

            Account account = Account.of(accountRec);
            account = accountRepository.save(account);

            // 3. 카드 생성
            CardRec cardRec = mock(CardRec.class);
            when(cardRec.getCardNo()).thenReturn("1234567890123456");
            when(cardRec.getCvc()).thenReturn("123");
            when(cardRec.getWithdrawalDate()).thenReturn("1234567890");
            when(cardRec.getCardExpiryDate()).thenReturn("20251231");

            CardProduct cardProduct = cardProductRepository.findByCardUniqueNo("1001-664f125022bf433").get();

            OwnedCard ownedCard = OwnedCard.of(cardRec);
            ownedCard.addRelation(mobiUser, account, cardProduct);
            ownedCardRepository.save(ownedCard);

            // 4. 카드 등록
            RegisteredCard registeredCard = RegisteredCard.from(1000000);
            registeredCard.addRelations(mobiUser, ownedCard);
            registeredCardRepository.save(registeredCard);

            // 5. 거래내용 생성
            createMerchantTransaction(registeredCard, STARBUCkS_ID, 1L, "20240101", "123456", 1000L, "info1");
            createMerchantTransaction(registeredCard, STARBUCkS_ID,2L, "20240102", "123457", 2000L, "info2");
            createMerchantTransaction(registeredCard, 1906L,3L, "20240102", "123458", 3000L, "info3");
            createMerchantTransaction(registeredCard, STARBUCkS_ID,4L, "20240103", "123459", 4000L, "info4");
            createMerchantTransaction(registeredCard, 1906L,5L, "20240204", "123460", 5000L, "info5");

            final String mobiApiKey = merchantRepository.findById(STARBUCkS_ID)
                    .orElseThrow(MerchantNotFoundException::new).getApiKey();
            final String url = "/api/v1/merchants/" + STARBUCkS_ID + "/transactions";

            // when
            ResultActions result = mockMvc.perform(get(url)
                    .header("mobiApiKey", mobiApiKey));

            // then
            result.andExpect(status().isOk())
                    .andDo(mvcResult -> {
                        String contentAsString = mvcResult.getResponse().getContentAsString();
                        MerchantTransactionResponse merchantTransactionResponse = objectMapper.readValue(contentAsString, MerchantTransactionResponse.class);
                        assertThat(merchantTransactionResponse).isNotNull();
                        assertThat(merchantTransactionResponse.getItems().size()).isEqualTo(3);
                        assertThat(merchantTransactionResponse.getItems().get(0).getTransactionUniqueNo()).isEqualTo(4L);
                        assertThat(merchantTransactionResponse.getItems().get(1).getTransactionUniqueNo()).isEqualTo(2L);
                        assertThat(merchantTransactionResponse.getItems().get(2).getTransactionUniqueNo()).isEqualTo(1L);
                    });
        }

        @Test
        @DisplayName("성공: 204 No Content : 거래내역 없음")
        void Success204() throws Exception {
            // given
            final String mobiApiKey = merchantRepository.findById(STARBUCkS_ID)
                    .orElseThrow(MerchantNotFoundException::new).getApiKey();
            final String url = "/api/v1/merchants/" + STARBUCkS_ID + "/transactions";

            // when
            ResultActions result = mockMvc.perform(get(url)
                    .header("mobiApiKey", mobiApiKey));

            // then
            result.andExpect(status().isNoContent());
        }
    }

    private void createMerchantTransaction(RegisteredCard registeredCard, Long merchantId, Long transactionUniqueNo, String transactionDate, String transactionTime, Long paymentBalance, String info) {

        ApprovalPaymentRequest request = mock(ApprovalPaymentRequest.class);
        when(request.getInfo()).thenReturn(info);

        ResponseEntity<CardTransactionResponse> response = mock(ResponseEntity.class);
        CardTransactionResponse cardTransactionResponse = mock(CardTransactionResponse.class);
        CardTransactionResponse.Rec rec = mock(CardTransactionResponse.Rec.class);

        when(response.getBody()).thenReturn(cardTransactionResponse);
        when(cardTransactionResponse.getRec()).thenReturn(rec);
        when(rec.getTransactionUniqueNo()).thenReturn(transactionUniqueNo);
        when(rec.getTransactionDate()).thenReturn(transactionDate);
        when(rec.getTransactionTime()).thenReturn(transactionTime);
        when(rec.getPaymentBalance()).thenReturn(paymentBalance);

        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(MerchantNotFoundException::new);

        MerchantTransaction merchantTransaction = MerchantTransaction.of(request, response);
        merchantTransaction.addRelations(registeredCard, merchant);

        merchantTransactionRepository.save(merchantTransaction);
    }

}
