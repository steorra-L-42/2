package com.example.mobipay.domain.invitation.dto;

import com.example.mobipay.domain.invitation.enums.ApproveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class InvitationDecisionRequest {

    @NotNull(message = "Approve status is null")
    private ApproveStatus approved;

}
