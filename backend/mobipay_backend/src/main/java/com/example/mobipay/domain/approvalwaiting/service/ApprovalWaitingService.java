package com.example.mobipay.domain.approvalwaiting.service;

import com.example.mobipay.domain.approvalwaiting.entity.ApprovalWaiting;
import com.example.mobipay.domain.approvalwaiting.repository.ApprovalWaitingRepository;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.error.CarNotFoundException;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.merchant.entity.Merchant;
import com.example.mobipay.domain.merchant.error.MerchantNotFoundException;
import com.example.mobipay.domain.merchant.repository.MerchantRepository;
import com.example.mobipay.domain.postpayments.dto.ApprovalWaitingResponse;
import com.example.mobipay.domain.postpayments.dto.PaymentRequest;
import com.example.mobipay.domain.postpayments.error.InvalidMobiApiKeyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ApprovalWaitingService {

    private final MerchantRepository merchantRepository;
    private final CarRepository carRepository;
    private final ApprovalWaitingRepository approvalWaitingRepository;

    @Transactional
    public ApprovalWaitingResponse getApprovalWaiting(PaymentRequest request, String mobiApiKey) {
        // mobiApiKey 검증
        Merchant merchant = validateApiKey(request.getMerchantId(), mobiApiKey);

        // Approval_Waiting 생성 및 저장
        ApprovalWaiting approvalWaiting = createApprovalWaiting(request.getPaymentBalance(),
                request.getCarNumber(),
                merchant);

        return ApprovalWaitingResponse.of(merchant, approvalWaiting);
    }

    // 가맹점이 올바른 mobiApiKey를 가지고 있는지 검증
    private Merchant validateApiKey(Long merchantId, String mobiApiKey) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(MerchantNotFoundException::new);

        if (!merchant.getApiKey().equals(mobiApiKey)) {
            throw new InvalidMobiApiKeyException();
        }
        return merchant;
    }

    // Approval_Waiting 생성 및 관계 추가
    private ApprovalWaiting createApprovalWaiting(Long paymentBalance, String carNumber, Merchant merchant) {
        ApprovalWaiting approvalWaiting = ApprovalWaiting.from(paymentBalance);
        Car car = getCarByNumber(carNumber);
        approvalWaiting.addRelations(car, merchant);

        approvalWaitingRepository.save(approvalWaiting);
        return approvalWaiting;
    }

    // carNumber로 CAR 조회
    private Car getCarByNumber(String carNumber) {
        return carRepository.findByNumber(carNumber)
                .orElseThrow(CarNotFoundException::new);
    }
}
