package com.kimnlee.mobipay.ui

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Template
import androidx.car.app.model.Row
import com.kimnlee.mobipay.navigation.AutoNavDestination
import com.kimnlee.mobipay.navigation.AutoNavigation

class AutoHomeScreen(carContext: CarContext) : Screen(carContext) {
    private val navigation = AutoNavigation(carContext)

    override fun onGetTemplate(): Template {
        val list = ItemList.Builder()
            .addItem(
                Row.Builder()
                    .setTitle("결제 하기")
                    .setOnClickListener {
                        screenManager.push(navigation.navigate(AutoNavDestination.AutoPaymentScreen))
                    }
                    .build()
            )
            .addItem(
                Row.Builder()
                    .setTitle("카드 관리")
                    .setOnClickListener {
                        screenManager.push(navigation.navigate(AutoNavDestination.AutoCardmanagementScreen))
                    }
                    .build()
            )
            .addItem(
                Row.Builder()
                    .setTitle("나의 차 관리")
                    .setOnClickListener {
                        screenManager.push(navigation.navigate(AutoNavDestination.AutoVehiclemanagementScreen))
                    }
                    .build()
            )
            .addItem(
                Row.Builder()
                    .setTitle("멤버 초대")
                    .setOnClickListener {
                        screenManager.push(navigation.navigate(AutoNavDestination.AutoMemberInvitationScreen))
                    }
                    .build()
            )
            .build()

        return ListTemplate.Builder()
            .setTitle("MobiPay Auto Home")
            .setSingleList(list)
            .build()
    }
}