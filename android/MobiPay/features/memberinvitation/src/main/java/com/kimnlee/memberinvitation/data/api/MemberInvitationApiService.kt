package com.kimnlee.memberinvitation.data.api

import com.kimnlee.memberinvitation.data.model.MemberInvitationData
import com.kimnlee.memberinvitation.data.model.MemberInvitationResponseData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path


interface MemberInvitationApiService {
    @POST("")
    suspend fun approvalPaymentRequest()

    @Headers("Content-Type: application/json")
    @POST("/api/v1/invitations")
    fun invitationRequest(@Body memberInvitationData: MemberInvitationData): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("/api/v1/invitations/{invitationId}/response")
    fun respondToInvitation(
        @Path("invitationId") invitationId: Int,
        @Body invitationResponseData: MemberInvitationResponseData
    ): Call<Void>
}