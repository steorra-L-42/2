package com.example.mobipay.postpayments;

import static com.example.mobipay.global.error.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.example.mobipay.global.error.ErrorCode.INVALID_CARD_NO;
import static com.example.mobipay.global.error.ErrorCode.INVALID_PAYMENT_BALANCE;
import static com.example.mobipay.global.error.ErrorCode.MERCHANT_NOT_FOUND;
import static com.example.mobipay.global.error.ErrorCode.MOBI_USER_NOT_FOUND;
import static com.example.mobipay.global.error.ErrorCode.NOT_REGISTERED_CARD;
import static com.example.mobipay.global.error.ErrorCode.TRANSACTION_ALREADY_APPROVED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobipay.MobiPayApplication;
import com.example.mobipay.domain.approvalwaiting.entity.ApprovalWaiting;
import com.example.mobipay.domain.approvalwaiting.repository.ApprovalWaitingRepository;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.cargroup.entity.CarGroup;
import com.example.mobipay.domain.cargroup.repository.CarGroupRepository;
import com.example.mobipay.domain.fcmtoken.entity.FcmToken;
import com.example.mobipay.domain.fcmtoken.repository.FcmTokenRepository;
import com.example.mobipay.domain.fcmtoken.service.FcmService;
import com.example.mobipay.domain.merchant.entity.Merchant;
import com.example.mobipay.domain.merchant.repository.MerchantRepository;
import com.example.mobipay.domain.merchanttransaction.repository.MerchantTransactionRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.ownedcard.repository.OwnedCardRepository;
import com.example.mobipay.domain.postpayments.dto.ApprovalPaymentRequest;
import com.example.mobipay.domain.postpayments.dto.cardtransaction.CardTransactionRequest;
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
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.util.RestClientUtil;
import com.example.mobipay.util.SecurityTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@ContextConfiguration(classes = MobiPayApplication.class)
@AutoConfigureMockMvc
public class PaymentApprovalTest {

    @Mock
    CustomOAuth2User customOAuth2User;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private CarGroupRepository carGroupRepository;
    @Autowired
    private RegisteredCardRepository registeredCardRepository;
    @Autowired
    private ApprovalWaitingRepository approvalWaitingRepository;
    @Autowired
    private MobiUserRepository mobiUserRepository;
    @Autowired
    private OwnedCardRepository ownedCardRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CardProductRepository cardProductRepository;
    @Autowired
    private FcmTokenRepository fcmTokenRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private SsafyUserRepository ssafyUserRepository;

    @MockBean
    private FcmService fcmService;
    @MockBean
    private RestClientUtil restClientUtil;
    @Autowired
    private MerchantTransactionRepository merchantTransactionRepository;

