package com.kimnlee.vehiclemanagement.presentation.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.kimnlee.vehiclemanagement.navigation.VehicleManagementAutoNavDestination
import com.kimnlee.vehiclemanagement.navigation.VehicleManagementAutoNavigation

class AutoVehicleManagementScreen(carContext: CarContext) : Screen(carContext) {
    private val navigation = VehicleManagementAutoNavigation(carContext)

    override fun onGetTemplate(): Template {
        return PaneTemplate.Builder(
            Pane.Builder()
                .addRow(
                    Row.Builder()
                        .setTitle("차량 관리")
                        .addText("차량 관리 메인 화면입니다.")
                        .build()
                )
                .addAction(
                    Action.Builder()
                        .setTitle("상세 화면으로 이동")
                        .setOnClickListener {
                            screenManager.push(navigation.navigate(
                                VehicleManagementAutoNavDestination.VehicleManagementDetail))
                        }
                        .build()
                )
                .addAction(
                    Action.Builder()
                        .setTitle("홈으로 돌아가기")
                        .setOnClickListener {
                            screenManager.popToRoot()
                        }
                        .build()
                )
                .build()
        )
            .setHeaderAction(Action.APP_ICON)
            .build()
    }
}