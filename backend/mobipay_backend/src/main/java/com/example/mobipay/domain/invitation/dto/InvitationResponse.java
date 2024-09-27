package com.example.mobipay.domain.invitation.dto;

import com.example.mobipay.domain.invitation.entity.Invitation;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InvitationResponse {

    private Long invitationId;
    private LocalDateTime created;
    private Long carId;
    private Long mobiUserId;

    public static InvitationResponse from(Invitation invitation) {
        return InvitationResponse.builder()
                .invitationId(invitation.getId())
                .created(invitation.getCreated())
                .carId(invitation.getCar().getId())
                .mobiUserId(invitation.getMobiUser().getId())
                .build();
    }

}
