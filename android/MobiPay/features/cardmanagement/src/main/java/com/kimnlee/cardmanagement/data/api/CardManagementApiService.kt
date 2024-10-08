package com.kimnlee.cardmanagement.data.api

import com.kimnlee.cardmanagement.data.model.AutoPaymentCardRequest
import com.kimnlee.cardmanagement.data.model.AutoPaymentCardResponse
import com.kimnlee.cardmanagement.data.model.CardDetailResponse
import com.kimnlee.cardmanagement.data.model.MyDataConsentResponse
import com.kimnlee.cardmanagement.data.model.OwnedCardListResponse
import com.kimnlee.cardmanagement.data.model.RegisterCardRequest
import com.kimnlee.cardmanagement.data.model.RegisterCardResponse
import com.kimnlee.cardmanagement.data.model.RegisteredCardListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CardManagementApiService {

    // 소유한 카드 목록 조회
    @GET("api/v1/cards/owned")
    suspend fun getOwnedCards(): Response<OwnedCardListResponse>

    // 등록된 카드 목록 조회
    @GET("api/v1/cards")
    suspend fun getRegistrationCards(): Response<RegisteredCardListResponse>

    // 카드 등록
    @POST("api/v1/cards")
    suspend fun registerCard(@Body registerCardRequest: RegisterCardRequest): Response<RegisterCardResponse>

    // 카드 상세 조회
    @GET("api/v1/cards/{cardId}")
    suspend fun getCardDetail(@Path("cardId") cardId: Int): Response<CardDetailResponse>

    // 자동 결제 카드 등록
    @PATCH("api/v1/cards/auto-pay")
    suspend fun registerAutoPaymentCard(@Body autoPaymentCardRequest: AutoPaymentCardRequest): Response<AutoPaymentCardResponse>

    // 마이 데이터 동의 설정
    @PATCH("api/v1/users/mydata-consent")
    suspend fun submitMyDataAgreement(): Response<MyDataConsentResponse>

    // 마이 데이터 동의 여부 조회
    @GET("api/v1/users/mydata-consent")
    suspend fun getMyDataConsentStatus(): Response<MyDataConsentResponse>
}