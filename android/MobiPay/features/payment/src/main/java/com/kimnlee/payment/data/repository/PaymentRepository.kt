package com.kimnlee.payment.data.repository

import com.kimnlee.common.auth.AuthManager
import com.kimnlee.payment.data.api.PaymentApiService
import com.kimnlee.payment.data.model.Photos


class PaymentRepository(private val authManager: AuthManager) {
        private val apiService = PaymentApiService.create(authManager)

        suspend fun getPhotos(): List<Photos> {
            return apiService.getPhotos().filter { photo -> photo.id <= 5 }
        }
    }