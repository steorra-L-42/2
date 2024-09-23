package com.kimnlee.cardmanagement.data.repository

import com.kimnlee.cardmanagement.data.model.User
import com.kimnlee.cardmanagement.data.api.CardManagementApiService
import com.kimnlee.cardmanagement.data.model.Photos

class CardManagementRepository {
    private val apiService = CardManagementApiService.instance

    suspend fun getPhotos(): List<Photos> {
        return apiService.getPhotos().filter { photo -> photo.id <= 5 }
    }
}