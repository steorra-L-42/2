package com.kimnlee.auth.presentation.viewmodel

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.auth.api.AuthService
import com.kimnlee.common.auth.model.LoginRequest
import com.kimnlee.common.auth.model.RegistrationRequest
import com.kimnlee.common.auth.model.SendTokenRequest
import com.kimnlee.common.network.ApiClient
import com.kimnlee.common.network.NaverMapService
import com.kimnlee.firebase.FCMService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.HttpException
import retrofit2.http.Header
import retrofit2.http.Query
import kotlin.coroutines.resume

private const val TAG = "LoginViewModel"
class LoginViewModel(
    private val authManager: AuthManager,
    private val apiClient: ApiClient,
    private val fcmService: FCMService
) : ViewModel() {

    private val unAuthService: AuthService =
        apiClient.unAuthenticatedApi.create(AuthService::class.java)
    private val authService: AuthService =
        apiClient.authenticatedApi.create(AuthService::class.java)

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _needsRegistration = MutableStateFlow(false)
    val needsRegistration: StateFlow<Boolean> = _needsRegistration

    private val _registrationResult = MutableStateFlow<Boolean?>(null)
    val registrationResult: StateFlow<Boolean?> = _registrationResult

    private val _registrationError = MutableStateFlow<String?>(null)
    val registrationError: StateFlow<String?> = _registrationError

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private var _showPolicyModal = MutableStateFlow(false)
    val showPolicyModal : StateFlow<Boolean> = _showPolicyModal

    private var _hasAgreed = MutableStateFlow(false)
    val hasAgreed : StateFlow<Boolean> = _hasAgreed

    private var email: String = ""
    private var picture: String = ""
    private var kakaoAccessToken: String = ""
    private var kakaoRefreshToken: String = ""

    init {
        viewModelScope.launch {

            _isLoggedIn.value = authManager.isLoggedInImmediately()

            combine(isLoggedIn, needsRegistration) { isLoggedIn, needsRegistration ->
                when {
                    isLoggedIn -> "home"
                    needsRegistration -> "registration"
                    else -> "auth"
                }
            }.collect { route ->
                _navigationEvent.emit(route)
            }
        }
    }

    fun login(activity: Activity) {
        viewModelScope.launch {
            authManager.loginWithKakao(activity).onSuccess { token ->
                Log.d(TAG, "카카오 로그인 api 요청 성공, Token: $token")
                sendLoginRequest(token)
            }.onFailure { error ->
                Log.e(TAG, "카카오 로그인 실패", error)
            }
        }
    }

    private suspend fun sendLoginRequest(token: OAuthToken) {
        val loginRequest = LoginRequest(
            accessToken = token.accessToken,
            accessTokenExpiresAt = token.accessTokenExpiresAt.toString(),
            refreshToken = token.refreshToken,
            refreshTokenExpiresAt = token.refreshTokenExpiresAt.toString(),
            idToken = token.idToken,
            scopes = listOf("account_email", "profile_image", "talk_message")
        )
        try {
            val response = unAuthService.login(loginRequest)

            if (response.isSuccessful) {
                val authTokenFromHeaders = response.headers()["Authorization"]?.split(" ")?.getOrNull(1)
                authTokenFromHeaders?.let {
                    authManager.saveAuthToken(it)
                    Log.d(TAG, "sendLoginRequest에서 authManager.saveAuthToken 호출")
                }
                // 쿠키 처리
                val cookies = apiClient.getCookieManager().cookieStore.cookies
                val refreshToken = cookies.find { it.name == "refresh" }?.value

                refreshToken?.let { authManager.saveRefreshToken(it) }

                sendTokens()
            } else if (response.code() == 404) {
                val errorBody = response.errorBody()?.string()
                errorBody?.let {
                    val parts = it.split("Email:", "Picture:")
                    if (parts.size >= 3) {
                        email = parts[1].trim().split(",")[0]
                        picture = parts[2].trim()
                    }
                }
                _needsRegistration.value = true
                kakaoAccessToken = token.accessToken
                kakaoRefreshToken = token.refreshToken
                _navigationEvent.emit("registration")
            } else {
                Log.d(TAG, "sendLoginRequest에서 회원가입(404) 이외의 에러코드 확인 : ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "로그인 실패", e)
        }
    }

    fun register(name: String, phoneNumber: String) {
        viewModelScope.launch {
            val registrationRequest = RegistrationRequest(
                email = email,
                name = name,
                phoneNumber = phoneNumber,
                picture = picture,
                accessToken = kakaoAccessToken,
                refreshToken = kakaoRefreshToken
            )
            try {
                val response = unAuthService.register(registrationRequest)
                if (response.isSuccessful) {
                    Log.d(TAG, "response.isSuccessful")
                    val authTokenFromHeaders = response.headers()["Authorization"]?.split(" ")?.getOrNull(1)
                    authTokenFromHeaders?.let {
                        authManager.saveAuthToken(it)
                        Log.d(TAG, "AuthToken 저장 완료")
                    }
                    // 쿠키 처리
                    val cookies = apiClient.getCookieManager().cookieStore.cookies
                    val refreshToken = cookies.find { it.name == "refresh" }?.value

                    refreshToken?.let { authManager.saveRefreshToken(it) }

                    sendTokens()
                    Log.d(TAG, "회원가입 성공 AuthToken: ${authManager.getAuthToken()}, RefreshToken: ${authManager.getRefreshToken()}")
                } else if (response.code() == 500) {
                    Log.d(TAG, "register response 500, 이미 가입된 전화번호로 가입 시도")
                    _registrationError.value = "이미 가입된 전화번호에요."
                }
            } catch (e: HttpException) {
                _registrationResult.value = false
                Log.e(TAG, "회원가입 실패", e)
            }

            // 네비게이션 이벤트는 모든 처리가 끝난 후 한 번만 발생시킴
            if (_isLoggedIn.value) {
                _navigationEvent.emit("home")
            }
        }
    }

    // 200ok 오면 fcm token 바디에 넣어서 보내주기
    private suspend fun sendTokens() {
        val fcmToken = suspendCancellableCoroutine<String?> { continuation ->
            fcmService.getToken { token ->
                continuation.resume(token)
            }
        }

        fcmToken?.let {
            val sendTokensRequest = SendTokenRequest(token = fcmToken)

            try {
                val response = authService.sendTokens(sendTokensRequest)

                if (response.isSuccessful) {
                    Log.d(TAG, "FCM 토큰 전송 완료")
                    // isLoggedIn true로 만들고 나머지 상태 원상복구
                    authManager.setLoggedIn(true)
                    authManager.saveUserInfoFromToken() // 저장된 authToken에서 사용자 정보 파싱하고 저장
                    _isLoggedIn.value = true
                }
            } catch (e: Exception) {
                Log.d(TAG, "FCM 토큰 서버로 전송 실패")
                // 예외 발생 시 처리
                _isLoggedIn.value = false
                authManager.setLoggedIn(false)
            }
        } ?: run {
            Log.e(TAG, "FCM 토큰을 가져오지 못했습니다")
            // FCM 토큰을 가져오지 못한 경우 처리
            _isLoggedIn.value = false
            authManager.setLoggedIn(false)
        }
    }

    // 테스트 로그인으로 로그인하면 카카오에서 로그아웃 처리가 안되기 때문에 테스트 로그인때는 TestLogout으로 로그아웃 할것
    fun logout() {
        viewModelScope.launch {
            try {
                val result = authManager.logoutWithKakao()
                if (result.isSuccess) {
                    resetStatus()
                    Log.d(TAG, "로그아웃 성공")
                } else {
                    Log.e(TAG, "카카오 로그아웃 실패")
                    // 로그아웃에 실패해버리면 그냥 로그인 상태를 초기화 시켜버리기
                    resetStatus()
                }
            } catch (e: Exception) {
                Log.e(TAG, "로그아웃 실패", e)
            }
        }
    }


    fun testLogin() {
        viewModelScope.launch {
            authManager.setLoggedIn(true)
            _isLoggedIn.value = true
            val testToken = "test_auth_token_${System.currentTimeMillis()}"
            authManager.saveAuthToken(testToken)
            Log.i(TAG, "테스트 로그인으로 진행, 로그아웃은 TestLogout으로만 로그아웃 가능")
        }
    }

    fun resetStatus() {
        Log.d(TAG, "로그인 상태 초기화")
        viewModelScope.launch {
            authManager.setLoggedIn(false)
            _isLoggedIn.value = false
            authManager.clearTokens()
            authManager.clearUserInfo()
            _registrationResult.value = null
            _needsRegistration.value = false
            _hasAgreed.value = false
        }
    }

    // 약관 동의 모달
    fun openPrivacyModal (){
        _showPolicyModal.value = true
    }
    fun closePrivacyModal (){
        _showPolicyModal.value = false
    }
    fun tooglePolicy(){
        if (!hasAgreed.value) _hasAgreed.value = true
        else _hasAgreed.value = false
    }
}