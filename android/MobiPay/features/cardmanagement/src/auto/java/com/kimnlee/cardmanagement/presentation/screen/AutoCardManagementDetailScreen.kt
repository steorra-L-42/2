package com.kimnlee.cardmanagement.presentation.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template

class AutoCardManagementDetailScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        return PaneTemplate.Builder(
            Pane.Builder()
                .addRow(
                    Row.Builder()
                        .setTitle("카드 관리 상세")
                        .addText("카드 관리 상세 정보를 여기에 표시합니다.")
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