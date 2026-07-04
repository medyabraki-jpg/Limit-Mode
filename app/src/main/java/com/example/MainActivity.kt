package com.example

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.database.AppDatabase
import com.example.database.LauncherRepository
import com.example.database.WhitelistedApp
import com.example.models.ActiveProfile
import com.example.models.Screen
import com.example.viewmodel.LauncherViewModel
import com.example.viewmodel.PinAction
import com.example.ui.LollipopColors
import com.example.ui.LollipopStatusBar
import com.example.ui.LollipopAppBar
import com.example.ui.LollipopCard
import com.example.ui.LollipopButton
import com.example.ui.LollipopFlatButton
import com.example.ui.LollipopTextField
import com.example.ui.LollipopCheckbox
import com.example.ui.QuickSettingsScreen
import com.example.ui.SettingsScreen
import com.example.ui.ScreenTimeLockedScreen
import com.example.ui.PinVerificationDialog
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            collapseNotifications()
        }
    }

    private fun collapseNotifications() {
        try {
            @Suppress("DEPRECATION")
            val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            sendBroadcast(closeIntent)
        } catch (e: Exception) {
            // ignore
        }
        try {
            @Suppress("WrongConstant")
            val statusBarService = getSystemService("statusbar")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val collapseMethod = statusBarManager.getMethod("collapsePanels")
            collapseMethod.invoke(statusBarService)
        } catch (e: Exception) {
            // ignore
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Room database and repository setup
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = LauncherRepository(database.launcherDao())

        val factory = LauncherViewModelFactory(application, repository)

        setContent {
            val viewModel: LauncherViewModel = viewModel(factory = factory)
            val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
            val pinActionNeedVerification by viewModel.showPinDialogForAction.collectAsStateWithLifecycle()
            val settings by viewModel.settingsState.collectAsStateWithLifecycle()

            val currentTab = when (currentScreen) {
                is Screen.Home -> "HOME"
                is Screen.QuickSettings -> "QUICK_SETTINGS"
                is Screen.Settings -> "SETTINGS"
                else -> "HOME"
            }

            val isTimeExceeded = settings.screenTimeEnabled && settings.screenTimeUsedSeconds >= settings.screenTimeLimitMinutes * 60

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LollipopColors.Background)
                    .safeDrawingPadding()
            ) {
                // High-fidelity Lollipop status bar
                LollipopStatusBar(backgroundColor = LollipopColors.PrimaryDark)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (isTimeExceeded && currentScreen !is Screen.Settings) {
                        // Display full block lock screen when screen time limit is reached
                        ScreenTimeLockedScreen(viewModel = viewModel)
                    } else {
                        // Regular navigation routing
                        when (currentScreen) {
                            is Screen.Home -> {
                                HomeScreen(viewModel = viewModel)
                            }
                            is Screen.QuickSettings -> {
                                QuickSettingsScreen(viewModel = viewModel)
                            }
                            is Screen.Settings -> {
                                SettingsScreen(viewModel = viewModel)
                            }
                            else -> {
                                HomeScreen(viewModel = viewModel)
                            }
                        }
                    }

                    // PIN Dialog layer for accessing locked views
                    pinActionNeedVerification?.let { action ->
                        PinVerificationDialog(
                            action = action,
                            onDismiss = { viewModel.dismissPinDialog() },
                            onVerify = { pin ->
                                val success = viewModel.verifyPinAndExecute(pin, action)
                                if (!success) {
                                    Toast.makeText(this@MainActivity, "Incorrect PIN", Toast.LENGTH_SHORT).show()
                                }
                                success
                            }
                        )
                    }
                }

                // Bottom Navigation Dock with 3 clean tabs (Home, Quick Settings, Parent Settings)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(LollipopColors.Paper)
                        .border(width = 1.dp, color = LollipopColors.Border),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // HOME GRID Tab
                    val isHomeSelected = currentTab == "HOME"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo(Screen.Home) }
                            .padding(vertical = 6.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home Grid",
                            tint = if (isHomeSelected) LollipopColors.Indigo else LollipopColors.TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "HOME GRID",
                            color = if (isHomeSelected) LollipopColors.Indigo else LollipopColors.TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }

                    // QUICK SETTINGS Tab
                    val isQuickSelected = currentTab == "QUICK_SETTINGS"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo(Screen.QuickSettings) }
                            .padding(vertical = 6.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Quick Settings",
                            tint = if (isQuickSelected) LollipopColors.Indigo else LollipopColors.TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "QUICK SETTINGS",
                            color = if (isQuickSelected) LollipopColors.Indigo else LollipopColors.TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }

                    // PARENT SETTINGS Tab (Requires Passcode)
                    val isSettingsSelected = currentTab == "SETTINGS"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (isSettingsSelected) {
                                    // Already in settings
                                } else {
                                    viewModel.requestPinVerification(PinAction.OpenSettings)
                                }
                            }
                            .padding(vertical = 6.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Parent Settings",
                            tint = if (isSettingsSelected) LollipopColors.Indigo else LollipopColors.TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "PARENT PIN",
                            color = if (isSettingsSelected) LollipopColors.Indigo else LollipopColors.TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }
        }
    }
}

