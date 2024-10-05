package com.example.mobipay.postpayments;

import static com.example.mobipay.global.error.ErrorCode.MOBI_USER_NOT_FOUND;
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
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
public class HistoryListTest {

    @Mock
    CustomOAuth2User customOAuth2User;

    @Autowired
    private MockMvc mockMvc;
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

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        mobiUserRepository.deleteAll();
        merchantTransactionRepository.deleteAll();
        accountRepository.deleteAll();
        registeredCardRepository.deleteAll();
        ownedCardRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        mobiUserRepository.deleteAll();
        merchantTransactionRepository.deleteAll();
        accountRepository.deleteAll();
        registeredCardRepository.deleteAll();
        ownedCardRepository.deleteAll();
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Test
    @Transactional
    @DisplayName("[OK] get histories: 결제 내역 조회")
    void 올바른_결제_내역_조회_테스트() throws Exception {
        // given
        Long merchantId = 1906L;
        Merchant merchant = merchantRepository.findById(merchantId).get();

        /**
         * 본인의 결제 기록 생성
         */
        MobiUser user1 = MobiUser.of("bbam@gmail.com", "mobiuser1", "010-1111-1111", "mobiUser1Picture");
        mobiUserRepository.save(user1);
        RegisteredCard user1RegisteredCard = createRegisteredCard(user1);

        MerchantTransaction user1MerchantTransaction1 = createMerchantTransaction(
                11L, 11L, "20241002", "000200",
                user1RegisteredCard, merchant);

        MerchantTransaction user1MerchantTransaction2 = createMerchantTransaction(
                12L, 12L, "20241002", "160218",
                user1RegisteredCard, merchant);

        MerchantTransaction user1MerchantTransaction3 = createMerchantTransaction(
                13L, 13L, "20201002", "160000",
                user1RegisteredCard, merchant);

        /**
         * 다른 사람의 결제 기록 생성
         */
        MobiUser user2 = MobiUser.of("aabm@gmail.com", "mobiUser2", "010-2222-2222", "mobiUser2Picture");
        mobiUserRepository.save(user2);
        RegisteredCard user2RegisteredCard = createRegisteredCard(user2);

        MerchantTransaction user2MerchantTransaction1 = createMerchantTransaction(
                21L, 21L, "20200101", "120000",
                user2RegisteredCard, merchant);

        SecurityTestUtil.setUpMockUser(customOAuth2User, user1.getId());
        final String url = "/api/v1/postpayments/history";

        // when
        ResultActions result = mockMvc.perform(get(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(3))
                .andExpect(jsonPath("$.items[0].transactionUniqueNo").value(
                        user1MerchantTransaction2.getTransactionUniqueNo()))
                .andExpect(jsonPath("$.items[0].merchantName").value(merchant.getMerchantName()))
                .andExpect(jsonPath("$.items[0].transactionDate").value(user1MerchantTransaction2.getTransactionDate()))
                .andExpect(jsonPath("$.items[0].transactionTime").value(user1MerchantTransaction2.getTransactionTime()))
                .andExpect(jsonPath("$.items[0].paymentBalance").value(user1MerchantTransaction2.getPaymentBalance()))

                .andExpect(jsonPath("$.items[1].merchantName").value(merchant.getMerchantName()))
                .andExpect(jsonPath("$.items[1].transactionDate").value(user1MerchantTransaction1.getTransactionDate()))
                .andExpect(jsonPath("$.items[1].transactionTime").value(user1MerchantTransaction1.getTransactionTime()))
                .andExpect(jsonPath("$.items[1].paymentBalance").value(user1MerchantTransaction1.getPaymentBalance()))

                .andExpect(jsonPath("$.items[2].merchantName").value(merchant.getMerchantName()))
                .andExpect(jsonPath("$.items[2].transactionDate").value(user1MerchantTransaction3.getTransactionDate()))
                .andExpect(jsonPath("$.items[2].transactionTime").value(user1MerchantTransaction3.getTransactionTime()))
                .andExpect(jsonPath("$.items[2].paymentBalance").value(user1MerchantTransaction3.getPaymentBalance()));
    }

    @Test
    @Transactional
    @DisplayName("[NoContent] get histories: 결제 내역 조회")
    void NoContent_결제_내역_조회_테스트() throws Exception {
        // given
        MobiUser user = MobiUser.of("bbam@gmail.com", "mobiuser1", "010-1111-1111", "mobiUser1Picture");
        mobiUserRepository.save(user);

        SecurityTestUtil.setUpMockUser(customOAuth2User, user.getId());
        final String url = "/api/v1/postpayments/history";

        // when
        ResultActions result = mockMvc.perform(get(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    @DisplayName("[NotFound] get histories: 결제 내역 조회(존재하지 않는 유저)")
    void 존재하지_않는_유저_결제_내역_조회_테스트() throws Exception {
        // given
        MobiUser user = MobiUser.of("bbam@gmail.com", "mobiuser1", "010-1111-1111", "mobiUser1Picture");
        mobiUserRepository.save(user);

        SecurityTestUtil.setUpMockUser(customOAuth2User, 123456789L);
        final String url = "/api/v1/postpayments/history";

        // when
        ResultActions result = mockMvc.perform(get(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MOBI_USER_NOT_FOUND.getMessage()));
    }

    // 등록된 카드 생성
    private RegisteredCard createRegisteredCard(MobiUser mobiUser) {
        String bankCode = "001";
        String cardUniqueNo = "1001-664f125022bf433";

        CardRec cardRec = mock(CardRec.class);
        // 다 다르게
        when(cardRec.getCardNo()).thenReturn(generateUniqueNo(16));
        when(cardRec.getCvc()).thenReturn("123");
        when(cardRec.getWithdrawalDate()).thenReturn("1234567890");
        when(cardRec.getCardExpiryDate()).thenReturn("20251231");

        OwnedCard ownedCard = OwnedCard.of(cardRec);

        AccountRec accountRec = mock(AccountRec.class);
        when(accountRec.getBankCode()).thenReturn(bankCode);
        when(accountRec.getAccountNo()).thenReturn(generateUniqueNo(16)); // 다 다르게

        Account account = Account.of(accountRec);
        accountRepository.save(account);

        CardProduct cardProduct = cardProductRepository.findByCardUniqueNo(cardUniqueNo).get();

        ownedCard.addRelation(mobiUser, account, cardProduct);
        ownedCardRepository.save(ownedCard);

        RegisteredCard registeredCard = RegisteredCard.from();
        registeredCard.addRelations(mobiUser, ownedCard);
        registeredCardRepository.save(registeredCard);

        return registeredCard;
    }

    private String generateUniqueNo(Integer length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }

    // 등록된 카드로 MerchantTransaction 생성
    private MerchantTransaction createMerchantTransaction(Long paymentBalance, Long transactionUniqueNo,
                                                          String transactionDate,
                                                          String transactionTime, RegisteredCard registeredCard,
                                                          Merchant merchant) {
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
        when(recResponseMock.getTransactionDate()).thenReturn(transactionDate);
        when(recResponseMock.getTransactionTime()).thenReturn(transactionTime);
        when(recResponseMock.getPaymentBalance()).thenReturn(paymentBalance);
        // 4. Mocking ResponseEntity
        ResponseEntity<CardTransactionResponse> responseMock = mock(ResponseEntity.class);
        when(responseMock.getBody()).thenReturn(cardTransactionResponseMock);

        MerchantTransaction merchantTransaction = MerchantTransaction.of(requestMock, responseMock);
        merchantTransaction.addRelations(registeredCard, merchant);
        merchantTransactionRepository.save(merchantTransaction);
        return merchantTransaction;
    }
}
