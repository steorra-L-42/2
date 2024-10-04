package com.kimnlee.common

import java.io.Serializable

data class FCMDataForInvitation(
    val type: String?,
    val title: String?,
    val body: String?,
    val invitationId: Int?,
    val created: String?,
    val inviterName: String?,
    val inviterPicture: String?,
    val carNumber: String?,
    val carModel: String?
) : Serializable
