package com.kimnlee.common.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//테스트를 위한 회원가입용 임포트문 삭제예정(api 통신에 필요할 경우 남길 수 있음)
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthManager(private val context: Context) {
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val authService: AuthService = createAuthService() // 삭제 예정

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun signUp(email: String): SignUpResponse {
        return authService.signUp(SignUpRequest(email))
    }

    private fun createAuthService(): AuthService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:8080") // baseUrl 수정해야함ㄴ
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(AuthService::class.java)
    }
}

interface AuthService {
    @POST("api/v1/account-test")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse
}

data class SignUpRequest(val email: String)
data class SignUpResponse(val success: Boolean, val message: String)