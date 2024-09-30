package com.example.mobipay.global.authentication.dto.accountdepositupdate;

import com.example.mobipay.global.dto.Header;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AccountDepositUpdateRequest {

    private static final Long DEPOSIT_AMOUNT = 10_000_000_000L;
    private static final String TRANSACTION_SUMMARY = "(수시입출금) : 계좌 개설 이벤트 100억 입금";

    @JsonProperty("Header")
    private final Header header;

    private final String accountNo;
    private final Long transactionBalance;
    private final String transactionSummary;

    public AccountDepositUpdateRequest(String apiName, String apiKey, String userKey, String accountNo) {
        this.header = Header.of(apiName, apiKey, userKey);
        this.accountNo = accountNo;
        this.transactionBalance = DEPOSIT_AMOUNT;
        this.transactionSummary = TRANSACTION_SUMMARY;
    }
}
