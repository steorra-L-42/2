import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.kimnlee.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // local.properties에서 읽기
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        val fcmBaseUrl = localProperties.getProperty("FCM_BASE_URL") ?: "http://54.250.106.89:8080"
        val ocrBaseUrl = localProperties.getProperty("OCR_BASE_URL") ?: "http://54.250.106.89:8080"
        val baseUrl = localProperties.getProperty("BASE_URL") ?: "No Base URL Defined"
        val kakaoApiKey = localProperties.getProperty("KAKAO_API_KEY") ?: "No Api Key Defined"
        val naverMapClientSecret = localProperties.getProperty("NAVER_MAP_CLIENT_SECRET") ?: "NO_TOKEN_NO_LOCAL_PROPERTIES"
        val secretKey = localProperties.getProperty("SECRET_KEY")

        buildConfigField("String", "FCM_BASE_URL", "\"$fcmBaseUrl\"")
        buildConfigField("String", "OCR_BASE_URL", "\"$ocrBaseUrl\"")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "KAKAO_API_KEY", "\"$kakaoApiKey\"")
        buildConfigField("String", "NAVER_MAP_CLIENT_SECRET", "\"$naverMapClientSecret\"")
        buildConfigField("String", "SECRET_KEY", "\"$secretKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }
}

dependencies {

    // AndroidX Core
    api(libs.androidx.core.ktx)

    // Compose UI
    api(libs.androidx.ui)
    api(libs.androidx.material3)
    api(libs.androidx.ui.tooling.preview)
    api(libs.androidx.ui.graphics)

    // Compose Runtime
    api(libs.androidx.runtime)
    api(libs.androidx.runtime.android)

    // Navigation
    api(libs.androidx.navigation.common.ktx)
    api(libs.androidx.navigation.compose)

    // Lifecycle
    api(libs.androidx.lifecycle.runtime.compose)
    api(libs.lifecycle.runtime.ktx)
    api(libs.androidx.lifecycle.viewmodel.compose)

    // Google Play Location, Maps
    api(libs.play.services.location)
    api(libs.play.services.maps)

    // Retrofit
    api(libs.retrofit)
    api(libs.converter.gson)

    // Camera
    api(libs.androidx.camera.camera2)
    api(libs.androidx.camera.lifecycle)
    api(libs.androidx.camera.view)
    api(libs.androidx.camera.extensions)
    api(libs.androidx.camera.core)

    // Gson
    api(libs.gson)

    // Android auto
    api(libs.androidx.app)

    // EncryptSharedPreferences
    implementation(libs.androidx.security.crypto)

    // Kakao Login API
    api(libs.v2.user)

    // Naver Maps
    api(libs.map.sdk)

    // jwt토큰 파싱해서 유저정보 가져오는 라이브러리들
    implementation(libs.jjwt.api)
    implementation(libs.jjwt.impl)
    implementation(libs.jjwt.jackson)

    // coil (gif)
    api(libs.coil.compose)
    api(libs.coil.gif)

    implementation(libs.guava)
    testImplementation(libs.junit)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.legacy.support.v13)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}