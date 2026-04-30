package com.scrollblocker.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

// IMPORTANT: Ensure this matches your actual ViewModel package!
import com.scrollblocker.viewmodel.BlockingMode

// YOUR NEW COLOR PALETTE
import com.scrollblocker.ui.theme.BackgroundDark
import com.scrollblocker.ui.theme.CardDark
import com.scrollblocker.ui.theme.PrimaryCyan
import com.scrollblocker.ui.theme.SecondaryPink
import com.scrollblocker.ui.theme.SurfaceDark
import com.scrollblocker.ui.theme.TextGray
import com.scrollblocker.ui.theme.TextWhite
import com.scrollblocker.ui.theme.WarningOrange

@Composable
fun HomeScreen(
    isBlockingEnabled: Boolean,
    blockingMode: BlockingMode,
    blockedApps: Map<String, Boolean>,

    // Pause State
    isPauseToggleEnabled: Boolean,
    isPauseTimerActive: Boolean,
    pauseDuration: Int,
    pauseTimeRemaining: Long,

    // Callbacks
    onToggleBlocking: (Boolean) -> Unit,
    onBlockingModeChange: (BlockingMode) -> Unit,
    onAppBlockToggle: (String, Boolean) -> Unit,
    onPauseToggle: (Boolean) -> Unit,
    onPauseDurationChange: (Int) -> Unit,
    onStartPauseTimer: () -> Unit,

    // NEW: We added this to check accessibility status!
    isAccessibilityGranted: Boolean = true
) {
    // Background Gradient
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(BackgroundDark, SurfaceDark, BackgroundDark)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
    ) {
        // Decorative Neon Blurs
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-80).dp, y = 120.dp)
                .blur(80.dp)
                .background(PrimaryCyan.copy(alpha = 0.15f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = (-200).dp)
                .blur(60.dp)
                .background(SecondaryPink.copy(alpha = 0.15f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 1. ACCESSIBILITY NOTIFICATION BOX
            if (isAccessibilityGranted) {
                NotificationBox(
                    text = "Accessibility Permission Granted",
                    icon = Icons.Default.CheckCircle,
                    color = PrimaryCyan
                )
            } else {
                NotificationBox(
                    text = "Accessibility Permission Required!",
                    icon = Icons.Default.Warning,
                    color = WarningOrange
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. ROUND STATUS ICON
            val statusColor = if (isPauseTimerActive) SecondaryPink else if (!isBlockingEnabled) TextGray else PrimaryCyan
            val statusIcon = when {
                !isBlockingEnabled -> Icons.Default.Shield
                isPauseTimerActive -> Icons.Default.PauseCircle
                blockingMode == BlockingMode.BLOCK_ALL -> Icons.Default.Block
                else -> Icons.Default.Timer
            }
            val statusText = when {
                !isBlockingEnabled -> "Service is Turned Off"
                isPauseTimerActive -> "Pause is Active"
                blockingMode == BlockingMode.BLOCK_ALL -> "Block All Active"
                else -> "Daily Limit Active"
            }

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(CardDark)
                    .border(3.dp, statusColor.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = "Status",
                    tint = statusColor,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = statusText,
                color = TextWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. PAUSE OPTION (1m to 15m Slider)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Take a Break",
                        color = SecondaryPink,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Temporarily pause blocking to use your apps.",
                        color = TextGray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isPauseTimerActive) {
                        // Show Countdown
                        val minutesLeft = pauseTimeRemaining / 60
                        val secondsLeft = pauseTimeRemaining % 60
                        Text(
                            text = "${minutesLeft}m ${secondsLeft}s remaining",
                            color = TextWhite,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // Show Slider
                        var sliderValue by remember { mutableFloatStateOf(pauseDuration.toFloat()) }

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("${sliderValue.roundToInt()}", color = TextWhite, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                            Text(" min", color = SecondaryPink, fontSize = 16.sp, modifier = Modifier.padding(bottom = 6.dp))
                        }

                        Slider(
                            value = sliderValue,
                            onValueChange = {
                                sliderValue = it
                                onPauseDurationChange(it.roundToInt())
                            },
                            valueRange = 1f..15f,
                            steps = 13, // 1m to 15m
                            colors = SliderDefaults.colors(
                                thumbColor = SecondaryPink,
                                activeTrackColor = SecondaryPink,
                                inactiveTrackColor = SurfaceDark
                            )
                        )

                        Button(
                            onClick = onStartPauseTimer,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryPink)
                        ) {
                            Text("Start Pause", color = BackgroundDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. BLOCKING MODE & SPECIFIC APPS
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Blocking Rules",
                        color = PrimaryCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Strictly prevents display of short videos.",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Mode Selection Options
                    ModeSelectionRow(
                        title = "Block All Apps",
                        isSelected = blockingMode == BlockingMode.BLOCK_ALL,
                        onClick = { onBlockingModeChange(BlockingMode.BLOCK_ALL) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModeSelectionRow(
                        title = "Block Specific Apps",
                        isSelected = blockingMode == BlockingMode.BLOCK_SPECIFIC,
                        onClick = { onBlockingModeChange(BlockingMode.BLOCK_SPECIFIC) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = SurfaceDark) // Note: Used HorizontalDivider for Material3
                    Spacer(modifier = Modifier.height(16.dp))

                    // ALWAYS SHOW THE 3 APPS: Instagram, YouTube, X
                    val targetApps = listOf("Instagram", "YouTube", "X")

                    targetApps.forEach { appName ->
                        // If Block All is active, force it to true. Otherwise, check the map.
                        val isEffectivelyBlocked = if (blockingMode == BlockingMode.BLOCK_ALL) true else (blockedApps[appName] ?: false)
                        // Disable the switch if Block All is active so they can't turn it off
                        val isSwitchEnabled = blockingMode == BlockingMode.BLOCK_SPECIFIC

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = appName,
                                color = if (isEffectivelyBlocked) TextWhite else TextGray,
                                fontSize = 16.sp
                            )
                            Switch(
                                checked = isEffectivelyBlocked,
                                onCheckedChange = { isChecked ->
                                    if (isSwitchEnabled) {
                                        onAppBlockToggle(appName, isChecked)
                                    }
                                },
                                enabled = isSwitchEnabled,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = BackgroundDark,
                                    checkedTrackColor = PrimaryCyan,
                                    uncheckedThumbColor = TextGray,
                                    uncheckedTrackColor = SurfaceDark,
                                    // When disabled (Block All), keep it looking bright Cyan so they know it's active
                                    disabledCheckedThumbColor = BackgroundDark,
                                    disabledCheckedTrackColor = PrimaryCyan.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// Custom Component for the Notification Box
@Composable
fun NotificationBox(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, color = color, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

// Custom Component for the Mode Selection (Block All vs Specific)
@Composable
fun ModeSelectionRow(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) SurfaceDark else Color.Transparent)
            .border(1.dp, if (isSelected) PrimaryCyan else SurfaceDark, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(2.dp, if (isSelected) PrimaryCyan else TextGray, CircleShape)
                .background(if (isSelected) PrimaryCyan else Color.Transparent)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, color = if (isSelected) TextWhite else TextGray, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        isBlockingEnabled = true,
        blockingMode = BlockingMode.BLOCK_ALL, // Or SPECIFIC_APPS
        blockedApps = mapOf("Instagram" to true, "TikTok" to false, "YouTube" to true),
        isPauseToggleEnabled = true,
        isPauseTimerActive = false,
        pauseDuration = 5,
        pauseTimeRemaining = 0L,
        onToggleBlocking = {},
        onBlockingModeChange = {},
        onAppBlockToggle = { _, _ -> },
        onPauseToggle = {},
        onPauseDurationChange = {},
        onStartPauseTimer = {},
        isAccessibilityGranted = true
    )
}
