package com.example.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LauncherDao {
    @Query("SELECT * FROM launcher_settings WHERE id = 1")
    fun getSettingsFlow(): Flow<LauncherSettings?>

    @Query("SELECT * FROM launcher_settings WHERE id = 1")
    suspend fun getSettingsDirect(): LauncherSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: LauncherSettings)

    @Query("SELECT * FROM whitelisted_apps ORDER BY label ASC")
    fun getAllWhitelistedAppsFlow(): Flow<List<WhitelistedApp>>

    @Query("SELECT * FROM whitelisted_apps ORDER BY label ASC")
    suspend fun getAllWhitelistedAppsDirect(): List<WhitelistedApp>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWhitelistedApp(app: WhitelistedApp)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWhitelistedApps(apps: List<WhitelistedApp>)

    @Delete
    suspend fun deleteWhitelistedApp(app: WhitelistedApp)

    @Query("DELETE FROM whitelisted_apps WHERE packageName = :packageName")
    suspend fun deleteWhitelistedAppByPackage(packageName: String)
}
