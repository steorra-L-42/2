package com.example.merchant.util.mobipay;

import com.example.merchant.domain.cancel.dto.CancelTransactionResponse;
import com.example.merchant.domain.cancel.dto.MerchantTranscactionResponse;
import com.example.merchant.global.enums.MerchantType;
import com.example.merchant.util.credential.CredentialUtil;
import com.example.merchant.util.mobipay.dto.MobiPaymentRequest;
import com.example.merchant.util.mobipay.dto.MobiPaymentResponse;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class MobiPayImpl implements MobiPay{

    @Value("${mobipay.url}")
    private String MOBI_PAY_URL;

    private final Validator validator;
    private final RestClient restClient = RestClient.create();
    private final CredentialUtil credentialUtil;

    @Override
    public ResponseEntity<MobiPaymentResponse> request(MobiPaymentRequest request,
                                                       Class<MobiPaymentResponse> responseClass) {
        final String url = MOBI_PAY_URL + "/postpayments/request";
        ResponseEntity<MobiPaymentResponse> responseEntity = post(request.getType(), request, url, responseClass);
        validate(responseEntity);

        return responseEntity;
    }

    @Override
    public ResponseEntity<MerchantTranscactionResponse> getTransactionList(MerchantType merchantType,
                                                                           Class<MerchantTranscactionResponse> responseClass) {
        final String url = MOBI_PAY_URL + "/merchants/" + credentialUtil.getMerchantIdByType(merchantType) + "/transactions";
        ResponseEntity<MerchantTranscactionResponse> responseEntity = get(merchantType, url, responseClass);
        validate(responseEntity);

        return responseEntity;
    }

    @Override
    public ResponseEntity<CancelTransactionResponse> cancelTransaction(MerchantType merchantType, Long transactionUniqueNo,
                                                                       Class<CancelTransactionResponse> responseClass) {
        final String url = MOBI_PAY_URL + "/merchants/" + credentialUtil.getMerchantIdByType(merchantType)
                + "/cancelled-transactions/" + transactionUniqueNo;
        ResponseEntity<CancelTransactionResponse> responseEntity = patch(merchantType, url, responseClass);
        validate(responseEntity);

        return responseEntity;
    }

    private <R> ResponseEntity<R> get(MerchantType merchantType, String url, Class<R> responseClass) {
        return restClient.get()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("mobiApiKey", credentialUtil.getMobiApiKeyByType(merchantType))
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (request, response) -> ResponseEntity.status(response.getStatusCode()).build()
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        (request, response) -> ResponseEntity.status(response.getStatusCode()).build()
                )
                .toEntity(responseClass);
    }

    private <T, R> ResponseEntity<R> post(MerchantType merchantType, T requestBody, String url, Class<R> responseClass) {
        return restClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("mobiApiKey", credentialUtil.getMobiApiKeyByType(merchantType))
                .body(requestBody)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (request, response) -> ResponseEntity.status(response.getStatusCode()).build()
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        (request, response) -> ResponseEntity.status(response.getStatusCode()).build()
                )
                .toEntity(responseClass);
    }

    private <R> ResponseEntity<R> patch(MerchantType merchantType, String url, Class<R> responseClass) {
        return restClient.patch()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("mobiApiKey", credentialUtil.getMobiApiKeyByType(merchantType))
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (request, response) -> ResponseEntity.status(response.getStatusCode()).build()
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        (request, response) -> ResponseEntity.status(response.getStatusCode()).build()
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
