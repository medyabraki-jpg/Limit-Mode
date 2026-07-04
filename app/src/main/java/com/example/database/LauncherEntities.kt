package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "launcher_settings")
data class LauncherSettings(
    @PrimaryKey val id: Int = 1,
    val activeProfile: String = "TEEN_ADULT", // "CHILD" or "TEEN_ADULT"
    val parentPin: String = "1234",
    val childProfileLabel: String = "Child Profile (8-11)",
    val teenProfileLabel: String = "Teen & Adult (12+)",
    val screenTimeEnabled: Boolean = false,
    val screenTimeLimitMinutes: Int = 30,
    val screenTimeUsedSeconds: Int = 0
)

@Entity(tableName = "whitelisted_apps")
data class WhitelistedApp(
    @PrimaryKey val packageName: String,
    val label: String,
    val isChildAllowed: Boolean = false,
    val isTeenAllowed: Boolean = true
)
