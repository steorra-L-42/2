package com.example.mobipay.fcm;

import static com.example.mobipay.global.error.ErrorCode.CAR_NOT_FOUND;
import static com.example.mobipay.global.error.ErrorCode.INVALID_MOBI_API_KEY;
import static com.example.mobipay.global.error.ErrorCode.MERCHANT_NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobipay.MobiPayApplication;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.fcmtoken.dto.MenuListRequest;
import com.example.mobipay.domain.fcmtoken.entity.FcmToken;
import com.example.mobipay.domain.fcmtoken.repository.FcmTokenRepository;
import com.example.mobipay.domain.fcmtoken.service.FcmService;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
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
@AutoConfigureMockMvc
@ContextConfiguration(classes = MobiPayApplication.class)
public class FcmSendMenuListTest {

    private static final Long STARBUCKS_ID = 1911L;
    private static final String STARBUCKS_API_KEY = "K1qT4xM9jW2bF5vYcN7";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private MobiUserRepository mobiUserRepository;
    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @MockBean
    private FcmService fcmServiceImpl;

    private static Stream<Arguments> BadRequestParameter() {
        return Stream.of(
                Arguments.of("실패: carNumber가 null일 경우 400 Bad Request", null, STARBUCKS_ID, "menuList", 1L),
                Arguments.of("실패: carNumber가 빈 문자열일 경우 400 Bad Request", "", STARBUCKS_ID, "menuList", 1L),
                Arguments.of("실패: carNumber가 8자를 초과할 경우 400", "123456789", STARBUCKS_ID, "menuList", 1L),

                Arguments.of("실패: merchantId가 null일 경우 400 Bad Request", "123가1234", null, "menuList", 1L),
                Arguments.of("실패: menuList가 null일 경우 400 Bad Request", "123가1234", STARBUCKS_ID, null, 1L),
                Arguments.of("실패: roomId가 null일 경우 400 Bad Request", "123가1234", STARBUCKS_ID, "menuList", null)
        );
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        fcmTokenRepository.deleteAll();
        mobiUserRepository.deleteAll();
        carRepository.deleteAll();
        Mockito.reset(fcmServiceImpl);
    }

    @AfterEach
    void tearDown() {
        fcmTokenRepository.deleteAll();
        mobiUserRepository.deleteAll();
        carRepository.deleteAll();
        Mockito.reset(fcmServiceImpl);
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
    }

    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("BadRequestParameter")
    @DisplayName("[Bad Request] send menuList: 메뉴 리스트 전송(올바르지 않은 파라미터)")
    public void 올바르지_않은_파라미터_메뉴_리스트_테스트(String testName, String carNumber, Long merchantId, String info, Long roomId)
            throws Exception {
        // given
        final String url = "/api/v1/fcm/menu-list";
        final MenuListRequest menuListRequest = new MenuListRequest(carNumber, merchantId, info, roomId);
        final String requestBody = objectMapper.writeValueAsString(menuListRequest);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .header("mobiApiKey", STARBUCKS_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[NotFound] send menuList: 메뉴 리스트 전송(존재하지 않는 자동차)")
    public void 존재하지_않는_자동차_메뉴_리스트_테스트() throws Exception {
        // given
        final String url = "/api/v1/fcm/menu-list";
        final MenuListRequest menuListRequest = new MenuListRequest("111가1111", STARBUCKS_ID, "info", 1L);
        final String requestBody = objectMapper.writeValueAsString(menuListRequest);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .header("mobiApiKey", STARBUCKS_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(CAR_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("[NotFound] send menuList: 메뉴 리스트 전송(존재하지 않는 가맹점)")
    public void 존재하지_않는_가맹점_메뉴_리스트_테스트() throws Exception {
        // given
        Car car = Car.of("111가1111", "gv80");
        carRepository.save(car);

        final String url = "/api/v1/fcm/menu-list";
        final MenuListRequest menuListRequest = new MenuListRequest("111가1111", 123456789L, "info", 1L);
        final String requestBody = objectMapper.writeValueAsString(menuListRequest);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .header("mobiApiKey", STARBUCKS_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(MERCHANT_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("[BadRequest] send menuList: 메뉴 리스트 전송(일치하지 않는 API_KEY)")
    public void 일치하지_않는_API_KEY_메뉴_리스트_테스트() throws Exception {
        // given
        Car car = Car.of("111가1111", "gv80");
        carRepository.save(car);

        final String url = "/api/v1/fcm/menu-list";
        final MenuListRequest menuListRequest = new MenuListRequest("111가1111", STARBUCKS_ID, "info", 1L);
        final String requestBody = objectMapper.writeValueAsString(menuListRequest);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .header("mobiApiKey", "다른API_KEY")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INVALID_MOBI_API_KEY.getMessage()));
    }

    @Test
    @DisplayName("[OK] send menuList: 메뉴 리스트 전송")
    public void 올바른_메뉴_리스트_테스트() throws Exception {
        // given
        MobiUser mobiUser = MobiUser.of("email", "name", "010-0000-0000", "picture");
        mobiUserRepository.save(mobiUser);

        FcmToken fcmToken = FcmToken.from("fcmValue");
        fcmTokenRepository.save(fcmToken);

        // MobiUser에 FcmToken 설정 후 다시 저장
        mobiUser.setFcmToken(fcmToken);
        mobiUserRepository.save(mobiUser);  // 업데이트된 객체 저장

        Car car = Car.of("111가1111", "gv80");
        car.setOwner(mobiUser);  // 차량 소유자 설정
        carRepository.save(car);

        final String url = "/api/v1/fcm/menu-list";
        final MenuListRequest menuListRequest = new MenuListRequest("111가1111", STARBUCKS_ID, "info", 1L);
        final String requestBody = objectMapper.writeValueAsString(menuListRequest);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .header("mobiApiKey", STARBUCKS_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk());
    }

}
