package com.example.ui

import android.content.Context
import android.media.AudioManager
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.models.Screen
import com.example.viewmodel.LauncherViewModel
import com.example.viewmodel.PinAction

@Composable
fun QuickSettingsScreen(viewModel: LauncherViewModel) {
    val context = LocalContext.current
    
    val isWifi by viewModel.isWifiEnabled.collectAsStateWithLifecycle()
    val isBluetooth by viewModel.isBluetoothEnabled.collectAsStateWithLifecycle()
    val isAirplane by viewModel.isAirplaneModeEnabled.collectAsStateWithLifecycle()
    val isMobileData by viewModel.isMobileDataEnabled.collectAsStateWithLifecycle()
    val ringerModeState by viewModel.ringerMode.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        LollipopAppBar(
            title = "Quick Settings",
            backgroundColor = LollipopColors.Primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Main Quick Settings Layout resembling the classic notification shade grid
        LollipopCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "CONNECTION & HARDWARE",
                color = LollipopColors.Primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // WIFI Row
            QuickSettingItem(
                title = "Wi-Fi Network",
                subtitle = if (isWifi) "Enabled" else "Disabled",
                icon = Icons.Default.Wifi,
                checked = isWifi,
                onCheckedChange = { viewModel.toggleWifi(context) }
            )

            HorizontalDivider(color = LollipopColors.Border, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 12.dp))

            // BLUETOOTH Row
            QuickSettingItem(
                title = "Bluetooth",
                subtitle = if (isBluetooth) "Enabled" else "Disabled",
                icon = Icons.Default.Bluetooth,
                checked = isBluetooth,
                onCheckedChange = { viewModel.toggleBluetooth(context) }
            )

            HorizontalDivider(color = LollipopColors.Border, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 12.dp))

            // AIRPLANE MODE Row
            QuickSettingItem(
                title = "Airplane Mode",
                subtitle = if (isAirplane) "On" else "Off",
                icon = Icons.Default.Flight,
                checked = isAirplane,
                onCheckedChange = { viewModel.toggleAirplaneMode(context) }
            )

            HorizontalDivider(color = LollipopColors.Border, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 12.dp))

            // MOBILE DATA Row
            QuickSettingItem(
                title = "Mobile Data",
                subtitle = if (isMobileData) "Enabled" else "Disabled",
                icon = Icons.Default.SignalCellularAlt,
                checked = isMobileData,
                onCheckedChange = { viewModel.toggleMobileData(context) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Silent / Audio Mode segment picker card
        LollipopCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "SOUND PROFILE & SILENT MODE",
                color = LollipopColors.Primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // NORMAL
                val isNormal = ringerModeState == AudioManager.RINGER_MODE_NORMAL
                SoundModePill(
                    label = "Normal",
                    icon = Icons.Default.VolumeUp,
                    isSelected = isNormal,
                    onClick = { viewModel.setRingerMode(context, AudioManager.RINGER_MODE_NORMAL) },
                    modifier = Modifier.weight(1f)
                )

                // VIBRATE
                val isVibrate = ringerModeState == AudioManager.RINGER_MODE_VIBRATE
                SoundModePill(
                    label = "Vibrate",
                    icon = Icons.Default.Vibration,
                    isSelected = isVibrate,
                    onClick = { viewModel.setRingerMode(context, AudioManager.RINGER_MODE_VIBRATE) },
                    modifier = Modifier.weight(1f)
                )

                // SILENT
                val isSilent = ringerModeState == AudioManager.RINGER_MODE_SILENT
                SoundModePill(
                    label = "Silent",
                    icon = Icons.Default.VolumeOff,
                    isSelected = isSilent,
                    onClick = { viewModel.setRingerMode(context, AudioManager.RINGER_MODE_SILENT) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Note: Toggling Silent Mode may require you to grant 'Do Not Disturb' Access permission if prompted.",
                fontSize = 11.sp,
                color = LollipopColors.TextSecondary,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SoundModePill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) LollipopColors.Indigo else Color(0xFFE0E0E0)
    val contentColor = if (isSelected) Color.White else LollipopColors.TextPrimary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .shadow(if (isSelected) 2.dp else 0.dp, shape = RoundedCornerShape(4.dp))
            .background(bgColor, shape = RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label.uppercase(),
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Composable
fun QuickSettingItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (checked) LollipopColors.Indigo.copy(alpha = 0.15f) else Color(0xFFE0E0E0),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked) LollipopColors.Indigo else LollipopColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = LollipopColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = subtitle,
                color = LollipopColors.TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.SansSerif
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = LollipopColors.Accent,
                checkedTrackColor = LollipopColors.Accent.copy(alpha = 0.5f),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}

@Composable
fun SettingsScreen(viewModel: LauncherViewModel) {
    val context = LocalContext.current
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    val whitelistedApps by viewModel.whitelistedAppsState.collectAsStateWithLifecycle()

    var activeSettingTab by remember { mutableStateOf("TIME") } // "TIME" or "APPS" or "PIN"
    var pinFieldQuery by remember { mutableStateOf("") }
    var appsSearchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(installedApps, appsSearchQuery) {
        if (appsSearchQuery.isBlank()) installedApps else installedApps.filter {
            it.label.contains(appsSearchQuery, ignoreCase = true) ||
                    it.packageName.contains(appsSearchQuery, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LollipopAppBar(
            title = "Parent Settings",
            backgroundColor = LollipopColors.Primary,
            actions = {
                IconButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        )

        // Sub-navigation header inside locked Settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LollipopColors.Paper)
                .border(1.dp, LollipopColors.Border)
        ) {
            SettingTabButton(
                title = "Screen Time",
                isSelected = activeSettingTab == "TIME",
                onClick = { activeSettingTab = "TIME" },
                modifier = Modifier.weight(1f)
            )
            SettingTabButton(
                title = "Allowed Apps",
                isSelected = activeSettingTab == "APPS",
                onClick = { activeSettingTab = "APPS" },
                modifier = Modifier.weight(1f)
            )
            SettingTabButton(
                title = "Settings PIN",
                isSelected = activeSettingTab == "PIN",
                onClick = { activeSettingTab = "PIN" },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeSettingTab) {
                "TIME" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Card 1: Toggle & Limit
                        LollipopCard {
                            Text(
                                text = "SCREEN TIME LIMIT SETUP",
                                color = LollipopColors.Primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Enable Daily Time Limit",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Blocks the apps grid once expired.",
                                        fontSize = 12.sp,
                                        color = LollipopColors.TextSecondary
                                    )
                                }
                                Switch(
                                    checked = settings.screenTimeEnabled,
                                    onCheckedChange = { viewModel.setScreenTimeEnabled(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = LollipopColors.Accent
                                    )
                                )
                            }

                            if (settings.screenTimeEnabled) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "SELECT DAILY LIMIT: ${settings.screenTimeLimitMinutes} MINUTES",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = LollipopColors.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // Quick Durations Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(15, 30, 60, 120).forEach { mins ->
                                        val isCurrent = settings.screenTimeLimitMinutes == mins
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(
                                                    if (isCurrent) LollipopColors.Accent else Color(0xFFE0E0E0),
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                                .clickable { viewModel.setScreenTimeLimitMinutes(mins) }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${mins}m",
                                                color = if (isCurrent) Color.White else LollipopColors.TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Precision adjusting
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Custom Limit Precision",
                                        fontSize = 13.sp,
                                        color = LollipopColors.TextSecondary
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = {
                                            if (settings.screenTimeLimitMinutes > 1) {
                                                viewModel.setScreenTimeLimitMinutes(settings.screenTimeLimitMinutes - 5)
                                            }
                                        }) {
                                            Icon(imageVector = Icons.Default.RemoveCircleOutline, contentDescription = "Minus 5m")
                                        }
                                        Text(
                                            text = "${settings.screenTimeLimitMinutes} min",
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                        IconButton(onClick = {
                                            viewModel.setScreenTimeLimitMinutes(settings.screenTimeLimitMinutes + 5)
                                        }) {
                                            Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = "Plus 5m")
                                        }
                                    }
                                }
                            }
                        }

                        // Card 2: Used state & Reset
                        LollipopCard {
                            Text(
                                text = "TODAY'S SCREEN TIME TRACKER",
                                color = LollipopColors.Primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            val limitSeconds = settings.screenTimeLimitMinutes * 60
                            val usedSeconds = settings.screenTimeUsedSeconds
                            val fraction = if (limitSeconds > 0) usedSeconds.toFloat() / limitSeconds else 0f
                            val pct = (fraction * 100).toInt().coerceIn(0, 100)

                            Text(
                                text = if (settings.screenTimeEnabled) {
                                    "Used ${usedSeconds / 60}m ${usedSeconds % 60}s of ${settings.screenTimeLimitMinutes}m limit"
                                } else {
                                    "Used ${usedSeconds / 60}m ${usedSeconds % 60}s (Limits currently disabled)"
                                },
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                color = LollipopColors.TextPrimary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Custom Flat Progress Bar resembling Lollipop style
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(6.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                                        .fillMaxHeight()
                                        .background(
                                            if (fraction >= 1f && settings.screenTimeEnabled) Color.Red else LollipopColors.Accent,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$pct% consumed",
                                fontSize = 12.sp,
                                color = if (fraction >= 1f && settings.screenTimeEnabled) Color.Red else LollipopColors.TextSecondary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                LollipopButton(
                                    onClick = { viewModel.resetScreenTimeUsed() },
                                    text = "Reset Usage",
                                    backgroundColor = LollipopColors.Indigo,
                                    modifier = Modifier.weight(1f)
                                )
                                LollipopButton(
                                    onClick = { viewModel.extendScreenTime(15) },
                                    text = "+15m bonus",
                                    backgroundColor = LollipopColors.Accent,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                "APPS" -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LollipopColors.Paper)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .shadow(1.dp)
                        ) {
                            LollipopTextField(
                                value = appsSearchQuery,
                                onValueChange = { appsSearchQuery = it },
                                label = "Search apps to whitelist"
                            )
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredApps, key = { it.packageName }) { appInfo ->
                                val dbApp = whitelistedApps.find { it.packageName == appInfo.packageName }
                                val isAllowed = dbApp?.isTeenAllowed ?: false

                                LollipopCard(elevation = 1.dp) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier.size(40.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            var iconDrawable by remember(appInfo.packageName) { mutableStateOf<android.graphics.drawable.Drawable?>(null) }
                                            LaunchedEffect(appInfo.packageName) {
                                                try {
                                                    iconDrawable = context.packageManager.getApplicationIcon(appInfo.packageName)
                                                } catch (e: Exception) {}
                                            }
                                            if (iconDrawable != null) {
                                                androidx.compose.ui.viewinterop.AndroidView(
                                                    factory = { ctx ->
                                                        android.widget.ImageView(ctx).apply {
                                                            scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
                                                        }
                                                    },
                                                    update = { iv -> iv.setImageDrawable(iconDrawable) },
                                                    modifier = Modifier.size(40.dp)
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .background(Color.LightGray, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(text = appInfo.label.take(1).uppercase())
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = appInfo.label,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = LollipopColors.TextPrimary
                                            )
                                            Text(
                                                text = appInfo.packageName,
                                                fontSize = 11.sp,
                                                color = LollipopColors.TextSecondary,
                                                maxLines = 1
                                            )
                                        }

                                        LollipopCheckbox(
                                            checked = isAllowed,
                                            onCheckedChange = { checked ->
                                                if (checked) {
                                                    viewModel.updateAppPermission(
                                                        packageName = appInfo.packageName,
                                                        label = appInfo.label,
                                                        isChildAllowed = true,
                                                        isTeenAllowed = true
                                                    )
                                                } else {
                                                    viewModel.removeAppFromWhitelist(appInfo.packageName)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                "PIN" -> {
                    var confirmPinField by remember { mutableStateOf("") }
                    var isPinVisible by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        LollipopCard {
                            Text(
                                text = "CHANGE PARENT PASSCODE",
                                color = LollipopColors.Primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Text(
                                text = "Current Settings Passcode is: ${settings.parentPin}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = LollipopColors.TextSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            LollipopTextField(
                                value = pinFieldQuery,
                                onValueChange = { if (it.length <= 8 && it.all { char -> char.isDigit() }) pinFieldQuery = it },
                                label = "Enter New PIN (Digits only)",
                                placeholder = "e.g. 5678",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                visualTransformation = if (isPinVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LollipopTextField(
                                value = confirmPinField,
                                onValueChange = { if (it.length <= 8 && it.all { char -> char.isDigit() }) confirmPinField = it },
                                label = "Confirm New PIN",
                                placeholder = "e.g. 5678",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                visualTransformation = if (isPinVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Show/Hide PIN checkbox toggle
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { isPinVisible = !isPinVisible }
                                    .padding(vertical = 4.dp)
                            ) {
                                LollipopCheckbox(
                                    checked = isPinVisible,
                                    onCheckedChange = { isPinVisible = it }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Show Passcode",
                                    fontSize = 14.sp,
                                    color = LollipopColors.TextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            val isMatching = pinFieldQuery == confirmPinField
                            val isLongEnough = pinFieldQuery.length >= 4

                            if (pinFieldQuery.isNotEmpty() && confirmPinField.isNotEmpty()) {
                                if (!isMatching) {
                                    Text(
                                        text = "PINs do not match",
                                        color = Color.Red,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                } else if (!isLongEnough) {
                                    Text(
                                        text = "PIN must be at least 4 digits",
                                        color = Color.Red,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                } else {
                                    Text(
                                        text = "Ready to update!",
                                        color = Color(0xFF4CAF50),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                            }

                            LollipopButton(
                                onClick = {
                                    if (isLongEnough && isMatching) {
                                        viewModel.changeParentPin(pinFieldQuery)
                                        pinFieldQuery = ""
                                        confirmPinField = ""
                                        Toast.makeText(context, "Passcode updated successfully!", Toast.LENGTH_SHORT).show()
                                    } else if (!isLongEnough) {
                                        Toast.makeText(context, "Passcode must be at least 4 digits!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Passcodes do not match!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = isLongEnough && isMatching,
                                text = "Update Passcode",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomBorderColor = if (isSelected) LollipopColors.Accent else Color.Transparent
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) LollipopColors.Indigo else LollipopColors.TextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(3.dp)
                .background(bottomBorderColor)
        )
    }
}

@Composable
fun ScreenTimeLockedScreen(viewModel: LauncherViewModel) {
    val context = LocalContext.current
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()
    
    var pinText by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121)) // Matte grey lock background
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.HourglassDisabled,
            contentDescription = null,
            tint = LollipopColors.Accent,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Time Limit Reached",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You've exceeded your daily limit of ${settings.screenTimeLimitMinutes} minutes.",
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 15.sp,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Enter Passcode Section
        Text(
            text = "ENTER PARENT PASSCODE TO UNLOCK",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bullet dots showing input length
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val length = pinText.length
            val dotsColor = if (inputError) Color.Red else LollipopColors.Accent
            for (i in 0 until 4) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            if (i < length) dotsColor else Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                )
            }
        }

        if (inputError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Incorrect Passcode. Try again.",
                color = Color.Red,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Flat circular keyboard keypad
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.width(260.dp)
        ) {
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("Clear", "0", "OK")
            )

            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEach { char ->
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    if (char == "OK" || char == "Clear") Color.Transparent else Color.White.copy(alpha = 0.1f)
                                )
                                .clickable {
                                    inputError = false
                                    when (char) {
                                        "Clear" -> {
                                            if (pinText.isNotEmpty()) {
                                                pinText = pinText.dropLast(1)
                                            }
                                        }
                                        "OK" -> {
                                            if (pinText == settings.parentPin) {
                                                // Grant 15m extend time
                                                viewModel.extendScreenTime(30)
                                                Toast.makeText(context, "Bypassed! 30 minutes added.", Toast.LENGTH_SHORT).show()
                                                pinText = ""
                                            } else {
                                                inputError = true
                                                pinText = ""
                                            }
                                        }
                                        else -> {
                                            if (pinText.length < 4) {
                                                pinText += char
                                                if (pinText.length == 4) {
                                                    // Automatically verify PIN when 4 digits are input
                                                    if (pinText == settings.parentPin) {
                                                        viewModel.extendScreenTime(30)
                                                        Toast.makeText(context, "Bypassed! 30 minutes added.", Toast.LENGTH_SHORT).show()
                                                        pinText = ""
                                                    } else {
                                                        inputError = true
                                                        pinText = ""
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (char == "Clear") {
                                Icon(imageVector = Icons.Default.Backspace, contentDescription = "Clear", tint = Color.White.copy(alpha = 0.6f))
                            } else if (char == "OK") {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "OK", tint = LollipopColors.Accent)
                            } else {
                                Text(
                                    text = char,
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
