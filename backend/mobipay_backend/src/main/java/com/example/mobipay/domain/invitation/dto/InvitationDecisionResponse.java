package com.example.mobipay.domain.invitation.dto;

import com.example.mobipay.domain.invitation.entity.Invitation;
import com.example.mobipay.domain.invitation.enums.ApproveStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InvitationDecisionResponse {

    private Long invitationId;
    private ApproveStatus approved;
    private LocalDateTime created;
    private LocalDateTime modified;
    private Long carId;
    private Long mobiUserId;

    public static InvitationDecisionResponse from(Invitation invitation) {
        return InvitationDecisionResponse.builder()
                .invitationId(invitation.getId())
                .approved(invitation.getApproved())
                .created(invitation.getCreated())
                .modified(invitation.getModified())
                .carId(invitation.getCar().getId())
                .mobiUserId(invitation.getMobiUser().getId())
                .build();
    }

}
