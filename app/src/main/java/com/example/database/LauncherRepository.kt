package com.example.database

import kotlinx.coroutines.flow.Flow

class LauncherRepository(private val launcherDao: LauncherDao) {

    val settingsFlow: Flow<LauncherSettings?> = launcherDao.getSettingsFlow()
    val whitelistedAppsFlow: Flow<List<WhitelistedApp>> = launcherDao.getAllWhitelistedAppsFlow()

    suspend fun ensureDefaultSettings(): LauncherSettings {
        val current = launcherDao.getSettingsDirect()
        if (current == null) {
            val defaults = LauncherSettings()
            launcherDao.insertOrUpdateSettings(defaults)
            return defaults
        }
        return current
    }

    suspend fun updateActiveProfile(profile: String) {
        val current = ensureDefaultSettings()
        launcherDao.insertOrUpdateSettings(current.copy(activeProfile = profile))
    }

    suspend fun updateParentPin(newPin: String) {
        val current = ensureDefaultSettings()
        launcherDao.insertOrUpdateSettings(current.copy(parentPin = newPin))
    }

    suspend fun updateProfileLabels(childLabel: String, teenLabel: String) {
        val current = ensureDefaultSettings()
        launcherDao.insertOrUpdateSettings(
            current.copy(
                childProfileLabel = childLabel,
                teenProfileLabel = teenLabel
            )
        )
    }

    suspend fun addWhitelistedApp(app: WhitelistedApp) {
        launcherDao.insertWhitelistedApp(app)
    }

    suspend fun addWhitelistedApps(apps: List<WhitelistedApp>) {
        launcherDao.insertWhitelistedApps(apps)
    }

    suspend fun deleteApp(app: WhitelistedApp) {
        launcherDao.deleteWhitelistedApp(app)
    }

    suspend fun deleteAppByPackage(packageName: String) {
        launcherDao.deleteWhitelistedAppByPackage(packageName)
    }

    suspend fun updateAppPermission(packageName: String, label: String, isChildAllowed: Boolean, isTeenAllowed: Boolean) {
        launcherDao.insertWhitelistedApp(
            WhitelistedApp(
                packageName = packageName,
                label = label,
                isChildAllowed = isChildAllowed,
                isTeenAllowed = isTeenAllowed
            )
        )
    }

    suspend fun updateScreenTimeEnabled(enabled: Boolean) {
        val current = ensureDefaultSettings()
        launcherDao.insertOrUpdateSettings(current.copy(screenTimeEnabled = enabled))
    }

    suspend fun updateScreenTimeLimit(minutes: Int) {
        val current = ensureDefaultSettings()
        launcherDao.insertOrUpdateSettings(current.copy(screenTimeLimitMinutes = minutes))
    }

    suspend fun updateScreenTimeUsedSeconds(seconds: Int) {
        val current = ensureDefaultSettings()
        launcherDao.insertOrUpdateSettings(current.copy(screenTimeUsedSeconds = seconds))
    }
}
