package com.kimnlee.common.components
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.kimnlee.common.ui.theme.Typography

@Composable
fun MobiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = Typography, // Apply custom typography as before
        content = content
    )
}