package dev.fg.buildyourownx.libs.navigation_visualizer.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle

val DarkSurfaceColor = Color(0xFF1E1E1E)
val EmeraldGreen = Color(0xFF10B981) // Resumed
val AmberYellow = Color(0xFFF59E0B) // Started
val MutedGray = Color(0xFF6B7280) // Destroyed/Historical
val LightGrayText = Color(0xFFD1D5DB)

fun getLifecycleColor(state: Lifecycle.State): Color {
    return when (state) {
        Lifecycle.State.RESUMED -> EmeraldGreen
        Lifecycle.State.STARTED -> AmberYellow
        Lifecycle.State.CREATED -> Color(0xFF3B82F6) // Blue
        else -> MutedGray
    }
}