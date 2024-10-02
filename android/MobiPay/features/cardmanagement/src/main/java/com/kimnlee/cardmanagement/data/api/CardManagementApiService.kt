package com.kimnlee.cardmanagement.data.api

import OwnedCardListResponse
import Photos
import RegisterCardRequest
import RegisterCardResponse
import RegisteredCardListResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CardManagementApiService {

    @GET("photos")
    suspend fun getPhotos(): List<Photos>

    @GET("api/v1/cards/owned")
    suspend fun getOwnedCards(): Response<OwnedCardListResponse>

    @GET("api/v1/cards")
    suspend fun getRegistrationCards(): Response<RegisteredCardListResponse>

    @POST("api/v1/cards")
    suspend fun registerCard(@Body request: RegisterCardRequest): RegisterCardResponse
}