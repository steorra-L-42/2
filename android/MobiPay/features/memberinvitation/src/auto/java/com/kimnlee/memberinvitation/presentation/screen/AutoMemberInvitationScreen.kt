package com.kimnlee.memberinvitation.presentation.screen

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.kimnlee.memberinvitation.navigation.MemberInvitationAutoNavDestination
import com.kimnlee.memberinvitation.navigation.MemberInvitationAutoNavigation

class AutoMemberInvitationScreen(carContext: CarContext) : Screen(carContext) {
    private val navigation = MemberInvitationAutoNavigation(carContext)

    override fun onGetTemplate(): Template {
        return PaneTemplate.Builder(
            Pane.Builder()
                .addRow(
                    Row.Builder()
                        .setTitle("멤버 초대")
                        .addText("멤버 초대 메인 화면입니다.")
                        .build()
                )
                .addAction(
                    Action.Builder()
                        .setTitle("상세 화면으로 이동")
                        .setOnClickListener {
                            screenManager.push(navigation.navigate(
                                MemberInvitationAutoNavDestination.MemberInvitationDetail))
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