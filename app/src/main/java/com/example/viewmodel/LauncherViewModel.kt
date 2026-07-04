package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.LauncherSettings
import com.example.database.LauncherRepository
import com.example.database.WhitelistedApp
import com.example.models.ActiveProfile
import com.example.models.InstalledAppInfo
import com.example.models.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LauncherViewModel(
    application: Application,
    private val repository: LauncherRepository
) : AndroidViewModel(application) {

    // Live Settings & Whitelisted apps from Room
    val settingsState: StateFlow<LauncherSettings> = repository.settingsFlow
        .map { it ?: LauncherSettings() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LauncherSettings())

    val whitelistedAppsState: StateFlow<List<WhitelistedApp>> = repository.whitelistedAppsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI screen state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Loaded installed apps list
    private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps.asStateFlow()

    // PIN dialog state
    private val _showPinDialogForAction = MutableStateFlow<PinAction?>(null)
    val showPinDialogForAction: StateFlow<PinAction?> = _showPinDialogForAction.asStateFlow()

    // Search query for launcher home / app whitelist selector
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Quick Settings Hardware States
    val isWifiEnabled = MutableStateFlow(false)
    val isBluetoothEnabled = MutableStateFlow(false)
    val isAirplaneModeEnabled = MutableStateFlow(false)
    val isMobileDataEnabled = MutableStateFlow(false)
    val ringerMode = MutableStateFlow(2) // 2 = AudioManager.RINGER_MODE_NORMAL

    init {
        viewModelScope.launch {
            repository.ensureDefaultSettings()
            refreshInstalledApps()
        }

        // Screen Time clock ticker loop
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val settings = settingsState.value
                if (settings.screenTimeEnabled) {
                    val used = settings.screenTimeUsedSeconds
                    val limit = settings.screenTimeLimitMinutes * 60
                    if (used < limit) {
                        repository.updateScreenTimeUsedSeconds(used + 1)
                    }
                }
            }
        }

        // Quick Settings hardware polling loop (updates state in real-time)
        viewModelScope.launch {
            val wifiManager = application.getSystemService(Context.WIFI_SERVICE) as? android.net.wifi.WifiManager
            val audioManager = application.getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
            val telephonyManager = application.getSystemService(Context.TELEPHONY_SERVICE) as? android.telephony.TelephonyManager

            while (true) {
                try {
                    isWifiEnabled.value = wifiManager?.isWifiEnabled ?: false
                } catch (e: Exception) {}

                try {
                    val adapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
                    isBluetoothEnabled.value = adapter?.isEnabled ?: false
                } catch (e: Exception) {}

                try {
                    isAirplaneModeEnabled.value = android.provider.Settings.Global.getInt(
                        application.contentResolver,
                        android.provider.Settings.Global.AIRPLANE_MODE_ON,
                        0
                    ) != 0
                } catch (e: Exception) {}

                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        isMobileDataEnabled.value = telephonyManager?.isDataEnabled ?: false
                    } else {
                        @Suppress("DEPRECATION")
                        val connManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
                        val mobileInfo = connManager?.getNetworkInfo(android.net.ConnectivityManager.TYPE_MOBILE)
                        isMobileDataEnabled.value = mobileInfo?.isConnected ?: false
                    }
                } catch (e: Exception) {}

                try {
                    ringerMode.value = audioManager?.ringerMode ?: 2
                } catch (e: Exception) {}

                delay(2000)
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refreshInstalledApps() {
        val pm = getApplication<Application>().packageManager
        val myPkg = getApplication<Application>().packageName
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
        val list = resolveInfos.mapNotNull { info ->
            val pkgName = info.activityInfo.packageName
            if (pkgName == myPkg) return@mapNotNull null
            val label = info.loadLabel(pm).toString()
            val isSystem = (info.activityInfo.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
            InstalledAppInfo(packageName = pkgName, label = label, isSystemApp = isSystem)
        }.distinctBy { it.packageName }.sortedBy { it.label.uppercase() }
        
        _installedApps.value = list
    }

    fun requestPinVerification(action: PinAction) {
        _showPinDialogForAction.value = action
    }

    fun dismissPinDialog() {
        _showPinDialogForAction.value = null
    }

    fun verifyPinAndExecute(pin: String, action: PinAction): Boolean {
        val correctPin = settingsState.value.parentPin
        if (pin == correctPin) {
            _showPinDialogForAction.value = null
            when (action) {
                is PinAction.SwitchProfile -> {
                    viewModelScope.launch {
                        repository.updateActiveProfile(action.profile.name)
                    }
                }
                is PinAction.OpenSettings -> {
                    _currentScreen.value = Screen.Settings
                }
                is PinAction.OpenAppSelector -> {
                    _currentScreen.value = Screen.AppSelector
                }
            }
            return true
        }
        return false
    }

    fun changeParentPin(newPin: String) {
        if (newPin.length >= 4) {
            viewModelScope.launch {
                repository.updateParentPin(newPin)
            }
        }
    }

    fun updateAppPermission(packageName: String, label: String, isChildAllowed: Boolean, isTeenAllowed: Boolean) {
        viewModelScope.launch {
            repository.updateAppPermission(packageName, label, isChildAllowed, isTeenAllowed)
        }
    }

    fun removeAppFromWhitelist(packageName: String) {
        viewModelScope.launch {
            repository.deleteAppByPackage(packageName)
        }
    }

    fun launchApp(context: Context, packageName: String) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Cannot launch this application", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Screen Time Setter Logic
    fun setScreenTimeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateScreenTimeEnabled(enabled)
        }
    }

    fun setScreenTimeLimitMinutes(minutes: Int) {
        viewModelScope.launch {
            repository.updateScreenTimeLimit(minutes)
        }
    }

    fun resetScreenTimeUsed() {
        viewModelScope.launch {
            repository.updateScreenTimeUsedSeconds(0)
        }
    }

    fun extendScreenTime(minutes: Int) {
        viewModelScope.launch {
            val current = settingsState.value.screenTimeUsedSeconds
            val secondsToSubtract = minutes * 60
            val newVal = if (current > secondsToSubtract) current - secondsToSubtract else 0
            repository.updateScreenTimeUsedSeconds(newVal)
        }
    }

    // Quick Settings hardware control methods
    fun toggleWifi(context: Context) {
        val wifiManager = getApplication<Application>().getSystemService(Context.WIFI_SERVICE) as? android.net.wifi.WifiManager
        val current = isWifiEnabled.value
        try {
            @Suppress("DEPRECATION")
            wifiManager?.isWifiEnabled = !current
            isWifiEnabled.value = !current
        } catch (e: Exception) {}
        
        try {
            val intent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {}
    }

    fun toggleBluetooth(context: Context) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {}
    }

    fun toggleAirplaneMode(context: Context) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {}
    }

    fun toggleMobileData(context: Context) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {}
    }

    fun setRingerMode(context: Context, mode: Int) {
        val audioManager = getApplication<Application>().getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
        val hasPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager?.isNotificationPolicyAccessGranted == true
        } else {
            true
        }
        if (hasPermission) {
            audioManager?.ringerMode = mode
            ringerMode.value = mode
        } else {
            Toast.makeText(context, "Grant Notification Policy Access to change ringer mode", Toast.LENGTH_LONG).show()
            try {
                val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {}
        }
    }
}

sealed class PinAction {
    data class SwitchProfile(val profile: ActiveProfile) : PinAction()
    object OpenSettings : PinAction()
    object OpenAppSelector : PinAction()
}
