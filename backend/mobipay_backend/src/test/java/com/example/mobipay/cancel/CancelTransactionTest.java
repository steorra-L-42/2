package com.example.mobipay.cancel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobipay.MobiPayApplication;
import com.example.mobipay.domain.fcmtoken.entity.FcmToken;
import com.example.mobipay.domain.fcmtoken.error.FCMException;
import com.example.mobipay.domain.fcmtoken.repository.FcmTokenRepository;
import com.example.mobipay.domain.fcmtoken.service.FcmService;
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
import com.example.mobipay.domain.ssafyuser.entity.SsafyUser;
import com.example.mobipay.domain.ssafyuser.repository.SsafyUserRepository;
import com.example.mobipay.global.authentication.dto.AccountRec;
import com.example.mobipay.global.authentication.dto.CardRec;
import com.example.mobipay.global.authentication.dto.SsafyUserResponse;
import com.example.mobipay.util.RestClientUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = MobiPayApplication.class)
public class CancelTransactionTest {

    private static final Logger log = LoggerFactory.getLogger(CancelTransactionTest.class);
    private static final Long STARBUCkS_ID = 1911L;

    private final WebApplicationContext context;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final MerchantRepository merchantRepository;
    private final MerchantTransactionRepository merchantTransactionRepository;
    private final MobiUserRepository mobiUserRepository;
    private final SsafyUserRepository ssafyUserRepository;
    private final AccountRepository accountRepository;
    private final CardProductRepository cardProductRepository;
    private final OwnedCardRepository ownedCardRepository;
    private final RegisteredCardRepository registeredCardRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final EntityManager em;

    @MockBean
    private RestClientUtil restClientUtil;
    @MockBean
    private FcmService fcmServiceImpl;

    @Autowired
    public CancelTransactionTest(WebApplicationContext context, MockMvc mockMvc,
                                 ObjectMapper objectMapper, MerchantRepository merchantRepository,
                                 MerchantTransactionRepository merchantTransactionRepository,
                                 MobiUserRepository mobiUserRepository, AccountRepository accountRepository,
                                 CardProductRepository cardProductRepository, OwnedCardRepository ownedCardRepository,
                                 RegisteredCardRepository registeredCardRepository,
                                 SsafyUserRepository ssafyUserRepository, FcmTokenRepository fcmTokenRepository,
                                 EntityManager em) {
        this.context = context;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.cardProductRepository = cardProductRepository;
        this.merchantRepository = merchantRepository;
        this.mobiUserRepository = mobiUserRepository;
        this.accountRepository = accountRepository;
        this.ownedCardRepository = ownedCardRepository;
        this.registeredCardRepository = registeredCardRepository;
        this.merchantTransactionRepository = merchantTransactionRepository;
        this.fcmTokenRepository = fcmTokenRepository;
        this.ssafyUserRepository = ssafyUserRepository;
        this.em = em;
    }

    @BeforeEach
    void setUp() {
        merchantTransactionRepository.deleteAll();
        registeredCardRepository.deleteAll();
        ownedCardRepository.deleteAll();
        accountRepository.deleteAll();
        mobiUserRepository.deleteAll();
        fcmTokenRepository.deleteAll();
        ssafyUserRepository.deleteAll();
        Mockito.reset(fcmServiceImpl);
        Mockito.reset(restClientUtil);
    }

    @AfterEach
    void tearDown() {
        merchantTransactionRepository.deleteAll();
        registeredCardRepository.deleteAll();
        ownedCardRepository.deleteAll();
        accountRepository.deleteAll();
        mobiUserRepository.deleteAll();
        fcmTokenRepository.deleteAll();
        ssafyUserRepository.deleteAll();
        Mockito.reset(fcmServiceImpl);
        Mockito.reset(restClientUtil);
    }

    @Nested
    @DisplayName("실패: 400 Bad Request")
    class Fail400 {