    private static Stream<Arguments> invalidRequestBody() {
        return Stream.of(
                // testName, approvalWaitingId, merchantId, paymentBalance, cardNo, info, approved
                Arguments.of("null - approvalWaitingId", null, 1L, 10000L, "1234567890123456", "info", true),
                Arguments.of("null - merchantId", 1L, null, 10000L, "1234567890123456", "info", true),
                Arguments.of("null - paymentBalance", 1L, 1L, null, "1234567890123456", "info", true),
                Arguments.of("null - cardNo", 1L, 1L, 10000L, null, "info", true),
                Arguments.of("null - info", 1L, 1L, 10000L, "1234567890123456", null, true),
                Arguments.of("null - approved", 1L, 1L, 10000L, "1234567890123456", "info", null),
                Arguments.of("0 - paymentBalance", 1L, 1L, 10000L, "1234567890123456", "info", true),

                Arguments.of("음수 - paymentBalance", 1L, 1L, 0L, "1234567890123456", "info", true),
                Arguments.of("null - approvalWaitingId", 1L, 1L, -10000L, "1234567890123456", "info", true),

                Arguments.of("16자리보다 작은 - cardNo", 1L, 1L, -10000L, "123456", "info", true),
                Arguments.of("16자리보다 큰 - cardNo", 1L, 1L, 10000L, "123456123456123456123456123456", "info", true),

                Arguments.of("empty - approvalWaitingId", "", 1L, 10000L, "1234567890123456", "info", true),
                Arguments.of("blank - approvalWaitingId", "  ", 1L, 10000L, "1234567890123456", "info", true),
                Arguments.of("문자 - approvalWaitingId", "!approvalWaiting@@Id$$", 1L, 10000L, "1234567890123456", "info",
                        true),
                Arguments.of("문자 + 숫자 - approvalWaitingId", "123approvalWaitingId", 1L, 10000L, "1234567890123456",
                        "info", true)
                /**
                 * empty, blank, 문자열이 들어올 경우 예외 발생 처리 추후 진행하도록
                 */
//                Arguments.of("empty - merchantId", 1L, "", 10000L, "1234567890123456", "info", true),
//                Arguments.of("blank - merchantId", 1L, "  ", 10000L, "1234567890123456", "info", true),
//                Arguments.of("문자 - merchantId", 1L, "!merchant@@Id$$", 10000L, "1234567890123456", "info", true),
//                Arguments.of("문자 + 숫자 - merchantId", 1L, "123merchantId", 10000L, "1234567890123456", "info", true),
//
//                Arguments.of("empty - paymentBalance", 1L, 1L, "", "1234567890123456", "info", true),
//                Arguments.of("blank - paymentBalance", 1L, 1L, "  ", "1234567890123456", "info", true),
//                Arguments.of("문자 - paymentBalance", 1L, 1L, "  !payment@@Balance$$", "1234567890123456", "info", true),
//                Arguments.of("문자 + 숫자 - paymentBalance", 1L, 1L, "123paymentBalance", "1234567890123456", "info", true),
//
//                Arguments.of("empty - approved", 1L, 1L, 10000L, "1234567890123456", "info", ""),
//                Arguments.of("blank - approved", 1L, 1L, 10000L, "1234567890123456", "info", "  "),
//                Arguments.of("문자 - approved", 1L, 1L, 10000L, "1234567890123456", "info", "true"),
//                Arguments.of("숫자 - approved", 1L, 1L, 10000L, "1234567890123456", "info", 12345L),
//                Arguments.of("문자 + 숫자 - approved", 1L, 1L, 10000L, "1234567890123456", "info", "t1r2u3e4")
        );
    }

    @BeforeEach
    void setUp() throws IOException, FirebaseMessagingException {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        carRepository.deleteAll();
        carGroupRepository.deleteAll();
        registeredCardRepository.deleteAll();
        approvalWaitingRepository.deleteAll();
        mobiUserRepository.deleteAll();
        ownedCardRepository.deleteAll();
        accountRepository.deleteAll();
        fcmTokenRepository.deleteAll();
        ssafyUserRepository.deleteAll();

        Mockito.doReturn(true).when(fcmService).sendMessage(any());
    }

