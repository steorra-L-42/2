package com.example.mobipay.domain.invitation.controller;

import com.example.mobipay.domain.invitation.dto.InvitationDecisionResponse;
import com.example.mobipay.domain.invitation.dto.InvitationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/invitations")
public class InvitationController {

    @PostMapping
    public ResponseEntity<InvitationResponse> invite () {
        return ResponseEntity.ok(new InvitationResponse());
    }

    @PostMapping("/{invitationId}/reponse")
    public ResponseEntity<InvitationDecisionResponse> decide () {
        return ResponseEntity.ok(new InvitationDecisionResponse());
    }

}
