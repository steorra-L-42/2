package com.example.mobipay.domain.approvalwaiting.entity;

import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.merchant.entity.Merchant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "approval_waiting")
public class ApprovalWaiting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "payment_balance", nullable = false)
    private Long paymentBalance;

    @Column(name = "approved", nullable = false)
    private Boolean approved = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    private ApprovalWaiting(Long paymentBalance) {
        this.paymentBalance = paymentBalance;
    }

    public static ApprovalWaiting from(Long paymentBalance) {
        return new ApprovalWaiting(paymentBalance);
    }

    public void addRelations(Car car, Merchant merchant) {
        if (this.car != null) {
            this.car.getApprovalWaitings().remove(this);
        }
        this.car = car;
        car.getApprovalWaitings().add(this);

        if (this.merchant != null) {
            this.merchant.getApprovalWaitings().remove(this);
        }
        this.merchant = merchant;
        merchant.getApprovalWaitings().add(this);
    }
}
