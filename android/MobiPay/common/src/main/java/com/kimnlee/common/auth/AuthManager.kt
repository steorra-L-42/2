package com.kimnlee.common.auth

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine

//테스트를 위한 회원가입용 임포트문 삭제예정(api 통신에 필요할 경우 남길 수 있음)
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import kotlin.coroutines.resume
import retrofit2.http.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthManager(private val context: Context) {
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val authService: AuthService = createAuthService() // 삭제 예정

    // 카카오 로그인 로직 -------------------------------------------------------------
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

    fun clearAuthToken() {
        encryptedSharedPreferences.edit().remove(KEY_AUTH_TOKEN).apply()
    }

    suspend fun loginWithKakao(): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                continuation.resume(Result.failure(error))
            } else if (token != null) {
                // 여기서 백엔드 서버에 로그인 요청을 보내고 응답을 처리해야 함
                // 지금은 임시로 카카오 액세스 토큰 암호화해서 저장, 동작확인용 (나중에 모든 값 저장해서 진송이형한테 보내줘야 됨)
                // 백엔드 응답오면 응답에서 온 authToken, refreshToken 저장해야 함
                Log.i("AuthManager", "카카오 로그인 token 확인용 : ${token}")
                saveAuthToken(token.accessToken)
                continuation.resume(Result.success(Unit))
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    suspend fun logout(): Result<Unit> = suspendCancellableCoroutine { continuation ->
        UserApiClient.instance.logout { error ->
            if (error != null) {
                continuation.resume(Result.failure(error))
            } else {
                clearAuthToken()
                continuation.resume(Result.success(Unit))
            }
        }
    }
    // 카카오 로그인 로직 끝 -------------------------------------------------------------

    suspend fun signUp(email: String, name: String, phoneNumber: String): SignUpResponse {
        Log.d("SignUp", "회원가입 요청: $email, $name, $phoneNumber !!!!!!!!!!!!!!")
        return try {
            val response = authService.signUp(SignUpRequest(email, name, phoneNumber))
            Log.d("SignUp", "회원가입 응답: ${response} !!!!!!!!!!!!!!!!!!")
            response
        } catch (e: Exception) {
            Log.e("SignUp", "회원가입 오류: ${e.message} !!!!!!!!!!!!!!!!!!")
            throw e
        }
    }

    private fun createAuthService(): AuthService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.100.126:8080/") // baseUrl 수정해야함
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(AuthService::class.java)
    }

    // 카카오 로그인에 필요한 obj
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}

interface AuthService {
    @POST("api/v1/account-test")
    suspend fun signUp(@Body request: SignUpRequest): SignUpResponse
}

data class SignUpRequest(val email: String, val name: String, val phoneNumber: String)
data class SignUpResponse(val success: Boolean, val message: String)