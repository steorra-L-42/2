package com.kimnlee.mobipay

import android.content.Intent
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import com.kimnlee.freedrive.presentation.screen.MainCarSession
import com.kimnlee.mobipay.presentation.screen.AutoHomeScreen

class MobiPayCarAppService : CarAppService() {
    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }


    override fun onCreateSession() = MainCarSession()

    /*
     * 기존 Screen 메뉴를 활성화 하려면 주석 풀면 됨
    override fun onCreateSession(): Session {
        return object : Session() {
            override fun onCreateScreen(intent: Intent): Screen {
                return AutoHomeScreen(carContext)
            }
        }
    }
    */
}