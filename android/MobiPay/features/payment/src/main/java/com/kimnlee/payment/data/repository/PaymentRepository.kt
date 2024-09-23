package com.kimnlee.payment.data.repository

import com.kimnlee.payment.data.api.PaymentApiService
import com.kimnlee.payment.data.model.Photos


class PaymentRepository {
        private val apiService = PaymentApiService.instance

        suspend fun getPhotos(): List<Photos> {
            return apiService.getPhotos().filter { photo -> photo.id <= 5 }
        }
    }