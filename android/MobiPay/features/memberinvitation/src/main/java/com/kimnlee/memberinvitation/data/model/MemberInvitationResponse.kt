package com.kimnlee.memberinvitation.data.model

data class MemberInvitationResponse(
    val invitationId: Int,
    val approved: String,
    val created: String,
    val modified: String,
    val carId: Int,
    val mobiUserId: Int
)
