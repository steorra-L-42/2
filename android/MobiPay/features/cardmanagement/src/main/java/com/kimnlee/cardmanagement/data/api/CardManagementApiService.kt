package com.kimnlee.cardmanagement.data.api

import CardListResponse
import com.kimnlee.common.network.ApiClient
import com.kimnlee.cardmanagement.data.model.Photos
import com.kimnlee.common.auth.AuthManager
import retrofit2.Response
import retrofit2.http.GET

interface CardManagementApiService {

    @GET("photos")
    suspend fun getPhotos(): List<Photos>

    @GET("api/v1/cards/owned")
    suspend fun getUserCards(): Response<CardListResponse>
}