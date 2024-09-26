package com.kimnlee.cardmanagement.data.repository

import com.kimnlee.cardmanagement.data.model.User
import com.kimnlee.cardmanagement.data.api.CardManagementApiService
import com.kimnlee.cardmanagement.data.model.Photos
import com.kimnlee.common.auth.AuthManager

class CardManagementRepository(private val authManager: AuthManager) {
    private val cardManagementService = CardManagementApiService.create(authManager)

    suspend fun getPhotos(): List<Photos> {
        return cardManagementService.getPhotos().filter { photo -> photo.id <= 5 }
    }
}