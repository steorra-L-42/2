package com.kimnlee.cardmanagement.data.api

import com.kimnlee.common.network.ApiClient
import com.kimnlee.common.auth.AuthManager
import retrofit2.http.GET
import com.kimnlee.cardmanagement.data.model.Card

interface CardManagementApiService {
    @GET("api/v1/cards/owned")
    suspend fun getCards(): List<Card>

    companion object {
        fun create(authManager: AuthManager): CardManagementApiService {
            return ApiClient.getInstance(authManager)
                .authenticatedApi
                .create(CardManagementApiService::class.java)
        }
    }
}