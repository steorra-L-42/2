package com.example.mobipay.domain.invitation.service;

import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.error.CarNotFoundException;
import com.example.mobipay.domain.car.error.NotOwnerException;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.cargroup.repository.CarGroupRepository;
import com.example.mobipay.domain.invitation.dto.InvitationDecisionResponse;
import com.example.mobipay.domain.invitation.dto.InvitationRequest;
import com.example.mobipay.domain.invitation.dto.InvitationResponse;
import com.example.mobipay.domain.invitation.entity.Invitation;
import com.example.mobipay.domain.invitation.enums.ApproveStatus;
import com.example.mobipay.domain.invitation.error.AlreadyDecidedException;
import com.example.mobipay.domain.invitation.error.AlreadyInvitedException;
import com.example.mobipay.domain.invitation.error.InvitationNoFoundException;
import com.example.mobipay.domain.invitation.error.NotInvitedException;
import com.example.mobipay.domain.invitation.error.NotApprovedOrRejectedException;
import com.example.mobipay.domain.invitation.repository.InvitationRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class InvitationService {

    private final CarGroupRepository carGroupRepository;
    private final MobiUserRepository mobiUserRepository;
    private final CarRepository carRepository;
    private final InvitationRepository invitationRepository;

    @Transactional
    public InvitationResponse invite(CustomOAuth2User oauth2User, InvitationRequest request) {

        Car car = carRepository.findCarById(request.getCarId())
                .orElseThrow(CarNotFoundException::new);
        validateOwner(oauth2User.getMobiUserId(), car.getOwner().getId());

        MobiUser invitedMobiUser = mobiUserRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(MobiUserNotFoundException::new);
        validateNotInvited(invitedMobiUser, request.getCarId());

        Invitation invitation = invitationRepository.save(Invitation.of(car, invitedMobiUser));

        // TODO: FCM으로 초대 메시지 전송

        return InvitationResponse.from(invitation);
    }

    @Transactional
    public InvitationDecisionResponse decide(CustomOAuth2User oauth2User, Long invitationId, ApproveStatus decision) {

        validateApprovedOrRejected(decision); // APPROVED, REJECTED 둘 중 하나

        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(InvitationNoFoundException::new);

        validateAlreadyDecided(invitation); // 이미 처리된 초대인지 확인
        validateInvited(oauth2User.getMobiUserId(), invitation.getMobiUser().getId()); // 초대 받은 사람인지 확인

        switch (decision) {
            case APPROVED:
                invitation.approve();
                break;
            case REJECTED:
                invitation.reject();
                break;
        }

        return InvitationDecisionResponse.from(invitation);
    }

    private void validateApprovedOrRejected(ApproveStatus decision) {
        boolean ApprovedOrRejected = decision.equals(ApproveStatus.REJECTED) || decision.equals(ApproveStatus.APPROVED);
        if(!ApprovedOrRejected) {
            throw new NotApprovedOrRejectedException();
        }
    }

    private void validateOwner(Long oauth2MobiUserId, Long carOwnerId) {
        // 차량 소유자인지 확인
        if (!oauth2MobiUserId.equals(carOwnerId)) {
            throw new NotOwnerException();
        }
    }

    private void validateNotInvited(MobiUser invitedMobiUser, Long carId) {
        // 이미 초대된 번호인지 확인
        Boolean alreadyInvited = carGroupRepository.existsByMobiUserIdAndCarId(invitedMobiUser.getId(), carId);
        if (alreadyInvited) {
            throw new AlreadyInvitedException();
        }
    }

    private void validateAlreadyDecided(Invitation invitation) {
        boolean alreadyDecided = !invitation.getApproved().equals(ApproveStatus.WAITING);
        if(alreadyDecided) {
            throw new AlreadyDecidedException();
        }
    }

    private void validateInvited(Long oauthMobiUserId, Long invitedMobiUserId) {
        if(!oauthMobiUserId.equals(invitedMobiUserId)) {
            throw new NotInvitedException();
        }
    }

}
