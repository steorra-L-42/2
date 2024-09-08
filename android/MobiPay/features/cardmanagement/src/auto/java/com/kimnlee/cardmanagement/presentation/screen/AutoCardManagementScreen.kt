package com.kimnlee.cardmanagement.presentation.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.kimnlee.cardmanagement.navigation.CardManagementAutoNavDestination
import com.kimnlee.cardmanagement.navigation.CardManagementAutoNavigation

class AutoCardManagementScreen(carContext: CarContext) : Screen(carContext) {
    private val navigation = CardManagementAutoNavigation(carContext)

    override fun onGetTemplate(): Template {
        return PaneTemplate.Builder(
            Pane.Builder()
                .addRow(
                    Row.Builder()
                        .setTitle("카드 관리")
                        .addText("카드 관리 메인 화면입니다.")
                        .build()
                )
                .addAction(
                    Action.Builder()
                        .setTitle("상세 화면으로 이동")
                        .setOnClickListener {
                            screenManager.push(navigation.navigate(CardManagementAutoNavDestination.CardManagementDetail))
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