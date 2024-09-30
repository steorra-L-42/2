package com.kimnlee.cardmanagement.data.repository

import com.kimnlee.cardmanagement.data.api.CardManagementApiService
import com.kimnlee.cardmanagement.data.model.Card
import com.kimnlee.common.auth.AuthManager

class CardManagementRepository(private val authManager: AuthManager) {
    private val cardManagementService = CardManagementApiService.create(authManager)

    suspend fun getCards(): List<Card> {
        return cardManagementService.getCards()
//            .filter { photo -> photo.id <= 5 }
    }
}