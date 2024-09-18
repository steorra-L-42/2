package com.kimnlee.freedrive.presentation.screen

import androidx.car.app.CarAppService
import androidx.car.app.validation.HostValidator
import com.kimnlee.freedrive.presentation.screen.MainCarSession

class MainCarAppService : CarAppService() {
    override fun createHostValidator() = HostValidator.ALLOW_ALL_HOSTS_VALIDATOR

    override fun onCreateSession() = MainCarSession()
}
