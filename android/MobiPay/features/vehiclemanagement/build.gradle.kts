plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.kimnlee.vehiclemanagement"
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
            java.srcDirs("src/main/java", "src/mobile/java", "src/auto/java")
            res.srcDirs("src/main/res", "src/mobile/res", "src/auto/res")
        }
    }
}

dependencies {

    implementation(project(":common"))
    implementation(project(":features:cardmanagement"))
    implementation(project(":features:memberinvitation"))

    implementation(libs.guava)
    implementation(libs.text.recognition)
    implementation(project(":features:cardmanagement"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}