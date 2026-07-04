package com.example.models

data class InstalledAppInfo(
    val packageName: String,
    val label: String,
    val isSystemApp: Boolean
)

enum class ActiveProfile {
    CHILD,
    TEEN_ADULT
}

sealed class Screen {
    object Home : Screen()
    object QuickSettings : Screen()
    object Settings : Screen()
    object AppSelector : Screen()
}
