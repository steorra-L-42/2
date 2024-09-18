package com.mobi.testnavi.navi.car

import androidx.car.app.CarAppService
import androidx.car.app.validation.HostValidator
import com.mobi.testnavi.navi.car.MainCarSession

class MainCarAppService : CarAppService() {
    override fun createHostValidator() = HostValidator.ALLOW_ALL_HOSTS_VALIDATOR

    override fun onCreateSession() = MainCarSession()
}
