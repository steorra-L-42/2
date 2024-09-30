package com.kimnlee.payment.data.repository

import com.kimnlee.payment.data.api.PaymentApiService
import com.kimnlee.payment.data.model.Photos


class PaymentRepository(private val authenticatedApi: PaymentApiService) {

        suspend fun getPhotos(): List<Photos> {
            return authenticatedApi.getPhotos().filter { photo -> photo.id <= 5 }
        }
    }