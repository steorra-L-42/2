package com.kimnlee.api.network

import com.kimnlee.data.model.OCRResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OCRService {
    @Multipart
    @POST("/predict/")
    fun uploadImage(
        @Part file: MultipartBody.Part
    ): Call<OCRResponse>
}
