package com.kimnlee.cardmanagement.data.repository

import com.kimnlee.cardmanagement.data.api.CardManagementApiService
import com.kimnlee.cardmanagement.data.model.Card
import com.kimnlee.cardmanagement.data.model.Photos
import com.kimnlee.common.auth.AuthManager

class CardManagementRepository(private val authenticatedApi: CardManagementApiService) {

    // 더미데이터 용
    suspend fun getPhotos(): List<Photos> {
        return authenticatedApi.getPhotos()
            .filter { photo -> photo.id <= 5 }
    }

    // 자신이 소지한 카드의 목록을 조회
    suspend fun getCards(): List<Card> {
        return authenticatedApi.getCards()
//            .filter { card -> card.id <= 5 }
    }
}