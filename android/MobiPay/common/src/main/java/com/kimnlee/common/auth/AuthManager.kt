package com.kimnlee.common.auth

//테스트를 위한 회원가입용 임포트문 삭제예정(api 통신에 필요할 경우 남길 수 있음)
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
import com.kimnlee.common.auth.api.AuthService
import com.kimnlee.common.auth.model.LoginRequest
import com.kimnlee.common.auth.model.RegistrationRequest
import com.kimnlee.common.auth.model.SendTokenRequest
import com.kimnlee.common.auth.model.SendTokenResponse
import com.kimnlee.common.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
private const val TAG = "AuthManager"
class AuthManager(private val context: Context) {

    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
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
        Log.d(TAG, "authToken 저장 호출: $token")
        encryptedSharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        Log.d(TAG, "authToken 반환 호출: ${encryptedSharedPreferences.getString(KEY_AUTH_TOKEN, null)}")
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

//    suspend fun login(loginRequest: LoginRequest): Response<Void> = withContext(Dispatchers.IO) {
//        try {
//            unAuthService.login(loginRequest)
//        } catch (e: HttpException) {
//            Log.d(TAG, "${e.response()}")
//            Log.d(TAG, "${e.code()}")
//            Log.d(TAG, "Login failed: ${e.message()}")
//            throw e
//        } catch (e: Exception) {
//            throw Exception("Network error or other exception", e)
//        }
//    }

//    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
//        try {
//            // 1. 카카오 로그아웃
//            val kakaoLogoutResult = suspendCancellableCoroutine<Result<Unit>> { continuation ->
//                UserApiClient.instance.logout { error ->
//                    if (error != null) {
//                        continuation.resume(Result.failure(error))
//                    } else {
//                        continuation.resume(Result.success(Unit))
//                    }
//                }
//            }
//
//            // 카카오 로그아웃 실패 시 즉시 실패 결과 반환
//            if (kakaoLogoutResult.isFailure) {
//                return@withContext kakaoLogoutResult
//            }
//
//            // 2. 서버에 로그아웃 요청
//            val response = authService.logout()
//
//            if (response.isSuccessful) {
//                // 3. 로그아웃 성공 시 토큰 삭제
//                clearTokens()
//                setLoggedIn(false)
//                Result.success(Unit)
//            } else {
//                Result.failure(Exception("Server logout failed: ${response.code()}"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    suspend fun register(registrationRequest: RegistrationRequest): Response<Void> = withContext(Dispatchers.IO) {
//        unAuthService.register(registrationRequest)
//    }
//
//    suspend fun sendTokens(sendTokenRequest: SendTokenRequest): Response<SendTokenResponse> = withContext(Dispatchers.IO) {
//        authService.sendTokens(sendTokenRequest)
//    }

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}