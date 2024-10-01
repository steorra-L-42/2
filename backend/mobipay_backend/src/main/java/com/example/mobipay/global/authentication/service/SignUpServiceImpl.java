package com.example.mobipay.global.authentication.service;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.ownedcard.repository.OwnedCardRepository;
import com.example.mobipay.domain.setupdomain.account.entity.Account;
import com.example.mobipay.domain.setupdomain.account.entity.AccountProduct;
import com.example.mobipay.domain.setupdomain.account.repository.AccountProductRepository;
import com.example.mobipay.domain.setupdomain.account.repository.AccountRepository;
import com.example.mobipay.domain.setupdomain.card.entity.CardProduct;
import com.example.mobipay.domain.setupdomain.card.repository.CardProductRepository;
import com.example.mobipay.domain.ssafyuser.entity.SsafyUser;
import com.example.mobipay.domain.ssafyuser.repository.SsafyUserRepository;
import com.example.mobipay.global.authentication.dto.SsafyUserResponse;
import com.example.mobipay.global.authentication.dto.UserInfo;
import com.example.mobipay.global.authentication.dto.UserPair;
import com.example.mobipay.global.authentication.dto.accountcheck.AccountCheckRequest;
import com.example.mobipay.global.authentication.dto.accountcheck.AccountCheckResponse;
import com.example.mobipay.global.authentication.dto.accountdepositupdate.AccountDepositUpdateRequest;
import com.example.mobipay.global.authentication.dto.accountdepositupdate.AccountDepositUpdateResponse;
import com.example.mobipay.global.authentication.dto.accountregister.AccountRegisterRequest;
import com.example.mobipay.global.authentication.dto.accountregister.AccountRegisterResponse;
import com.example.mobipay.global.authentication.dto.cardcheck.CardCheckRequest;
import com.example.mobipay.global.authentication.dto.cardcheck.CardCheckResponse;
import com.example.mobipay.global.authentication.dto.cardcheck.CardCheckResponse.Rec;
import com.example.mobipay.global.authentication.dto.cardregister.CardRegisterRequest;
import com.example.mobipay.global.authentication.dto.cardregister.CardRegisterResponse;
import com.example.mobipay.global.authentication.dto.ssafyusercheck.SsafyUserCheckRequest;
import com.example.mobipay.global.authentication.dto.ssafyusercheck.SsafyUserCheckResponse;
import com.example.mobipay.global.authentication.dto.ssafyuserregister.SsafyUserRegisterRequest;
import com.example.mobipay.global.authentication.dto.ssafyuserregister.SsafyUserRegisterResponse;
import com.example.mobipay.global.authentication.error.AccountProductNotFoundException;
import com.example.mobipay.global.authentication.error.CardProductNotFoundException;
import com.example.mobipay.util.RestClientUtil;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private static final String ACCOUNT_REGISTER_API_NAME = "createDemandDepositAccount";
    private static final String CARD_REGISTER_API_NAME = "createCreditCard";
    private static final String ACCOUNT_CHECK_API_NAME = "inquireDemandDepositAccountList";
    private static final String CARD_CHECK_API_NAME = "inquireSignUpCreditCardList";
    private static final String ACCOUNT_DEPOSIT_UPDATE_API_NAME = "updateDemandDepositAccountDeposit";
    private static final String ACCOUNT_TYPE_UNIQUE_NO = "004-1-c880da59551a4e";
    private static final String[] CARD_UNIQUE_NO =
            new String[]{"1001-664f125022bf433", "1002-218c5933582e430", "1005-6d3da5e1ab334fc"};
    private static final String FAILED_TO_SIGNUP = "외부 API 에러로 인해 회원 가입이 실패했습니다.";

    private final MobiUserRepository mobiUserRepository;
    private final SsafyUserRepository ssafyUserRepository;
    private final AccountRepository accountRepository;
    private final AccountProductRepository accountProductRepository;
    private final CardProductRepository cardProductRepository;
    private final OwnedCardRepository ownedCardRepository;
    private final RestClientUtil restClientUtil;

    @Value("${ssafy.api.key}")
    private String ssafyApiKey;

    @Override
    public void checkIfMobiUserExists(String email) {
        Boolean existsMobiUser = mobiUserRepository.existsByEmail(email);
        if (!existsMobiUser) {
            // MobiPay 서비스에 가입되어 있지 않다면 404 NotFound
            throw new MobiUserNotFoundException();
        }
    }

    @Override
    @Transactional
    public MobiUser signUp(String email, String name, String phoneNumber, String picture) {

        // UserInfoDTO 생성
        UserInfo userInfo = UserInfo.of(email, name, phoneNumber, picture);
        // MobiPay 서비스에 가입되어있지 않다면 SSAFY_API 조회
        ResponseEntity<SsafyUserCheckResponse> userCheckResponse = checkSsafyUser(email);

        // SSAFY_API에 성공적으로 등록되어있던 유저라면
        if (userCheckResponse.getStatusCode().is2xxSuccessful()) {
            return handleExistingSsafyUser(userCheckResponse, userInfo);
        }
        // SSAFY_API에 성공적으로 등록되어있던 유저가 아니라면
        if (userCheckResponse.getStatusCode().is4xxClientError()) {
            return handleNewSsafyUser(email, userInfo);
        }

        // 예외로 인해 정상적인 가입이 이루어지지 않았을 경우
        throw new IllegalStateException(FAILED_TO_SIGNUP);
    }

    private MobiUser handleExistingSsafyUser(ResponseEntity<SsafyUserCheckResponse> userCheckResponse,
                                             UserInfo userInfo) {
        UserPair userPair = saveUserInfo(userCheckResponse, userInfo);

        // 계좌 및 카드 생성, 저장
        return processAccountsAndCards(userPair);
    }

    private MobiUser processAccountsAndCards(UserPair userPair) {
        // 계좌 조회
        List<AccountCheckResponse.Rec> accountRecs = checkAccounts(userPair)
                .getBody()
                .getRecs();

        registerExistsAccountsAndCards(userPair, accountRecs);

        // 계좌 존재하지 않을 시 계좌 생성 및 카드 생성 후 저장.
        registerNewAccountsAndCards(userPair, accountRecs);
        return userPair.getMobiUser();
    }

    private void registerNewAccountsAndCards(UserPair userPair, List<AccountCheckResponse.Rec> accountRecs) {
        if (accountRecs.isEmpty()) {
            Account account = registerAccount(userPair);
            registerCards(userPair, account);
        }
    }

    private void registerExistsAccountsAndCards(UserPair userPair, List<AccountCheckResponse.Rec> accountRecs) {
        // 계좌 존재 시 저장
        accountRecs.forEach(rec -> registerIfAccountExists(rec, userPair));
        // 카드 조회
        ResponseEntity<CardCheckResponse> cardCheckResponse = checkCards(userPair);
        List<Rec> cardRecs = cardCheckResponse.getBody().getRecs();
        // 카드 저장
        cardRecs.forEach(cardRec -> registerIfCardExists(cardRec, userPair));
    }

    private MobiUser handleNewSsafyUser(String email, UserInfo userInfo) {
        ResponseEntity<SsafyUserRegisterResponse> userRegisterResponse = registerSsafyUser(email);
        UserPair userPair = saveUserInfo(userRegisterResponse, userInfo);

        Account account = registerAccount(userPair);
        registerCards(userPair, account);

        return userPair.getMobiUser();
    }

    // user 저장
    private <T extends SsafyUserResponse> UserPair saveUserInfo(ResponseEntity<T> userResponse, UserInfo userInfo) {
        SsafyUser ssafyUser = SsafyUser.of(userResponse.getBody());
        MobiUser mobiUser = MobiUser.of(userInfo.getEmail(),
                userInfo.getName(),
                userInfo.getPhoneNumber(),
                userInfo.getPicture());

        ssafyUserRepository.save(ssafyUser);
        mobiUser.setSsafyUser(ssafyUser);
        mobiUserRepository.save(mobiUser);

        return new UserPair(mobiUser, ssafyUser);
    }

    // 계좌 조회
    private ResponseEntity<AccountCheckResponse> checkAccounts(UserPair userPair) {
        AccountCheckRequest accountCheckRequest = new AccountCheckRequest(ACCOUNT_CHECK_API_NAME, ssafyApiKey,
                userPair.getSsafyUser().getUserKey());
        return restClientUtil.checkAccount(accountCheckRequest, AccountCheckResponse.class);
    }

    // 계좌 조회 후 존재한다면 등록
    private void registerIfAccountExists(AccountCheckResponse.Rec rec, UserPair userPair) {
        String accountName = rec.getAccountName();

        AccountProduct accountProduct = accountProductRepository.findByAccountName(accountName)
                .orElseThrow(AccountProductNotFoundException::new);

        Account account = Account.of(rec);
        account.addRelation(userPair.getMobiUser(), accountProduct);

        accountRepository.save(account);

        // 계좌 저장 후 금액 추가
        updateAccountDeposit(userPair, account.getAccountNo());
    }

    // 카드 조회
    private ResponseEntity<CardCheckResponse> checkCards(UserPair userPair) {
        CardCheckRequest cardCheckRequest = new CardCheckRequest(CARD_CHECK_API_NAME, ssafyApiKey,
                userPair.getSsafyUser().getUserKey());
        return restClientUtil.checkCard(cardCheckRequest, CardCheckResponse.class);
    }

    // 카드 조회 후 존재한다면 등록
    private void registerIfCardExists(Rec cardRec, UserPair userPair) {
        String accountNo = cardRec.getWithdrawalAccountNo();
        Account account = accountRepository.findByAccountNo(accountNo)
                .orElseThrow(AccountProductNotFoundException::new);

        String cardUniqueNo = cardRec.getCardUniqueNo();
        CardProduct cardProduct = cardProductRepository.findByCardUniqueNo(cardUniqueNo)
                .orElseThrow(CardProductNotFoundException::new);

        OwnedCard ownedCard = OwnedCard.of(cardRec);
        ownedCard.addRelation(userPair.getMobiUser(), account, cardProduct);

        ownedCardRepository.save(ownedCard);
    }

    // ssafy 유저 체크
    private ResponseEntity<SsafyUserCheckResponse> checkSsafyUser(String email) {
        SsafyUserCheckRequest ssafyUserCheckRequest = new SsafyUserCheckRequest(email, ssafyApiKey);
        return restClientUtil.checkSsafyUser(ssafyUserCheckRequest, SsafyUserCheckResponse.class);
    }

    // ssafy 유저 등록
    private ResponseEntity<SsafyUserRegisterResponse> registerSsafyUser(String email) {
        SsafyUserRegisterRequest ssafyUserRegisterRequest = new SsafyUserRegisterRequest(email, ssafyApiKey);
        return restClientUtil.registerSsafyUser(ssafyUserRegisterRequest, SsafyUserRegisterResponse.class);
    }

    // 계좌 등록 & 엔티티 연관관계 설정
    private Account registerAccount(UserPair userPair) {
        // 계좌 등록
        ResponseEntity<AccountRegisterResponse> accountRegisterResponse = requestRegisterAccount(userPair);
        // 계좌 초기 금액 입금
        AccountRegisterResponse.Rec rec = accountRegisterResponse.getBody().getRec();
        updateAccountDeposit(userPair, rec.getAccountNo());

        // 계좌, 계좌상품, MobiUser 연관관계 설정
        Account account = Account.of(rec);
        AccountProduct accountProduct = accountProductRepository.findByAccountTypeUniqueNo(ACCOUNT_TYPE_UNIQUE_NO)
                .orElseThrow(AccountProductNotFoundException::new);

        account.addRelation(userPair.getMobiUser(), accountProduct);

        // save
        accountRepository.save(account);
        return account;
    }

    // 계좌 등록 요청
    private ResponseEntity<AccountRegisterResponse> requestRegisterAccount(UserPair userPair) {
        AccountRegisterRequest accountRegisterRequest = new AccountRegisterRequest(ACCOUNT_REGISTER_API_NAME,
                ssafyApiKey,
                userPair.getSsafyUser().getUserKey(),
                ACCOUNT_TYPE_UNIQUE_NO);

        return restClientUtil.registerAccount(accountRegisterRequest, AccountRegisterResponse.class);
    }

    // 계좌 초기 금액 입금
    private void updateAccountDeposit(UserPair userPair, String accountNo) {
        AccountDepositUpdateRequest accountDepositUpdateRequest = new AccountDepositUpdateRequest(
                ACCOUNT_DEPOSIT_UPDATE_API_NAME,
                ssafyApiKey,
                userPair.getSsafyUser().getUserKey(),
                accountNo
        );
        restClientUtil.updateAccountDeposit(accountDepositUpdateRequest, AccountDepositUpdateResponse.class);
    }

    // 카드 등록 & 엔티티 연관관계 설정
    private void registerCards(UserPair userPair, Account account) {
        // 카드 등록
        Arrays.stream(CARD_UNIQUE_NO).forEach(cardUniqueNo -> {
            ResponseEntity<CardRegisterResponse> cardRegisterResponse = requestRegisterCard(userPair, account,
                    cardUniqueNo);

            // 카드, 카드상품, MobiUser, 계좌 연관관계 설정
            OwnedCard ownedCard = OwnedCard.of(cardRegisterResponse.getBody().getRec());
            CardProduct cardProduct = cardProductRepository.findByCardUniqueNo(cardUniqueNo)
                    .orElseThrow(CardProductNotFoundException::new);

            // save
            ownedCard.addRelation(userPair.getMobiUser(), account, cardProduct);
            ownedCardRepository.save(ownedCard);
        });
    }

    // 카드 등록 요청
    private ResponseEntity<CardRegisterResponse> requestRegisterCard(UserPair userPair, Account account,
                                                                     String cardUniqueNo) {
        CardRegisterRequest cardRegisterRequest = new CardRegisterRequest(CARD_REGISTER_API_NAME,
                ssafyApiKey,
                userPair.getSsafyUser().getUserKey(),
                cardUniqueNo,
                account.getAccountNo());

        return restClientUtil.registerCard(cardRegisterRequest, CardRegisterResponse.class);
    }
}
