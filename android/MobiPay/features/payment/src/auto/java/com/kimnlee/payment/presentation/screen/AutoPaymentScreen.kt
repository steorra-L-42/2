package com.kimnlee.payment.presentation.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.kimnlee.payment.navigation.PaymentAutoNavDestination
import com.kimnlee.payment.navigation.PaymentAutoNavigation

class AutoPaymentScreen(carContext: CarContext) : Screen(carContext) {
    private val navigation = PaymentAutoNavigation(carContext)

    override fun onGetTemplate(): Template {
        return PaneTemplate.Builder(
            Pane.Builder()
                .addRow(
                    Row.Builder()
                        .setTitle("결제")
                        .addText("결제 메인 화면입니다.")
                        .build()
                )
                .addAction(
                    Action.Builder()
                        .setTitle("상세 화면으로 이동")
                        .setOnClickListener {
                            screenManager.push(navigation.navigate(PaymentAutoNavDestination.PaymentDetail))
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