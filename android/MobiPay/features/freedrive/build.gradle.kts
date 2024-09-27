plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.kimnlee.freedrive"
    compileSdk = 34

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java", "src/auto/java")
            res.srcDirs("src/main/res", "src/auto/res")
        }
    }
}

dependencies {

    implementation(project(":common"))

    implementation("androidx.startup:startup-runtime:1.1.1")
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")

    // Mapbox
    api("com.mapbox.navigation:ui-androidauto:0.22.0")
    api("com.mapbox.navigation:ui-dropin:2.20.2")
    api("com.mapbox.search:mapbox-search-android-ui:1.4.0")
    api("com.mapbox.search:mapbox-search-android:1.4.0")
    // mapbox navigation이랑 search sdk는 이렇게 호환되므로 버전 변경시 mapbox 깃헙에서 호환되는거 확인해야 함

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}