        @Test
        @DisplayName("merchantId가 숫자가 아닐 때")
        void fail400_not_number_merchantId() throws Exception {
            //given
            final String mobiApiKey = merchantRepository.findById(STARBUCkS_ID)
                    .orElseThrow(MerchantNotFoundException::new).getApiKey();
            final String url = "/api/v1/merchants/one/cancelled-transactions/1";

            //when
            ResultActions result = mockMvc.perform(patch(url).with(csrf())
                    .header("mobiApiKey", mobiApiKey)
                    .contentType(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("mobiApiKey가 없을 때")
        void fail400_no_mobiApiKey() throws Exception {
            //given
            final String url = "/api/v1/merchants/1/cancelled-transactions/1";

            //when
            ResultActions result = mockMvc.perform(patch(url).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("merchantId에 해당하는 MobiApiKey가 아닐 때")
        void Fail400_invalidMobiApiKey() throws Exception {
            //given
            final String url = "/api/v1/merchants/" + STARBUCkS_ID + "/cancelled-transactions/1";

            //when
            ResultActions result = mockMvc.perform(patch(url).with(csrf())
                    .header("mobiApiKey", "invalid")
                    .contentType(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("transactionId가 숫자가 아닐 때")
        void fail400_not_number_transactionId() throws Exception {
            //given
            final String mobiApiKey = merchantRepository.findById(STARBUCkS_ID)
                    .orElseThrow(MerchantNotFoundException::new).getApiKey();
            final String url = "/api/v1/merchants/1/cancelled-transactions/one";

            //when
            ResultActions result = mockMvc.perform(patch(url).with(csrf())
                    .header("mobiApiKey", mobiApiKey)
                    .contentType(MediaType.APPLICATION_JSON));

            //then
            result.andExpect(status().isBadRequest());
        }
    }

    @Transactional
    @Test
    @DisplayName("실패: 403 Forbidden : merchantId에 해당하는 transaction이 아닐 때")
    void Fail403_not_merchant_transaction() throws Exception {
        //given
        MerchantTransaction merchantTransaction = createMerchantTransaction(1906L, 1L, "20240101", "000001", 10000L,
                "info");

        final String mobiApiKey = merchantRepository.findById(STARBUCkS_ID)
                .orElseThrow(MerchantNotFoundException::new).getApiKey();

        final String url = "/api/v1/merchants/" + STARBUCkS_ID + "/cancelled-transactions/"
                + merchantTransaction.getTransactionUniqueNo();

        //when
        ResultActions result = mockMvc.perform(patch(url).with(csrf())
                .header("mobiApiKey", mobiApiKey)
                .contentType(MediaType.APPLICATION_JSON));
        em.flush();

        //then
        result.andExpect(status().isForbidden());
    }

    @Nested
    @DisplayName("실패: 404 Not Found")
    class Fail404 {

        @Test
        @DisplayName("존재하지 않는 merchantId일 때")
        void fail404_not_exist_merchant() throws Exception {
            // given
            final String mobiApiKey = merchantRepository.findById(STARBUCkS_ID)
                    .orElseThrow(MerchantNotFoundException::new).getApiKey();
            final String url = "/api/v1/merchants/9999/cancelled-transactions/1";

            // when
            ResultActions result = mockMvc.perform(patch(url).with(csrf())
                    .header("mobiApiKey", mobiApiKey)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("존재하지 않는 transactionId일 때")
        void fail404_not_exist_transaction() throws Exception {
            // given
            final String mobiApiKey = merchantRepository.findById(STARBUCkS_ID)
                    .orElseThrow(MerchantNotFoundException::new).getApiKey();
            final String url = "/api/v1/merchants/" + STARBUCkS_ID + "/cancelled-transactions/9999";

            // when
            ResultActions result = mockMvc.perform(patch(url).with(csrf())
                    .header("mobiApiKey", mobiApiKey)
                    .contentType(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isNotFound());
        }
    }

    @Transactional
    @Test
    @DisplayName("실패: 409 Conflict : 이미 취소된 transaction일 때")
    void fail409_already_cancelled() throws Exception {
        //given
        MerchantTransaction merchantTransaction = createMerchantTransaction(STARBUCkS_ID, 1L, "20240101", "000001",
                10000L, "info");
        merchantTransaction.cancel();

        final String mobiApiKey = merchantRepository.findById(STARBUCkS_ID)
                .orElseThrow(MerchantNotFoundException::new).getApiKey();

        final String url = "/api/v1/merchants/" + STARBUCkS_ID + "/cancelled-transactions/"
                + merchantTransaction.getTransactionUniqueNo();

        //when
        ResultActions result = mockMvc.perform(patch(url).with(csrf())
                .header("mobiApiKey", mobiApiKey)
                .contentType(MediaType.APPLICATION_JSON));
        em.flush();

        //then
        result.andExpect(status().isConflict());
    }

    @Nested
    @DisplayName("실패: 500 Internal Server Error")
    class fail500 {

        @Transactional
        @Test
        @DisplayName("금융 API 서버 오류")
        void Fail500_financial_api_server_error() throws Exception {
            //given
            Mockito.when(restClientUtil.cancelTransaction(Mockito.any(), Mockito.any()))
                    .thenThrow(new RuntimeException("금융 API 서버 오류"));

            MerchantTransaction merchantTransaction = createMerchantTransaction(STARBUCkS_ID, 1L, "20240101", "000001",
                    10000L, "info");

            final String mobiApiKey = merchantRepository.findById(STARBUCkS_ID)
                    .orElseThrow(MerchantNotFoundException::new).getApiKey();

            final String url = "/api/v1/merchants/" + STARBUCkS_ID + "/cancelled-transactions/"
                    + merchantTransaction.getTransactionUniqueNo();

            //when
            ResultActions result = mockMvc.perform(patch(url).with(csrf())
                    .header("mobiApiKey", mobiApiKey)
                    .contentType(MediaType.APPLICATION_JSON));
            em.flush();

            //then
            result.andExpect(status().isInternalServerError());
        }

        @Transactional
        @Test
        @DisplayName("fcm 전송 오류")
        void Fail500_fcm_send_error() throws Exception {
            //given
            doThrow(new FCMException("fcm push failed")).when(fcmServiceImpl).sendMessage(any());

            MerchantTransaction merchantTransaction = createMerchantTransaction(STARBUCkS_ID, 1L, "20240101", "000001",
                    10000L, "info");

            final String mobiApiKey = merchantRepository.findById(STARBUCkS_ID)
                    .orElseThrow(MerchantNotFoundException::new).getApiKey();

            final String url = "/api/v1/merchants/" + STARBUCkS_ID + "/cancelled-transactions/"
                    + merchantTransaction.getTransactionUniqueNo();

            //when
            ResultActions result = mockMvc.perform(patch(url).with(csrf())
                    .header("mobiApiKey", mobiApiKey)
                    .contentType(MediaType.APPLICATION_JSON));
            em.flush();

            //then
            result.andExpect(status().isInternalServerError());
        }
    }


    @Transactional
    @Test
    @DisplayName("성공")
    void Success200() throws Exception {

        //given
        MerchantTransaction merchantTransaction = createMerchantTransaction(STARBUCkS_ID, 1L, "20240101", "000001",
                10000L, "info");

        final String mobiApiKey = merchantRepository.findById(STARBUCkS_ID)
                .orElseThrow(MerchantNotFoundException::new).getApiKey();

        final String url = "/api/v1/merchants/" + STARBUCkS_ID + "/cancelled-transactions/"
                + merchantTransaction.getTransactionUniqueNo();

        //when
        ResultActions result = mockMvc.perform(patch(url).with(csrf())
                .header("mobiApiKey", mobiApiKey)
                .contentType(MediaType.APPLICATION_JSON));
        em.flush();

        //then
        result.andExpect(status().isOk());
    }


    private MerchantTransaction createMerchantTransaction(Long merchantId, Long transactionUniqueNo,
                                                          String transactionDate, String transactionTime,
                                                          Long paymentBalance, String info) {

        // 0. SsafyUser 생성
        SsafyUserResponse ssafyUserResponse = mock(SsafyUserResponse.class);
        when(ssafyUserResponse.getUserId()).thenReturn("ssafyUser");
        when(ssafyUserResponse.getUserName()).thenReturn("ssafyUser");
        when(ssafyUserResponse.getUserKey()).thenReturn("ssafyUserKey");
        when(ssafyUserResponse.getCreated()).thenReturn(OffsetDateTime.now());
        when(ssafyUserResponse.getModified()).thenReturn(OffsetDateTime.now());

        SsafyUser ssafyUser = SsafyUser.of(ssafyUserResponse);
        ssafyUserRepository.save(ssafyUser);

        // 1. FcmToken 생성
        FcmToken fcmToken = fcmTokenRepository.save(FcmToken.from("fcmToken"));

        // 2. mobiUser 생성
        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        mobiUser.setSsafyUser(ssafyUser);
        mobiUser.setFcmToken(fcmToken);
        mobiUser = mobiUserRepository.save(mobiUser);

        // 3. 계좌 생성
        AccountRec accountRec = mock(AccountRec.class);
        when(accountRec.getBankCode()).thenReturn("001");
        when(accountRec.getAccountNo()).thenReturn("12345678901234");

        Account account = Account.of(accountRec);
        account = accountRepository.save(account);

        // 4. 카드 생성
        CardRec cardRec = mock(CardRec.class);
        when(cardRec.getCardNo()).thenReturn("1234567890123456");
        when(cardRec.getCvc()).thenReturn("123");
        when(cardRec.getWithdrawalDate()).thenReturn("1234567890");
        when(cardRec.getCardExpiryDate()).thenReturn("20251231");

        CardProduct cardProduct = cardProductRepository.findByCardUniqueNo("1001-664f125022bf433").get();

        OwnedCard ownedCard = OwnedCard.of(cardRec);
        ownedCard.addRelation(mobiUser, account, cardProduct);
        ownedCardRepository.save(ownedCard);

        // 5. 카드 등록
        RegisteredCard registeredCard = RegisteredCard.from(1000000);
        registeredCard.addRelations(mobiUser, ownedCard);
        registeredCardRepository.save(registeredCard);

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

        return merchantTransactionRepository.save(merchantTransaction);
    }
}

