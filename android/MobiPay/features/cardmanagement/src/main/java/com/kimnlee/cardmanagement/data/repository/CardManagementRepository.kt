package com.kimnlee.cardmanagement.data.repository

import com.kimnlee.cardmanagement.data.model.User
import com.kimnlee.cardmanagement.data.api.CardManagementApiService
import com.kimnlee.cardmanagement.data.model.Photos
import com.kimnlee.common.auth.AuthManager

class CardManagementRepository(private val authenticatedApi: CardManagementApiService) {

    suspend fun getPhotos(): List<Photos> {
        return authenticatedApi.getPhotos().filter { photo -> photo.id <= 5 }
    }
}