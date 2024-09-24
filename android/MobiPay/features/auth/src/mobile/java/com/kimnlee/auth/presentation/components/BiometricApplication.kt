package com.kimnlee.auth.presentation.components

import android.app.Application
import androidx.activity.ComponentActivity

class BiometricApplication : Application() {
    private var currentActivity: ComponentActivity? = null

    fun setCurrentActivity(activity: ComponentActivity) {
        currentActivity = activity
    }
ㅡ
    companion object {
        lateinit var instance: BiometricApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}