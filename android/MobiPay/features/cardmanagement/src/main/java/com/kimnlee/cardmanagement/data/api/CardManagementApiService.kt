package com.kimnlee.cardmanagement.data.api

import com.kimnlee.common.network.ApiClient
import com.kimnlee.cardmanagement.data.model.Photos
import com.kimnlee.common.auth.AuthManager
import retrofit2.http.GET

interface CardManagementApiService {
    @GET("photos")
    suspend fun getPhotos(): List<Photos>

    companion object {
        fun create(authManager: AuthManager): CardManagementApiService {
            return ApiClient.getInstance(authManager)
                .authenticatedApi
                .create(CardManagementApiService::class.java)
        }
    }
}