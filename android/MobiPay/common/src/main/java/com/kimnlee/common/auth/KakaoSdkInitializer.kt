package com.kimnlee.common.auth

import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.kimnlee.common.BuildConfig

object KakaoSdkInitializer {
    private const val kakaoApiKey = BuildConfig.KAKAO_API_KEY

    // common 모듈에서 카카오 sdk 의존성 추가해서 관리하려고 여기에 만듦
    // common 모듈에서 전역으로 쓰일 BASE_URL이나 KAKAO_API_KEY들도 여기서 불러와서 사용하도록 정리함
    fun initialize(context: Context) {
        KakaoSdk.init(context, kakaoApiKey)
    }
}