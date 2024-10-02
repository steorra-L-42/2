package com.kimnlee.cardmanagement.data.repository

import OwnedCard
import OwnedCardListResponse
import Photos
import RegisteredCardListResponse

import com.kakao.sdk.user.model.User
import com.kimnlee.cardmanagement.data.api.CardManagementApiService
import com.kimnlee.common.auth.AuthManager
import retrofit2.Response

class CardManagementRepository(private val authenticatedApi: CardManagementApiService) {

    // 더미데이터 용
    suspend fun getPhotos(): List<Photos> {
        return authenticatedApi.getPhotos()
            .filter { photo -> photo.id <= 5 }
    }

    // 자신이 소지한 카드의 목록을 조회
    suspend fun getOwnedCards(): Response<OwnedCardListResponse> {
        return authenticatedApi.getOwnedCards()
    }

    // 자신이 등록한 카드의 목록을 조회
    suspend fun getRegistrationCards(): Response<RegisteredCardListResponse> {
        return authenticatedApi.getRegistrationCards()
    }
}