import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}
android {
    namespace = "com.kimnlee.mobipay"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kimnlee.mobipay"
        minSdk = 28
        maxSdk = 34
        targetSdk = 34
        versionCode = 20
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }
        val kakaoApiKey = localProperties.getProperty("KAKAO_API_KEY")

        manifestPlaceholders["KAKAO_API_KEY"] = kakaoApiKey
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(project(":common"))
    implementation(project(":features:auth"))
    implementation(project(":features:payment"))
    implementation(project(":features:cardmanagement"))
    implementation(project(":features:vehiclemanagement"))
    implementation(project(":features:memberinvitation"))
    implementation(project(":features:freedrive"))
    implementation(project(":features:firebase"))
    implementation(project(":features:notification"))

    implementation ("com.squareup.okhttp3:okhttp:4.9.3")

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.firebase.common.ktx)
    implementation(libs.androidx.core.splashscreen)
    // ShowMoreScreen에서 asyncImage 컴포즈를 사용하기 위해
    implementation(libs.coil.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}