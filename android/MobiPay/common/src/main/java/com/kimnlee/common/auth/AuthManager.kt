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
import com.kimnlee.common.BuildConfig
import com.kimnlee.common.auth.api.AuthService
import com.kimnlee.common.auth.model.LoginRequest
import com.kimnlee.common.auth.model.RegistrationRequest
import com.kimnlee.common.auth.model.SendTokenRequest
import com.kimnlee.common.auth.model.SendTokenResponse
import com.kimnlee.common.auth.model.UserInfo
import com.kimnlee.common.network.ApiClient
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.nio.charset.StandardCharsets
import kotlin.coroutines.resume

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
private const val TAG = "AuthManager"

class AuthManager(private val context: Context) {

    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val secretKey = BuildConfig.SECRET_KEY

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

    suspend fun isLoggedInImmediately(): Boolean {
        return context.dataStore.data.first()[IS_LOGGED_IN] ?: false
    }

    fun saveAuthToken(token: String) {
        Log.d(TAG, "JWT 토큰 정상적으로 저장됨: $token")
        encryptedSharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return encryptedSharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveRefreshToken(token: String) {
        Log.d(TAG, "리프레시 토큰 정상적으로 저장됨: $token")
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

    fun saveUserInfoFromToken() {

        val token = getAuthToken()

        try {
            val key = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload

            val name = claims["name"] as? String ?: ""
            val phoneNumber = claims["phoneNumber"] as? String ?: ""
            val picture = claims["picture"] as? String ?: ""
            val email = claims["email"] as? String ?: ""

            saveUserInfo(name, phoneNumber, picture, email)
            Log.d(TAG, "유저정보 JWT토큰으로 부터 저장 됨")
        } catch (e: Exception) {
            Log.e(TAG, "유저정보 JWT토큰으로 부터 저장 실패", e)
        }
    }

    private fun saveUserInfo(name: String, phoneNumber: String, picture: String, email: String) {
        encryptedSharedPreferences.edit()
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_PHONE_NUMBER, phoneNumber)
            .putString(KEY_USER_PICTURE, picture)
            .putString(KEY_USER_EMAIL, email)
            .apply()
        Log.d(TAG, "유저정보 다음과 같이 저장 됨: name=$name, phoneNumber=$phoneNumber, picture=$picture, email=$email")
    }

    fun getUserInfo(): UserInfo {
        val name = encryptedSharedPreferences.getString(KEY_USER_NAME, "") ?: ""
        val phoneNumber = encryptedSharedPreferences.getString(KEY_USER_PHONE_NUMBER, "") ?: ""
        val picture = encryptedSharedPreferences.getString(KEY_USER_PICTURE, "") ?: ""
        val email = encryptedSharedPreferences.getString(KEY_USER_EMAIL, "") ?: ""
        return UserInfo(name, phoneNumber, picture, email)
    }

    fun clearUserInfo() {
        encryptedSharedPreferences.edit()
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_PHONE_NUMBER)
            .remove(KEY_USER_PICTURE)
            .remove(KEY_USER_EMAIL)
            .apply()
        Log.d(TAG, "유저 정보 전부 지워짐")
    }

    suspend fun loginWithKakao(activity: Activity): Result<OAuthToken> = suspendCancellableCoroutine { continuation ->
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.d(TAG, "카카오 로그인 실패", error)
                continuation.resume(Result.failure(error))
            } else if (token != null) {
                Log.d(TAG, "카카오 로그인 성공")
                continuation.resume(Result.success(token))
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
            UserApiClient.instance.loginWithKakaoTalk(activity, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
        }
    }

    suspend fun logoutWithKakao(): Result<Unit> = suspendCancellableCoroutine { continuation ->
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e(TAG, "카카오 로그아웃 실패", error)
                continuation.resume(Result.failure(error))
            } else {
                Log.i(TAG, "카카오 로그아웃 성공")
                continuation.resume(Result.success(Unit))
            }
        }
    }

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_PHONE_NUMBER = "user_phone_number"
        private const val KEY_USER_PICTURE = "user_picture"
        private const val KEY_USER_EMAIL = "user_email"
    }
}
