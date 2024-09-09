package com.kimnlee.cardmanagement.data.repository

import com.kimnlee.cardmanagement.data.model.User
import com.kimnlee.cardmanagement.data.api.CardManagementApiService

class CardManagementRepository {
    private val apiService = CardManagementApiService.instance

    suspend fun getUsers(): List<User> {
        return apiService.getUsers()
    }
}