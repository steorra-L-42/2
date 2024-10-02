package com.kimnlee.cardmanagement.data.api

import OwnedCardListResponse
import Photos
import RegistrationCardListResponse
import retrofit2.Response
import retrofit2.http.GET

interface CardManagementApiService {

    @GET("photos")
    suspend fun getPhotos(): List<Photos>

    @GET("api/v1/cards/owned")
    suspend fun getOwnedCards(): Response<OwnedCardListResponse>

    @GET("api/v1/cards")
    suspend fun getRegistrationCards(): Response<RegistrationCardListResponse>
}