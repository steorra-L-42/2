package com.kimnlee.common.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class BottomNavItem(
    val route: String,
    @DrawableRes val iconResId: Int,
    @DrawableRes val filledIconResId: Int
)