package com.kimnlee.common.network

import android.content.ContentValues.TAG
import android.util.Log
import com.kimnlee.common.BuildConfig
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.auth.model.ReverseGeocodeResponse
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

class ApiClient private constructor(private val tokenProvider: () -> String?) {

    private val baseUrl = BuildConfig.BASE_URL
    private val fcmBaseUrl = BuildConfig.FCM_BASE_URL
    private val ocrBaseUrl = BuildConfig.OCR_BASE_URL

    private val cookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val authToken = tokenProvider()

        val newRequest = if (!authToken.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $authToken")
                .build()
        } else {
            originalRequest
        }
        chain.proceed(newRequest)
    }

    private val authenticatedOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .cookieJar(JavaNetCookieJar(cookieManager))
        .build()

    private val unauthenticatedOkHttpClient = OkHttpClient.Builder()
        .cookieJar(JavaNetCookieJar(cookieManager))
        .build()

    // AuthToken을 사용하는 Api (백엔드 통신할 때 사용)
    val authenticatedApi: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(authenticatedOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // AuthToken을 사용하지 않는 Api (로그인 때 사용)
    val unAuthenticatedApi: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(unauthenticatedOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // fcmService용 retrofit
    val fcmApi: Retrofit = Retrofit.Builder()
        .baseUrl(fcmBaseUrl)
        .client(unauthenticatedOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val ocrService: OCRService by lazy {
        Retrofit.Builder()
            .baseUrl(ocrBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OCRService::class.java)
    }

    fun getCookieManager(): CookieManager = cookieManager

     val naverMapService : NaverMapService by lazy {
         Retrofit.Builder()
        .baseUrl("https://naveropenapi.apigw.ntruss.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NaverMapService::class.java)
     }
    companion object {
        @Volatile
        private var instance: ApiClient? = null

        fun getInstance(tokenProvider: () -> String?): ApiClient {
            return instance ?: synchronized(this) {
                instance ?: ApiClient(tokenProvider).also { instance = it }
            }
        }
    }
}