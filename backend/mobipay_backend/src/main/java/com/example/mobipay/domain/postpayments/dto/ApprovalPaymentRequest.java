package com.example.mobipay.domain.postpayments.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class ApprovalPaymentRequest {

    @NotNull(message = "empty approvalWaitingId")
    private Long approvalWaitingId;

    @NotNull(message = "empty merchantId")
    private Long merchantId;

    @NotNull(message = "empty paymentBalance")
    @Min(value = 1, message = "Payment balance must be greater than 0")
    private Long paymentBalance;

    @NotNull(message = "empty paymentId")
    @Size(min = 16, max = 16, message = "cardNo must be 16 characters")
    private String cardNo;

    @NotNull(message = "empty info")
    private String info;

    @NotNull(message = "empty approved")
    private Boolean approved;
}
