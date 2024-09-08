package com.kimnlee.vehiclemanagement.presentation.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template

class AutoVehicleManagementDetailScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        return PaneTemplate.Builder(
            Pane.Builder()
                .addRow(
                    Row.Builder()
                        .setTitle("차량 관리 상세")
                        .addText("차량 관리 상세 정보를 여기에 표시합니다.")
                        .build()
                )
                .addAction(
                    Action.Builder()
                        .setTitle("뒤로 가기")
                        .setOnClickListener {
                            screenManager.pop()
                        }
                        .build()
                )
                .build()
        )
            .setHeaderAction(Action.BACK)
            .build()
    }
}