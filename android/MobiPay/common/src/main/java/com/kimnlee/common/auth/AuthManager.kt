package com.kimnlee.common.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kimnlee.common.auth.api.UnAuthService
import com.kimnlee.common.auth.model.LoginRequest
import com.kimnlee.common.auth.model.LoginResponse
import com.kimnlee.common.auth.model.RegistrationRequest
import com.kimnlee.common.auth.model.RegistrationResponse
import com.kimnlee.common.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.HttpException

//테스트를 위한 회원가입용 임포트문 삭제예정(api 통신에 필요할 경우 남길 수 있음)
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import retrofit2.http.*
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthManager(private val context: Context) {

    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val unAuthService: UnAuthService = ApiClient.getInstance().unAuthenticatedApi.create(UnAuthService::class.java)
    private val authService: AuthService = createAuthService() // 삭제 예정
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        "secret_shared_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }

    fun saveAuthToken(token: String) {
        encryptedSharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return encryptedSharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveRefreshToken(token: String) {
        encryptedSharedPreferences.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }

    fun getRefreshToken(): String? {
        return encryptedSharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    fun clearTokens() {
        encryptedSharedPreferences.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }

    suspend fun loginWithKakao(activity: Activity): Result<OAuthToken> = suspendCancellableCoroutine { continuation ->
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.d("KakaoLogin", "카카오 로그인 실패")
                continuation.resume(Result.failure(error))
            } else if (token != null) {
                Log.d("KakaoLogin", "카카오 로그인 진입")
                continuation.resume(Result.success(token))
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
            UserApiClient.instance.loginWithKakaoTalk(activity, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
        }
    }

    suspend fun login(loginRequest: LoginRequest): LoginResponse = withContext(Dispatchers.IO) {
        try {
            unAuthService.login(loginRequest)
        } catch (e: HttpException) {
            // HTTP 예외를 그대로 던집니다.
            throw e
        } catch (e: Exception) {
            // 다른 예외들은 CustomException으로 감싸서 던집니다.
            throw Exception("Network error or other exception", e)
        }
    }

    suspend fun logout(): Result<Unit> = suspendCancellableCoroutine { continuation ->
        UserApiClient.instance.logout { error ->
            if (error != null) {
                continuation.resume(Result.failure(error))
            } else {
                clearTokens()
                continuation.resume(Result.success(Unit))
            }
        }
    }

    suspend fun register(registrationRequest: RegistrationRequest): RegistrationResponse = withContext(Dispatchers.IO) {
        unAuthService.register(registrationRequest)
    }

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    suspend fun signUp(email: String, name: String, phoneNumber: String): Result<SignUpResponse> {
        return try {
            val response = authService.signUp(SignUpRequest(email, name, phoneNumber))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createAuthService(): AuthService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.126:8080/") // baseUrl 수정해야함
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(AuthService::class.java)
    }
}

interface AuthService {
    @POST("api/v1/form-signup")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse
}

data class SignUpRequest(val email: String, val name: String, val phoneNumber: String)
data class SignUpResponse(val message: String)