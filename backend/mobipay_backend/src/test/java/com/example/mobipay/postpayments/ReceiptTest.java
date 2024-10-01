package com.example.mobipay.postpayments;

import static com.example.mobipay.global.error.ErrorCode.MERCHANT_TRANSACTION_NOT_FOUND;
import static com.example.mobipay.global.error.ErrorCode.RECEIPT_USER_MISMATCH;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobipay.MobiPayApplication;
import com.example.mobipay.domain.merchant.entity.Merchant;
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
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.util.SecurityTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ContextConfiguration(classes = MobiPayApplication.class)
@AutoConfigureMockMvc
public class ReceiptTest {

    @Mock
    CustomOAuth2User customOAuth2User;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MobiUserRepository mobiUserRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CardProductRepository cardProductRepository;
    @Autowired
    private RegisteredCardRepository registeredCardRepository;
    @Autowired
    private OwnedCardRepository ownedCardRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private MerchantTransactionRepository merchantTransactionRepository;

    static Stream<Arguments> invalidPathVariables() {
        return Stream.of(
                Arguments.of("문자", "abc"),
                Arguments.of("빈 문자열", ""),
                Arguments.of("특수 문자", "!@#$%^"),
                Arguments.of("음수", "-1"),
                Arguments.of("0", "0"),
                Arguments.of("문자 + 숫자", "1가2나3다"),
                Arguments.of("Long 범위 밖", "99999999999999999999999")

        );
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        mobiUserRepository.deleteAll();
        accountRepository.deleteAll();
        merchantTransactionRepository.deleteAll();
        registeredCardRepository.deleteAll();
        ownedCardRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        mobiUserRepository.deleteAll();
        accountRepository.deleteAll();
        merchantTransactionRepository.deleteAll();
        registeredCardRepository.deleteAll();
        ownedCardRepository.deleteAll();
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Test
    @Transactional
    @DisplayName("[OK] get receipt: 영수증 세부 조회")
    void 올바른_영수증_세부_조회_테스트() throws Exception {
        // given
        Long merchantId = 1906L;
        String cardNo = "1234567890123456";
        Long paymentBalance = 50000L;
        Long transactionUniqueNo = 1L;

        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        mobiUserRepository.save(mobiUser);

        CardRec cardRec = mock(CardRec.class);
        when(cardRec.getCardNo()).thenReturn(cardNo);
        when(cardRec.getCvc()).thenReturn("123");
        when(cardRec.getWithdrawalDate()).thenReturn("1234567890");
        when(cardRec.getCardExpiryDate()).thenReturn("20251231");

        OwnedCard ownedCard = OwnedCard.of(cardRec);

        AccountRec accountRec = mock(AccountRec.class);
        when(accountRec.getBankCode()).thenReturn("001");
        when(accountRec.getAccountNo()).thenReturn("12345678901234");

        Account account = Account.of(accountRec);
        accountRepository.save(account);

        CardProduct cardProduct = cardProductRepository.findByCardUniqueNo("1001-664f125022bf433").get();

        ownedCard.addRelation(mobiUser, account, cardProduct);
        ownedCardRepository.save(ownedCard);

        RegisteredCard registeredCard = RegisteredCard.from("123456");
        registeredCard.addRelations(mobiUser, ownedCard);
        registeredCardRepository.save(registeredCard);

        Merchant merchant = merchantRepository.findById(merchantId).get();

        // RequestMock 설정
        ApprovalPaymentRequest requestMock = mock(ApprovalPaymentRequest.class);
        when(requestMock.getPaymentBalance()).thenReturn(paymentBalance);
        when(requestMock.getInfo()).thenReturn("info");

        // ResponseMock 설정
        // 1. Mocking Rec 클래스
        CardTransactionResponse.Rec recResponseMock = mock(CardTransactionResponse.Rec.class);
        // 2. Mocking CardTransactionResponse
        CardTransactionResponse cardTransactionResponseMock = mock(CardTransactionResponse.class);
        when(cardTransactionResponseMock.getRec()).thenReturn(recResponseMock);
        // 3. transactionUniqueNo 반환 값 설정
        when(recResponseMock.getTransactionUniqueNo()).thenReturn(transactionUniqueNo);
        // 4. Mocking ResponseEntity
        ResponseEntity<CardTransactionResponse> responseMock = mock(ResponseEntity.class);
        when(responseMock.getBody()).thenReturn(cardTransactionResponseMock);

        MerchantTransaction merchantTransaction = MerchantTransaction.of(requestMock, responseMock);
        merchantTransaction.addRelations(registeredCard, merchant);
        merchantTransactionRepository.save(merchantTransaction);

        SecurityTestUtil.setUpMockUser(customOAuth2User, mobiUser.getId());
        final String url = "/api/v1/postpayments/receipt/" + merchantTransaction.getTransactionUniqueNo();

        // when
        ResultActions result = mockMvc.perform(get(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionUniqueNo").value(transactionUniqueNo))
                .andExpect(jsonPath("$.paymentBalance").value(paymentBalance))
                .andExpect(jsonPath("$.info").value("info"))
                .andExpect(jsonPath("$.merchantName").value(merchant.getMerchantName()))
                .andExpect(jsonPath("$.cardNo").value(cardNo));
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("invalidPathVariables")
    @Transactional
    @DisplayName("[4xxClientError] get receipt: 영수증 세부 조회(올바르지 않은 PathVariable)")
    void 올바르지_않은_PathVariable_영수증_세부_조회_테스트(String testName, String transactionUniqueNo) throws Exception {
        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        mobiUserRepository.save(mobiUser);
        SecurityTestUtil.setUpMockUser(customOAuth2User, mobiUser.getId());

        final String url = "/api/v1/postpayments/receipt/" + transactionUniqueNo;

        ResultActions result = mockMvc.perform(get(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    @DisplayName("[NotFound] get receipt: 영수증 세부 조회(존재하지 않는 merchantTransaction)")
    void 존재하지_않는_merchantTransaction_영수증_세부_조회_테스트() throws Exception {
        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        mobiUserRepository.save(mobiUser);
        SecurityTestUtil.setUpMockUser(customOAuth2User, mobiUser.getId());

        final String url = "/api/v1/postpayments/receipt/" + 1L;

        ResultActions result = mockMvc.perform(get(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MERCHANT_TRANSACTION_NOT_FOUND.getMessage()));
    }

    @Test
    @Transactional
    @DisplayName("[Forbidden] get receipt: 영수증 세부 조회(영수증 유저 불일치)")
    void 영수증_유저_불일치_영수증_세부_조회_테스트() throws Exception {
        // given
        Long merchantId = 1906L;
        String cardNo = "1234567890123456";
        Long paymentBalance = 50000L;
        Long transactionUniqueNo = 1L;

        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        mobiUserRepository.save(mobiUser);

        CardRec cardRec = mock(CardRec.class);
        when(cardRec.getCardNo()).thenReturn(cardNo);
        when(cardRec.getCvc()).thenReturn("123");
        when(cardRec.getWithdrawalDate()).thenReturn("1234567890");
        when(cardRec.getCardExpiryDate()).thenReturn("20251231");

        OwnedCard ownedCard = OwnedCard.of(cardRec);

        AccountRec accountRec = mock(AccountRec.class);
        when(accountRec.getBankCode()).thenReturn("001");
        when(accountRec.getAccountNo()).thenReturn("12345678901234");

        Account account = Account.of(accountRec);
        accountRepository.save(account);

        CardProduct cardProduct = cardProductRepository.findByCardUniqueNo("1001-664f125022bf433").get();

        ownedCard.addRelation(mobiUser, account, cardProduct);
        ownedCardRepository.save(ownedCard);

        RegisteredCard registeredCard = RegisteredCard.from("123456");
        registeredCard.addRelations(mobiUser, ownedCard);
        registeredCardRepository.save(registeredCard);

        Merchant merchant = merchantRepository.findById(merchantId).get();

        // RequestMock 설정
        ApprovalPaymentRequest requestMock = mock(ApprovalPaymentRequest.class);
        when(requestMock.getPaymentBalance()).thenReturn(paymentBalance);
        when(requestMock.getInfo()).thenReturn("info");

        // ResponseMock 설정
        // 1. Mocking Rec 클래스
        CardTransactionResponse.Rec recResponseMock = mock(CardTransactionResponse.Rec.class);
        // 2. Mocking CardTransactionResponse
        CardTransactionResponse cardTransactionResponseMock = mock(CardTransactionResponse.class);
        when(cardTransactionResponseMock.getRec()).thenReturn(recResponseMock);
        // 3. transactionUniqueNo 반환 값 설정
        when(recResponseMock.getTransactionUniqueNo()).thenReturn(transactionUniqueNo);
        // 4. Mocking ResponseEntity
        ResponseEntity<CardTransactionResponse> responseMock = mock(ResponseEntity.class);
        when(responseMock.getBody()).thenReturn(cardTransactionResponseMock);

        MerchantTransaction merchantTransaction = MerchantTransaction.of(requestMock, responseMock);
        merchantTransaction.addRelations(registeredCard, merchant);
        merchantTransactionRepository.save(merchantTransaction);

        /**
         * 다른 유저로 SecurityContextHolder 설정
         */
        Long otherUserId = 123456789L;
        SecurityTestUtil.setUpMockUser(customOAuth2User, otherUserId);
        final String url = "/api/v1/postpayments/receipt/" + merchantTransaction.getTransactionUniqueNo();

        // when
        ResultActions result = mockMvc.perform(get(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(RECEIPT_USER_MISMATCH.getMessage()));
    }
}
