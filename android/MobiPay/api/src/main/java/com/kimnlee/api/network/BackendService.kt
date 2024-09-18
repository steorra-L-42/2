package com.kimnlee.mobipay

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BackendService {
    @GET("pay")
    fun getResponse(): Call<String>
    @POST("registertoken")
    fun registerToken(@Body token: String): Call<Void>
    @POST("confirmfcm")
    fun confirmFCMReceived(@Body msgId: String): Call<Void>
}
