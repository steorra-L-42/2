package com.example.mobipay.domain.merchanttransaction.entity;

import com.example.mobipay.domain.merchant.entity.Merchant;
import com.example.mobipay.domain.postpayments.dto.ApprovalPaymentRequest;
import com.example.mobipay.domain.postpayments.dto.cardtransaction.CardTransactionResponse;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "merchant_transaction")
public class MerchantTransaction {

    // 고유거래번호. 외부 api를 이용해 받은 response 값을 이용.
    @Id
    @Column(name = "transaction_unique_no")
    private Long transactionUniqueNo;

    @Column(name = "transaction_date", nullable = false, length = 8)
    private String transactionDate;

    @Column(name = "transaction_time", nullable = false, length = 6)
    private String transactionTime;

    @Column(name = "payment_balance", nullable = false)
    private Long paymentBalance;

    @Column(name = "info", nullable = false, columnDefinition = "TEXT")
    private String info;

    @Column(name = "cancelled", nullable = false)
    private Boolean cancelled = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "mobi_user_id"),
            @JoinColumn(name = "owned_card_id")
    })
    private RegisteredCard registeredCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    private MerchantTransaction(ApprovalPaymentRequest request, ResponseEntity<CardTransactionResponse> response) {
        this.transactionUniqueNo = response.getBody().getRec().getTransactionUniqueNo();
        LocalDateTime now = LocalDateTime.now();
        this.transactionDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.transactionTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        this.paymentBalance = request.getPaymentBalance();
        this.info = request.getInfo();
    }

    public static MerchantTransaction of(ApprovalPaymentRequest request,
                                         ResponseEntity<CardTransactionResponse> response) {
        return new MerchantTransaction(request, response);
    }

    public void addRelations(RegisteredCard registeredCard, Merchant merchant) {
        if (this.registeredCard != null) {
            this.registeredCard.getMerchantTransactions().remove(this);
        }
        this.registeredCard = registeredCard;
        registeredCard.getMerchantTransactions().add(this);

        if (this.merchant != null) {
            this.merchant.getMerchantTransactions().remove(this);
        }
        this.merchant = merchant;
        merchant.getMerchantTransactions().add(this);
    }
}