// Factory class for LauncherViewModel
class LauncherViewModelFactory(
    private val application: Application,
    private val repository: LauncherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LauncherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LauncherViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun AppIconImage(
    packageName: String,
    packageManager: PackageManager,
    modifier: Modifier = Modifier
) {
    var iconDrawable by remember(packageName) { mutableStateOf<android.graphics.drawable.Drawable?>(null) }
    
    LaunchedEffect(packageName) {
        try {
            iconDrawable = packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            // fallback
        }
    }
    
    if (iconDrawable != null) {
        androidx.compose.ui.viewinterop.AndroidView(
            factory = { context ->
                android.widget.ImageView(context).apply {
                    scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
                }
            },
            update = { imageView ->
                imageView.setImageDrawable(iconDrawable)
            },
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier
                .background(LollipopColors.Border, shape = RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = packageName.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun HomeScreen(viewModel: LauncherViewModel) {
    val context = LocalContext.current
    val whitelistedApps by viewModel.whitelistedAppsState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()

    // Single unified profile allowed apps (represented as isTeenAllowed)
    val allowedApps = remember(whitelistedApps) {
        whitelistedApps.filter { it.isTeenAllowed }
    }

    val filteredApps = remember(allowedApps, searchQuery) {
        if (searchQuery.isBlank()) {
            allowedApps
        } else {
            allowedApps.filter {
                it.label.contains(searchQuery, ignoreCase = true) ||
                        it.packageName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    var currentTimeString by remember { mutableStateOf("") }
    var currentDateString by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTimeString = timeFormat.format(now)
            currentDateString = dateFormat.format(now)
            delay(1000)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Massive elegant digital clock and launcher branding header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LollipopColors.Primary)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .shadow(4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = currentTimeString,
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = currentDateString,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center
                )

                if (settings.screenTimeEnabled) {
                    val limitSecs = settings.screenTimeLimitMinutes * 60
                    val usedSecs = settings.screenTimeUsedSeconds
                    val remSecs = (limitSecs - usedSecs).coerceAtLeast(0)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = "Time Left",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TIME LEFT: ${remSecs / 60}m ${remSecs % 60}s",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }
        }

        // Active app search and grid display
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LollipopCard(
                elevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                LollipopTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    label = "Search Whitelisted Apps",
                    placeholder = "Type app name to filter..."
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (allowedApps.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        tint = LollipopColors.TextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Home Grid is empty.",
                        color = LollipopColors.TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Access Settings in the bottom bar with PIN to whitelist applications.",
                        color = LollipopColors.TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else if (filteredApps.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = LollipopColors.TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No apps match your search.",
                        color = LollipopColors.TextSecondary,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(88.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredApps, key = { it.packageName }) { app ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .shadow(2.dp, shape = RoundedCornerShape(2.dp))
                                .background(LollipopColors.Paper, shape = RoundedCornerShape(2.dp))
                                .clickable {
                                    viewModel.launchApp(context, app.packageName)
                                }
                                .padding(12.dp)
                                .fillMaxWidth()
                        ) {
                            AppIconImage(
                                packageName = app.packageName,
                                packageManager = context.packageManager,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = app.label,
                                color = LollipopColors.TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