    @AfterEach
    void tearDown() {
        carRepository.deleteAll();
        carGroupRepository.deleteAll();
        registeredCardRepository.deleteAll();
        approvalWaitingRepository.deleteAll();
        ownedCardRepository.deleteAll();
        accountRepository.deleteAll();
        mobiUserRepository.deleteAll();
        fcmTokenRepository.deleteAll();
        ssafyUserRepository.deleteAll();
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Test
    @Transactional
    @DisplayName("[OK] payment approval: 결제 진행")
    void 올바른_결제_테스트() throws Exception {
        // given
        Long merchantId = 1906L;
        String cardNo = "1234567890123456";
        Long paymentBalance = 50000L;
        Boolean approved = true;

        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        FcmToken fcmToken = FcmToken.from("fcmTokenValue");
        fcmTokenRepository.save(fcmToken);
        mobiUser.setFcmToken(fcmToken);

        SsafyUserResponse ssafyUserResponse = mock(SsafyUserResponse.class);
        when(ssafyUserResponse.getUserId()).thenReturn("ssafyUser");
        when(ssafyUserResponse.getUserName()).thenReturn("ssafyUser");
        when(ssafyUserResponse.getUserKey()).thenReturn("ssafyUserKey");
        when(ssafyUserResponse.getCreated()).thenReturn(OffsetDateTime.now());
        when(ssafyUserResponse.getModified()).thenReturn(OffsetDateTime.now());

        SsafyUser ssafyUser = SsafyUser.of(ssafyUserResponse);
        ssafyUserRepository.save(ssafyUser);
        mobiUser.setSsafyUser(ssafyUser);

        mobiUserRepository.save(mobiUser);

        Car car = Car.from("11가1111");
        car.setOwner(mobiUser);
        carRepository.save(car);

        CarGroup carGroup = CarGroup.of(car, mobiUser);
        carGroup.addRelation(car, mobiUser);
        carGroupRepository.save(carGroup);

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

        ApprovalWaiting approvalWaiting = ApprovalWaiting.from(paymentBalance);
        Merchant merchant = merchantRepository.findById(merchantId).get();
        approvalWaiting.addRelations(car, merchant);
        approvalWaitingRepository.save(approvalWaiting);

        SecurityTestUtil.setUpMockUser(customOAuth2User, mobiUser.getId());
        final String url = "/api/v1/postpayments/approval";
        final String requestBody = objectMapper.writeValueAsString(
                new ApprovalPaymentRequest(approvalWaiting.getId(), merchant.getId(),
                        paymentBalance, cardNo, "info",
                        approved)
        );

        // when
        // Mock CardTransactionResponse
        CardTransactionResponse mockCardTransactionResponse = mock(CardTransactionResponse.class);
        when(mockCardTransactionResponse.getRec()).thenReturn(
                mock(CardTransactionResponse.Rec.class));
        when(mockCardTransactionResponse.getRec().getTransactionUniqueNo()).thenReturn(1L);

        // Mock ResponseEntity
        ResponseEntity<CardTransactionResponse> mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(
                mockCardTransactionResponse);
        when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        // Mock restClientUtil
        when(restClientUtil.processCardTransaction(any(CardTransactionRequest.class), any()))
                .thenReturn(mockResponseEntity);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.approvalWaitingId").value(approvalWaiting.getId()))
                .andExpect(jsonPath("$.merchantId").value(merchant.getId()))
                .andExpect(jsonPath("$.paymentBalance").value(paymentBalance))
                .andExpect(jsonPath("$.cardNo").value(cardNo))
                .andExpect(jsonPath("$.approved").value(approved));
        assertEquals(approvalWaiting.getApproved(), approved);
        assertEquals(merchantTransactionRepository.count(), 1);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("invalidRequestBody")
    @Transactional
    @DisplayName("[Bad Request] payment approval: 결제 진행(올바르지 않은 RequestBody 필드)")
    void RequestBody_올바르지_않은_필드_결제_테스트(String testName, Long approvalWaitingId, Long merchantId, Long paymentBalance,
                                       String cardNo, String info, Boolean approved) throws Exception {

        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        mobiUserRepository.save(mobiUser);

        SecurityTestUtil.setUpMockUser(customOAuth2User, mobiUser.getId());
        final String url = "/api/v1/postpayments/approval";
        final String requestBody = objectMapper.writeValueAsString(
                /**
                 * approvalWaitingId가 null인 경우
                 */
                new ApprovalPaymentRequest(approvalWaitingId, merchantId, paymentBalance, cardNo, info, approved)
        );

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().is4xxClientError());
    }

    @Test
    @Transactional
    @DisplayName("[Bad Request] payment approval: 결제 진행(존재하지 않는 mobiUser)")
    void 존재하지_않는_mobiUser_결제_테스트() throws Exception {
        Long merchantId = 1906L;
        String cardNo = "1234567890123456";
        Long paymentBalance = 50000L;
        Boolean approved = true;
        Long otherMobiUserId = 123456789L;

        // given
        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        mobiUserRepository.save(mobiUser);

        Car car = Car.from("11가1111");
        car.setOwner(mobiUser);
        carRepository.save(car);

        ApprovalWaiting approvalWaiting = ApprovalWaiting.from(paymentBalance);
        Merchant merchant = merchantRepository.findById(merchantId).get();
        approvalWaiting.addRelations(car, merchant);
        approvalWaitingRepository.save(approvalWaiting);

        SecurityTestUtil.setUpMockUser(customOAuth2User, otherMobiUserId);
        final String url = "/api/v1/postpayments/approval";
        final String requestBody = objectMapper.writeValueAsString(
                new ApprovalPaymentRequest(1L, merchantId, paymentBalance, cardNo, "info", approved)
        );

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MOBI_USER_NOT_FOUND.getMessage()));
        assertEquals(approvalWaiting.getApproved(), false);
    }


    @Test
    @Transactional
    @DisplayName("[Bad Request] payment approval: 결제 진행(이미 승인된 approval_waiting)")
    void 이미_승인된_approval_waiting_결제_테스트() throws Exception {
        // given
        Long merchantId = 1906L;
        String cardNo = "1234567890123456";
        Long paymentBalance = 50000L;
        Boolean approved = true;

        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        mobiUserRepository.save(mobiUser);

        Car car = Car.from("11가1111");
        car.setOwner(mobiUser);
        carRepository.save(car);

        ApprovalWaiting approvalWaiting = ApprovalWaiting.from(paymentBalance);
        Merchant merchant = merchantRepository.findById(merchantId).get();
        approvalWaiting.addRelations(car, merchant);
        /**
         * approvalWaiting을 승인상태로 변경
         */
        approvalWaiting.activateApproved();
        approvalWaitingRepository.save(approvalWaiting);

        SecurityTestUtil.setUpMockUser(customOAuth2User, mobiUser.getId());
        final String url = "/api/v1/postpayments/approval";
        final String requestBody = objectMapper.writeValueAsString(
                new ApprovalPaymentRequest(approvalWaiting.getId(), merchant.getId(), paymentBalance, cardNo, "info",
                        approved)
        );

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(TRANSACTION_ALREADY_APPROVED.getMessage()));
        assertEquals(approvalWaiting.getApproved(), true);
    }

    @Test
    @Transactional
    @DisplayName("[Bad Request] payment approval: 결제 진행(일치하지 않는 paymentBalance)")
    void 일치하지_않는_paymentBalance_결제_테스트() throws Exception {
        // given
        Long merchantId = 1906L;
        String cardNo = "1234567890123456";
        Long paymentBalance = 50000L;
        Long requestPaymentBalance = 10000L;
        Boolean approved = true;

        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        mobiUserRepository.save(mobiUser);

        Car car = Car.from("11가1111");
        car.setOwner(mobiUser);
        carRepository.save(car);

        ApprovalWaiting approvalWaiting = ApprovalWaiting.from(paymentBalance);
        Merchant merchant = merchantRepository.findById(merchantId).get();
        approvalWaiting.addRelations(car, merchant);
        approvalWaitingRepository.save(approvalWaiting);

        SecurityTestUtil.setUpMockUser(customOAuth2User, mobiUser.getId());
        final String url = "/api/v1/postpayments/approval";
        final String requestBody = objectMapper.writeValueAsString(
                /**
                 * 다른 PaymentBalance
                 */
                new ApprovalPaymentRequest(approvalWaiting.getId(), merchant.getId(), requestPaymentBalance, cardNo,
                        "info", approved)
        );

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_PAYMENT_BALANCE.getMessage()));
        assertEquals(approvalWaiting.getApproved(), false);
    }

    @Test
    @Transactional
    @DisplayName("[Bad Request] payment approval: 결제 진행(일치하지 않는 cardNo)")
    void 일치하지_않는_cardNo_결제_테스트() throws Exception {
        // given
        Long merchantId = 1906L;
        String cardNo = "1234567890123456";
        Long paymentBalance = 50000L;
        String requestCardNo = "9876543210987654";
        Boolean approved = true;

        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        mobiUserRepository.save(mobiUser);

        Car car = Car.from("11가1111");
        car.setOwner(mobiUser);
        carRepository.save(car);

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

        ApprovalWaiting approvalWaiting = ApprovalWaiting.from(paymentBalance);
        Merchant merchant = merchantRepository.findById(merchantId).get();
        approvalWaiting.addRelations(car, merchant);
        approvalWaitingRepository.save(approvalWaiting);

        SecurityTestUtil.setUpMockUser(customOAuth2User, mobiUser.getId());
        final String url = "/api/v1/postpayments/approval";
        final String requestBody = objectMapper.writeValueAsString(
                /**
                 * 다른 카드번호
                 */
                new ApprovalPaymentRequest(approvalWaiting.getId(), merchant.getId(), paymentBalance, requestCardNo,
                        "info", approved)
        );

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_CARD_NO.getMessage()));
        assertEquals(approvalWaiting.getApproved(), false);
    }

    @Test
    @Transactional
    @DisplayName("[Bad Request] payment approval: 결제 진행(등록된 카드가 없는 경우 - RegisteredCard의 mobiUserId 불일치)")
    void 등록되지_않은_RegisteredCard_결제_테스트() throws Exception {
        // given
        Long merchantId = 1906L;
        String cardNo = "1234567890123456";
        Long paymentBalance = 50000L;
        Boolean approved = true;

        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        mobiUserRepository.save(mobiUser);

        MobiUser otherMobiUser = MobiUser.of("other@gmail.com", "otherMobiUser", "010-2222-2222",
                "otherMobiUserPicture");
        mobiUserRepository.save(otherMobiUser);

        Car car = Car.from("11가1111");
        car.setOwner(mobiUser);
        carRepository.save(car);

        CarGroup carGroup = CarGroup.of(car, mobiUser);
        carGroup.addRelation(car, mobiUser);
        carGroupRepository.save(carGroup);

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

        ApprovalWaiting approvalWaiting = ApprovalWaiting.from(paymentBalance);
        Merchant merchant = merchantRepository.findById(merchantId).get();
        approvalWaiting.addRelations(car, merchant);
        approvalWaitingRepository.save(approvalWaiting);

        /**
         * 다른 mobiUserId가 들어온 경우
         */
        SecurityTestUtil.setUpMockUser(customOAuth2User, otherMobiUser.getId());
        final String url = "/api/v1/postpayments/approval";
        final String requestBody = objectMapper.writeValueAsString(
                new ApprovalPaymentRequest(approvalWaiting.getId(), merchant.getId(), paymentBalance, cardNo, "info",
                        approved)
        );

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(NOT_REGISTERED_CARD.getMessage()));
        assertEquals(approvalWaiting.getApproved(), false);
    }

    @Test
    @Transactional
    @DisplayName("[Not Found] payment approval: 결제 진행(등록된 카드가 없는 경우 - 잘못된 merchantId 전송)")
    void 잘못된_merchantId_결제_테스트() throws Exception {
        // given
        Long merchantId = 1906L;
        String cardNo = "1234567890123456";
        Long paymentBalance = 50000L;
        Boolean approved = true;
        Long otherMerchantId = 123456789L;

        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        mobiUserRepository.save(mobiUser);

        MobiUser otherMobiUser = MobiUser.of("other@gmail.com", "otherMobiUser", "010-2222-2222",
                "otherMobiUserPicture");
        mobiUserRepository.save(otherMobiUser);

        Car car = Car.from("11가1111");
        car.setOwner(mobiUser);
        carRepository.save(car);

        CarGroup carGroup = CarGroup.of(car, mobiUser);
        carGroup.addRelation(car, mobiUser);
        carGroupRepository.save(carGroup);

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

        ApprovalWaiting approvalWaiting = ApprovalWaiting.from(paymentBalance);
        Merchant merchant = merchantRepository.findById(merchantId).get();
        approvalWaiting.addRelations(car, merchant);
        approvalWaitingRepository.save(approvalWaiting);

        SecurityTestUtil.setUpMockUser(customOAuth2User, mobiUser.getId());
        final String url = "/api/v1/postpayments/approval";
        /**
         * 잘못된 merchantId 전송
         */
        final String requestBody = objectMapper.writeValueAsString(
                new ApprovalPaymentRequest(approvalWaiting.getId(), otherMerchantId,
                        paymentBalance, cardNo, "info", approved)
        );

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MERCHANT_NOT_FOUND.getMessage()));
        assertEquals(approvalWaiting.getApproved(), false);
    }

    @Test
    @Transactional
    @DisplayName("[InternalServerError] payment approval: 결제 진행(SSAFY_API와 올바른 통신이 되지 않은 경우 responseCode - H1xxx)")
    void SSAFY_API와_올바른_통신이_되지않은_경우_responseCode_H1xxx_결제_테스트() throws Exception {
        // given
        Long merchantId = 1906L;
        String cardNo = "1234567890123456";
        Long paymentBalance = 50000L;
        Boolean approved = true;

        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        FcmToken fcmToken = FcmToken.from("fcmTokenValue");
        fcmTokenRepository.save(fcmToken);
        mobiUser.setFcmToken(fcmToken);

        SsafyUserResponse ssafyUserResponse = mock(SsafyUserResponse.class);
        when(ssafyUserResponse.getUserId()).thenReturn("ssafyUser");
        when(ssafyUserResponse.getUserName()).thenReturn("ssafyUser");
        when(ssafyUserResponse.getUserKey()).thenReturn("ssafyUserKey");
        when(ssafyUserResponse.getCreated()).thenReturn(OffsetDateTime.now());
        when(ssafyUserResponse.getModified()).thenReturn(OffsetDateTime.now());

        SsafyUser ssafyUser = SsafyUser.of(ssafyUserResponse);
        ssafyUserRepository.save(ssafyUser);
        mobiUser.setSsafyUser(ssafyUser);

        mobiUserRepository.save(mobiUser);

        Car car = Car.from("11가1111");
        car.setOwner(mobiUser);
        carRepository.save(car);

        CarGroup carGroup = CarGroup.of(car, mobiUser);
        carGroup.addRelation(car, mobiUser);
        carGroupRepository.save(carGroup);

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

        ApprovalWaiting approvalWaiting = ApprovalWaiting.from(paymentBalance);
        Merchant merchant = merchantRepository.findById(merchantId).get();
        approvalWaiting.addRelations(car, merchant);
        approvalWaitingRepository.save(approvalWaiting);

        SecurityTestUtil.setUpMockUser(customOAuth2User, mobiUser.getId());
        final String url = "/api/v1/postpayments/approval";
        final String requestBody = objectMapper.writeValueAsString(
                new ApprovalPaymentRequest(approvalWaiting.getId(), merchant.getId(), paymentBalance, cardNo, "info",
                        approved)
        );

        // when
        // Mock CardTransactionResponse
        CardTransactionResponse mockCardTransactionResponse = mock(CardTransactionResponse.class);
        when(mockCardTransactionResponse.getRec()).thenReturn(
                mock(CardTransactionResponse.Rec.class));
        when(mockCardTransactionResponse.getRec().getTransactionUniqueNo()).thenReturn(1L);

        // Mock ResponseEntity
        ResponseEntity<CardTransactionResponse> mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(
                mockCardTransactionResponse);

        /**
         * SSAFY_API와 올바른 통신이 되지 않은 경우 (responseCode: H1XXX)
         */
        when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponseEntity.getBody().getResponseCode()).thenReturn("H1003");
        // Mock restClientUtil
        when(restClientUtil.processCardTransaction(any(CardTransactionRequest.class), any()))
                .thenReturn(mockResponseEntity);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(INTERNAL_SERVER_ERROR.getMessage()));
        assertEquals(approvalWaiting.getApproved(), false);
    }

    @Test
    @Transactional
    @DisplayName("[InternalServerError] payment approval: 결제 진행(SSAFY_API와 올바른 통신이 되지 않은 경우 responseCode - Qxxxx)")
    void SSAFY_API와_올바른_통신이_되지않은_경우_responseCode_Qxxxx_결제_테스트() throws Exception {
        // given
        Long merchantId = 1906L;
        String cardNo = "1234567890123456";
        Long paymentBalance = 50000L;
        Boolean approved = true;

        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        FcmToken fcmToken = FcmToken.from("fcmTokenValue");
        fcmTokenRepository.save(fcmToken);
        mobiUser.setFcmToken(fcmToken);

        SsafyUserResponse ssafyUserResponse = mock(SsafyUserResponse.class);
        when(ssafyUserResponse.getUserId()).thenReturn("ssafyUser");
        when(ssafyUserResponse.getUserName()).thenReturn("ssafyUser");
        when(ssafyUserResponse.getUserKey()).thenReturn("ssafyUserKey");
        when(ssafyUserResponse.getCreated()).thenReturn(OffsetDateTime.now());
        when(ssafyUserResponse.getModified()).thenReturn(OffsetDateTime.now());

        SsafyUser ssafyUser = SsafyUser.of(ssafyUserResponse);
        ssafyUserRepository.save(ssafyUser);
        mobiUser.setSsafyUser(ssafyUser);

        mobiUserRepository.save(mobiUser);

        Car car = Car.from("11가1111");
        car.setOwner(mobiUser);
        carRepository.save(car);

        CarGroup carGroup = CarGroup.of(car, mobiUser);
        carGroup.addRelation(car, mobiUser);
        carGroupRepository.save(carGroup);

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

        ApprovalWaiting approvalWaiting = ApprovalWaiting.from(paymentBalance);
        Merchant merchant = merchantRepository.findById(merchantId).get();
        approvalWaiting.addRelations(car, merchant);
        approvalWaitingRepository.save(approvalWaiting);

        SecurityTestUtil.setUpMockUser(customOAuth2User, mobiUser.getId());
        final String url = "/api/v1/postpayments/approval";
        final String requestBody = objectMapper.writeValueAsString(
                new ApprovalPaymentRequest(approvalWaiting.getId(), merchant.getId(), paymentBalance, cardNo, "info",
                        approved)
        );

        // when
        // Mock CardTransactionResponse
        CardTransactionResponse mockCardTransactionResponse = mock(CardTransactionResponse.class);
        when(mockCardTransactionResponse.getRec()).thenReturn(
                mock(CardTransactionResponse.Rec.class));
        when(mockCardTransactionResponse.getRec().getTransactionUniqueNo()).thenReturn(1L);

        // Mock ResponseEntity
        ResponseEntity<CardTransactionResponse> mockResponseEntity = mock(ResponseEntity.class);
        when(mockResponseEntity.getBody()).thenReturn(
                mockCardTransactionResponse);
        /**
         * SSAFY_API와 올바른 통신이 되지 않은 경우 (responseCode: QXXXX)
         */
        when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponseEntity.getBody().getResponseCode()).thenReturn("Q1000");
        // Mock restClientUtil
        when(restClientUtil.processCardTransaction(any(CardTransactionRequest.class), any()))
                .thenReturn(mockResponseEntity);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(url).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(INTERNAL_SERVER_ERROR.getMessage()));
        assertEquals(approvalWaiting.getApproved(), false);
    }
}
