package com.kimnlee.common.utils

import android.util.Log

private const val TAG = "AAFocusManager"
object AAFocusManager {
    @Volatile
    private var activeScreenCount = 0
    @Volatile
    private var aaConnected = false

    val isAppInFocus: Boolean
        get() = activeScreenCount > 0


    val isAAConnected: Boolean
        get() = aaConnected


    @Synchronized
    fun screenResumed() {
        activeScreenCount++
        Log.d(TAG, "Screen resumed, active screens: $activeScreenCount")
    }

    @Synchronized
    fun screenPaused() {
        if (activeScreenCount > 0) {
            activeScreenCount--
        }
        Log.d(TAG, "Screen paused, active screens: $activeScreenCount")
    }

    @Synchronized
    fun aaConnected(){
        Log.d(TAG, "aaConnected: 안오토 연결됐네")
        aaConnected = true
    }

    @Synchronized
    fun aaDisconnected(){
        Log.d(TAG, "aaConnected: 안오토 끊었네")
        aaConnected = false
    }

}