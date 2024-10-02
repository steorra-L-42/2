package com.example.mobipay.postpayments;

import static com.example.mobipay.global.error.ErrorCode.CAR_NOT_FOUND;
import static com.example.mobipay.global.error.ErrorCode.INVALID_MOBI_API_KEY;
import static com.example.mobipay.global.error.ErrorCode.MERCHANT_NOT_FOUND;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobipay.MobiPayApplication;
import com.example.mobipay.domain.approvalwaiting.repository.ApprovalWaitingRepository;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.cargroup.entity.CarGroup;
import com.example.mobipay.domain.cargroup.repository.CarGroupRepository;
import com.example.mobipay.domain.fcmtoken.entity.FcmToken;
import com.example.mobipay.domain.fcmtoken.repository.FcmTokenRepository;
import com.example.mobipay.domain.fcmtoken.service.FcmService;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.ownedcard.repository.OwnedCardRepository;
import com.example.mobipay.domain.postpayments.dto.PaymentRequest;
import com.example.mobipay.domain.registeredcard.repository.RegisteredCardRepository;
import com.example.mobipay.domain.setupdomain.account.entity.Account;
import com.example.mobipay.domain.setupdomain.account.repository.AccountRepository;
import com.example.mobipay.domain.setupdomain.card.entity.CardProduct;
import com.example.mobipay.domain.setupdomain.card.repository.CardProductRepository;
import com.example.mobipay.global.authentication.dto.AccountRec;
import com.example.mobipay.global.authentication.dto.CardRec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ContextConfiguration(classes = MobiPayApplication.class)
@AutoConfigureMockMvc
public class PaymentRequestTest {

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

    @MockBean
    private FcmService fcmService;

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
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Test
    @Transactional
    @DisplayName("[OK] payment request: FCM 알림 전송")
    void 올바른_알림_전송_테스트() throws Exception {
        // given
        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        FcmToken fcmToken = FcmToken.from("fcmTokenValue");
        fcmTokenRepository.save(fcmToken);
        mobiUser.setFcmToken(fcmToken);
        mobiUserRepository.save(mobiUser);

        Car car = Car.from("11가1111");
        car.setOwner(mobiUser);
        carRepository.save(car);

        CarGroup carGroup = CarGroup.of(car, mobiUser);
        carGroup.addRelation(car, mobiUser);
        carGroupRepository.save(carGroup);

        CardRec cardRec = mock(CardRec.class);
        when(cardRec.getCardNo()).thenReturn("1234567890123456");
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

        final String url = "/api/v1/postpayments/request";
        final String requestBody = objectMapper.writeValueAsString(
                new PaymentRequest("type", 5000L, "11가1111", "아메리카노 Tall 1개", 1911L));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .header("mobiApiKey", "K1qT4xM9jW2bF5vYcN7")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("[BadRequest] payment request: FCM 알림 전송(잘못된 자동차 번호)")
    void 올바르지_않은_자동차_알림_전송_테스트() throws Exception {
        // given
        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        FcmToken fcmToken = FcmToken.from("fcmTokenValue");
        fcmTokenRepository.save(fcmToken);
        mobiUser.setFcmToken(fcmToken);
        mobiUserRepository.save(mobiUser);

        Car car = Car.from("11가1111");
        car.setOwner(mobiUser);
        carRepository.save(car);

        CarGroup carGroup = CarGroup.of(car, mobiUser);
        carGroup.addRelation(car, mobiUser);
        carGroupRepository.save(carGroup);

        CardRec cardRec = mock(CardRec.class);
        when(cardRec.getCardNo()).thenReturn("1234567890123456");
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

        final String url = "/api/v1/postpayments/request";
        // RequestBody에 잘못된자동차번호 전달
        final String requestBody = objectMapper.writeValueAsString(
                new PaymentRequest("type", 5000L, "잘못된자동차번호", "아메리카노 Tall 1개", 1911L));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .header("mobiApiKey", "K1qT4xM9jW2bF5vYcN7")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(CAR_NOT_FOUND.getMessage()));
    }

    @Test
    @Transactional
    @DisplayName("[BadRequest] payment request: FCM 알림 전송(존재하지 않는 가맹점)")
    void 존재하지_않는_가맹점_알림_전송_테스트() throws Exception {
        // given
        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        FcmToken fcmToken = FcmToken.from("fcmTokenValue");
        fcmTokenRepository.save(fcmToken);
        mobiUser.setFcmToken(fcmToken);
        mobiUserRepository.save(mobiUser);

        Car car = Car.from("11가1111");
        car.setOwner(mobiUser);
        carRepository.save(car);

        CarGroup carGroup = CarGroup.of(car, mobiUser);
        carGroup.addRelation(car, mobiUser);
        carGroupRepository.save(carGroup);

        CardRec cardRec = mock(CardRec.class);
        when(cardRec.getCardNo()).thenReturn("1234567890123456");
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

        final String url = "/api/v1/postpayments/request";
        // RequestBody에 잘못된 merchantId 전달
        final String requestBody = objectMapper.writeValueAsString(
                new PaymentRequest("type", 5000L, "11가1111", "아메리카노 Tall 1개", 0L));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                .header("mobiApiKey", "K1qT4xM9jW2bF5vYcN7")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MERCHANT_NOT_FOUND.getMessage()));
    }

    @Test
    @Transactional
    @DisplayName("[BadRequest] payment request: FCM 알림 전송(올바르지않은 mobiApiKey)")
    void 올바르지_않은_mobiApiKey_알림_전송_테스트() throws Exception {
        // given
        MobiUser mobiUser = MobiUser.of("bbam@gmail.com", "mobiuser", "010-1111-1111", "mobiUserPicture");
        FcmToken fcmToken = FcmToken.from("fcmTokenValue");
        fcmTokenRepository.save(fcmToken);
        mobiUser.setFcmToken(fcmToken);
        mobiUserRepository.save(mobiUser);

        Car car = Car.from("11가1111");
        car.setOwner(mobiUser);
        carRepository.save(car);

        CarGroup carGroup = CarGroup.of(car, mobiUser);
        carGroup.addRelation(car, mobiUser);
        carGroupRepository.save(carGroup);

        CardRec cardRec = mock(CardRec.class);
        when(cardRec.getCardNo()).thenReturn("1234567890123456");
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

        final String url = "/api/v1/postpayments/request";
        final String requestBody = objectMapper.writeValueAsString(
                new PaymentRequest("type", 5000L, "11가1111", "아메리카노 Tall 1개", 1911L));

        // when
        ResultActions result = mockMvc.perform(post(url).with(csrf())
                // header에 잘못된 mobiApiKey 전달
                .header("mobiApiKey", "잘못된mobiApiKey")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_MOBI_API_KEY.getMessage()));
    }
}
