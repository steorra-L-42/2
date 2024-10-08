package com.example.mobipay.util;

import com.example.mobipay.domain.cancel.dto.SsafyCancelTransactionRequest;
import com.example.mobipay.domain.cancel.dto.SsafyCancelTransactionResponse;
import com.example.mobipay.domain.postpayments.dto.cardtransaction.CardTransactionRequest;
import com.example.mobipay.domain.postpayments.dto.cardtransaction.CardTransactionResponse;
import com.example.mobipay.domain.postpayments.dto.paymentresult.PaymentResultRequest;
import com.example.mobipay.global.authentication.dto.accountcheck.AccountCheckRequest;
import com.example.mobipay.global.authentication.dto.accountcheck.AccountCheckResponse;
import com.example.mobipay.global.authentication.dto.accountdepositupdate.AccountDepositUpdateRequest;
import com.example.mobipay.global.authentication.dto.accountdepositupdate.AccountDepositUpdateResponse;
import com.example.mobipay.global.authentication.dto.accountregister.AccountRegisterRequest;
import com.example.mobipay.global.authentication.dto.accountregister.AccountRegisterResponse;
import com.example.mobipay.global.authentication.dto.cardcheck.CardCheckRequest;
import com.example.mobipay.global.authentication.dto.cardcheck.CardCheckResponse;
import com.example.mobipay.global.authentication.dto.cardregister.CardRegisterRequest;
import com.example.mobipay.global.authentication.dto.cardregister.CardRegisterResponse;
import com.example.mobipay.global.authentication.dto.ssafyusercheck.SsafyUserCheckRequest;
import com.example.mobipay.global.authentication.dto.ssafyusercheck.SsafyUserCheckResponse;
import com.example.mobipay.global.authentication.dto.ssafyuserregister.SsafyUserRegisterRequest;
import com.example.mobipay.global.authentication.dto.ssafyuserregister.SsafyUserRegisterResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class RestClientUtil {

    private static final String SSAFY_PREFIX = "https://finopenapi.ssafy.io/ssafy/api/v1";
    private static final String SSAFY_USER_CHECK_URL = SSAFY_PREFIX + "/member/search";
    private static final String SSAFY_USER_REGISTER_URL = SSAFY_PREFIX + "/member";
    private static final String ACCOUNT_REGISTER_URL = SSAFY_PREFIX + "/edu/demandDeposit/createDemandDepositAccount";
    private static final String CARD_REGISTER_URL = SSAFY_PREFIX + "/edu/creditCard/createCreditCard";
    private static final String ACCOUNT_CHECK_URL = SSAFY_PREFIX + "/edu/demandDeposit/inquireDemandDepositAccountList";
    private static final String CARD_CHECK_URL = SSAFY_PREFIX + "/edu/creditCard/inquireSignUpCreditCardList";
    private static final String ACCOUNT_DEPOSIT_UPDATE_URL =
            SSAFY_PREFIX + "/edu/demandDeposit/updateDemandDepositAccountDeposit";
    private static final String CARD_TRANSACTION_URL = SSAFY_PREFIX + "/edu/creditCard/createCreditCardTransaction";
    private static final String RESULT_TO_MERCHANT_SERVER_URL = "https://merchant.mobipay.kr/api/v1/merchants/payments/result";
    private static final String CANCEL_TRANSACTION_URL = SSAFY_PREFIX + "/edu/creditCard/deleteCreditCardTransaction";

    private final Validator validator;
    private final RestClient restClient = RestClient.create();

    public ResponseEntity<SsafyUserCheckResponse> checkSsafyUser(SsafyUserCheckRequest request,
                                                                 Class<SsafyUserCheckResponse> response) {
        ResponseEntity<SsafyUserCheckResponse> responseEntity = post(request, SSAFY_USER_CHECK_URL, response);
        validate(responseEntity);

        return responseEntity;
    }

    public ResponseEntity<SsafyUserRegisterResponse> registerSsafyUser(SsafyUserRegisterRequest request,
                                                                       Class<SsafyUserRegisterResponse> response) {

        ResponseEntity<SsafyUserRegisterResponse> responseEntity = post(request, SSAFY_USER_REGISTER_URL, response);
        validate(responseEntity);

        return responseEntity;
    }

    public ResponseEntity<AccountRegisterResponse> registerAccount(AccountRegisterRequest request,
                                                                   Class<AccountRegisterResponse> response) {
        ResponseEntity<AccountRegisterResponse> responseEntity = post(request, ACCOUNT_REGISTER_URL, response);
        validate(responseEntity);
        return responseEntity;
    }

    public ResponseEntity<CardRegisterResponse> registerCard(CardRegisterRequest request,
                                                             Class<CardRegisterResponse> response) {
        ResponseEntity<CardRegisterResponse> responseEntity = post(request, CARD_REGISTER_URL, response);
        validate(responseEntity);
        return responseEntity;
    }

    public ResponseEntity<AccountCheckResponse> checkAccount(AccountCheckRequest request,
                                                             Class<AccountCheckResponse> response) {

        ResponseEntity<AccountCheckResponse> responseEntity = post(request, ACCOUNT_CHECK_URL, response);
        validate(responseEntity);
        return responseEntity;
    }

    public ResponseEntity<CardCheckResponse> checkCard(CardCheckRequest request,
                                                       Class<CardCheckResponse> response) {

        ResponseEntity<CardCheckResponse> responseEntity = post(request, CARD_CHECK_URL, response);
        validate(responseEntity);
        return responseEntity;
    }

    public ResponseEntity<AccountDepositUpdateResponse> updateAccountDeposit(AccountDepositUpdateRequest request,
                                                                             Class<AccountDepositUpdateResponse> response) {

        ResponseEntity<AccountDepositUpdateResponse> responseEntity = post(request, ACCOUNT_DEPOSIT_UPDATE_URL,
                response);
        validate(responseEntity);
        return responseEntity;
    }

    public ResponseEntity<CardTransactionResponse> processCardTransaction(CardTransactionRequest request,
                                                                          Class<CardTransactionResponse> response) {

        ResponseEntity<CardTransactionResponse> responseEntity = post(request, CARD_TRANSACTION_URL, response);
        validate(responseEntity);
        return responseEntity;
    }

    public ResponseEntity<SsafyCancelTransactionResponse> cancelTransaction(SsafyCancelTransactionRequest request, Class<SsafyCancelTransactionResponse> response) {

        ResponseEntity<SsafyCancelTransactionResponse> responseEntity = post(request, CANCEL_TRANSACTION_URL, response);
        validate(responseEntity);
        return responseEntity;
    }

    public ResponseEntity<Void> sendResultToMerchantServer(PaymentResultRequest request,
                                                           Map<String, String> additionalHeaders) {

        return post(request, RESULT_TO_MERCHANT_SERVER_URL, Void.class, additionalHeaders);
    }

    private <T, R> ResponseEntity<R> post(T requestBody, String url, Class<R> responseClass) {
        return restClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (request, response) -> ResponseEntity.status(response.getStatusCode())
                                .body(response.getBody())
                )
                .toEntity(responseClass);
    }

    private <T, R> ResponseEntity<R> post(T requestBody, String url, Class<R> responseClass,
                                          Map<String, String> additionalHeaders) {
        return restClient.post()
                .uri(url)
                .headers(httpHeaders -> additionalHeaders.forEach(httpHeaders::add))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (request, response) -> ResponseEntity.status(response.getStatusCode())
                                .body(response.getBody())
                )
                .toEntity(responseClass);
    }


    private <T> void validate(T object) {
        Set<ConstraintViolation<T>> validatedSet = validator.validate(object);
        if (!validatedSet.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }
}