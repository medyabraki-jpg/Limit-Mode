package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.PinAction
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import kotlinx.coroutines.delay

// Classic Lollipop Palette (Material 1 - 2014)
object LollipopColors {
    val Primary = Color(0xFF3F51B5)      // Indigo 500 (Classic Lollipop primary)
    val PrimaryDark = Color(0xFF303F9F)  // Indigo 700 (Dark theme primary)
    val Accent = Color(0xFFFF4081)       // Pink Accent 500 (Design HTML FAB color)
    val AccentDark = Color(0xFFF50057)   // Pink Accent 700
    val Indigo = Color(0xFF3F51B5)       // Indigo 500
    val IndigoDark = Color(0xFF303F9F)   // Indigo 700
    val Background = Color(0xFFEEEEEE)   // Grey 200 (Classic Lollipop light background)
    val Paper = Color(0xFFFFFFFF)        // Card/Paper White
    val TextPrimary = Color(0xFF212121)  // Dark grey text 87%
    val TextSecondary = Color(0xFF757575)// Medium grey text 54%
    val Border = Color(0xFFE0E0E0)       // Grey 300
}

@Composable
fun LollipopStatusBar(
    backgroundColor: Color = LollipopColors.PrimaryDark
) {
    var currentTimeString by remember { mutableStateOf("10:45 AM") }
    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        while (true) {
            currentTimeString = sdf.format(Date())
            delay(1000)
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(backgroundColor)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentTimeString,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
            Icon(
                imageVector = Icons.Default.SignalCellularAlt,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
            Icon(
                imageVector = Icons.Default.BatteryChargingFull,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
fun ProfileSwitcherBanner(
    profileName: String,
    onSwitchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LollipopColors.Paper)
            .border(width = 1.dp, color = Color(0xFFE0E0E0)) // border-b style
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "ACTIVE PROFILE",
                color = LollipopColors.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = profileName,
                color = LollipopColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.SansSerif
            )
        }
        
        // Switch Pill
        Row(
            modifier = Modifier
                .background(color = Color(0xFFE8EAF6), shape = RoundedCornerShape(20.dp))
                .clickable { onSwitchClick() }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = LollipopColors.Indigo,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "SWITCH",
                color = LollipopColors.Indigo,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun LollipopBottomNavigation(
    currentTab: String, // "HOME", "DRAWER", "SETTINGS"
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(LollipopColors.Paper.copy(alpha = 0.95f))
            .border(width = 1.dp, color = Color(0xFFE0E0E0)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        // HOME Tab
        val isHomeSelected = currentTab == "HOME"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false)
                ) { onTabSelected("HOME") }
                .padding(vertical = 6.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = if (isHomeSelected) LollipopColors.Indigo else LollipopColors.TextSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "HOME",
                color = if (isHomeSelected) LollipopColors.Indigo else LollipopColors.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // DRAWER Tab
        val isDrawerSelected = currentTab == "DRAWER"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false)
                ) { onTabSelected("DRAWER") }
                .padding(vertical = 6.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = "Drawer",
                tint = if (isDrawerSelected) LollipopColors.Indigo else LollipopColors.TextSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "DRAWER",
                color = if (isDrawerSelected) LollipopColors.Indigo else LollipopColors.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // SETTINGS Tab
        val isSettingsSelected = currentTab == "SETTINGS"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false)
                ) { onTabSelected("SETTINGS") }
                .padding(vertical = 6.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = if (isSettingsSelected) LollipopColors.Indigo else LollipopColors.TextSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "SETTINGS",
                color = if (isSettingsSelected) LollipopColors.Indigo else LollipopColors.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LollipopFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Add",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(12.dp, shape = RoundedCornerShape(28.dp))
            .background(LollipopColors.Accent, shape = RoundedCornerShape(28.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
fun LollipopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LollipopColors.Primary,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(elevation = 4.dp, shape = RectangleShape)
            .background(backgroundColor)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (navigationIcon != null) {
            navigationIcon()
            Spacer(modifier = Modifier.width(16.dp))
        } else {
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.weight(1f)
        )
        
        if (actions != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }
    }
}

@Composable
fun LollipopCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 2.dp,
    backgroundColor: Color = LollipopColors.Paper,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(elevation = elevation, shape = RoundedCornerShape(2.dp))
            .background(color = backgroundColor, shape = RoundedCornerShape(2.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun LollipopButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LollipopColors.Indigo,
    textColor: Color = Color.White,
    elevation: Dp = 2.dp,
    enabled: Boolean = true
) {
    val buttonBg = if (enabled) backgroundColor else LollipopColors.Border
    val buttonElevation = if (enabled) elevation else 0.dp
    
    Box(
        modifier = modifier
            .shadow(elevation = buttonElevation, shape = RoundedCornerShape(2.dp))
            .background(color = buttonBg, shape = RoundedCornerShape(2.dp))
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.25.sp
        )
    }
}

@Composable
fun LollipopFlatButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = LollipopColors.Primary,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = if (enabled) textColor else LollipopColors.TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.25.sp
        )
    }
}

@Composable
fun LollipopTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val lineColor by animateColorAsState(
        targetValue = if (isFocused) LollipopColors.Primary else LollipopColors.Border,
        label = "lineColor"
    )
    val lineThickness by animateDpAsState(
        targetValue = if (isFocused) 2.dp else 1.dp,
        label = "lineThickness"
    )
    val labelColor = if (isFocused) LollipopColors.Primary else LollipopColors.TextSecondary

    Column(modifier = modifier.padding(vertical = 6.dp)) {
        val isLabelFloating = isFocused || value.isNotEmpty()
        Text(
            text = label,
            color = labelColor,
            fontSize = if (isLabelFloating) 12.sp else 16.sp,
            fontWeight = if (isLabelFloating) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            interactionSource = interactionSource,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            textStyle = LocalTextStyle.current.copy(
                color = LollipopColors.TextPrimary,
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            color = LollipopColors.TextSecondary,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                    innerTextField()
                }
            }
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(lineThickness)
                .background(lineColor)
        )
    }
}

@Composable
fun LollipopCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = LollipopColors.Primary
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .border(
                width = 2.dp,
                color = if (checked) activeColor else LollipopColors.TextSecondary,
                shape = RoundedCornerShape(2.dp)
            )
            .background(
                color = if (checked) activeColor else Color.Transparent,
                shape = RoundedCornerShape(2.dp)
            )
            .clickable(
                onClick = { onCheckedChange(!checked) },
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun PinVerificationDialog(
    action: PinAction,
    onDismiss: () -> Unit,
    onVerify: (String) -> Boolean
) {
    var pinValue by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        LollipopCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "ENTER PARENT PASSCODE",
                color = LollipopColors.Primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Please enter the parent passcode to proceed.",
                fontSize = 14.sp,
                color = LollipopColors.TextSecondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LollipopTextField(
                value = pinValue,
                onValueChange = {
                    if (it.length <= 8 && it.all { c -> c.isDigit() }) {
                        pinValue = it
                        hasError = false
                    }
                },
                label = "Parent PIN",
                placeholder = "••••",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = PasswordVisualTransformation()
            )

            if (hasError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Incorrect PIN. Try again.",
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                LollipopFlatButton(
                    onClick = onDismiss,
                    text = "Cancel",
                    textColor = LollipopColors.TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                LollipopButton(
                    onClick = {
                        val success = onVerify(pinValue)
                        if (!success) {
                            hasError = true
                            pinValue = ""
                        }
                    },
                    text = "Verify",
                    backgroundColor = LollipopColors.Accent
                )
            }
        }
    }
}
