package com.scrollblocker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// IMPORTANT: I added the import for your new Session screen here!
import com.scrollblocker.ui.session.SessionConfigurationScreen
import com.scrollblocker.ui.permission.AccessibilityPermissionScreen
import com.scrollblocker.ui.welcome.WelcomeScreen
import com.scrollblocker.util.PermissionUtils
import com.scrollblocker.viewmodel.MainViewModel
import com.scrollblocker.ui.home.HomeScreen
import com.scrollblocker.viewmodel.BlockingMode

object Destinations {
    const val WELCOME = "welcome"
    const val SESSION_CONFIG = "session_config"
    const val COOLDOWN_CONFIG = "cooldown_config"
    const val PERMISSION = "permission"
    const val HOME = "home"

}

@Composable
fun AppNavGraph(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Decide start destination based on state
    val startDestination = if (uiState.isFirstLaunch) {
        Destinations.WELCOME
    } else if (uiState.isAccessibilityEnabled) {
        Destinations.HOME
    } else {
        Destinations.PERMISSION
    }

    // Effect to navigate based on state changes
    LaunchedEffect(uiState.isAccessibilityEnabled, uiState.isFirstLaunch) {
        if (!uiState.isFirstLaunch) {
            if (uiState.isAccessibilityEnabled) {
                if (navController.currentDestination?.route != Destinations.HOME) {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.PERMISSION) { inclusive = true }
                        popUpTo(Destinations.WELCOME) { inclusive = true }
                        popUpTo(Destinations.SESSION_CONFIG) { inclusive = true }
                        popUpTo(Destinations.COOLDOWN_CONFIG) { inclusive = true }
                    }
                }
            } else {
                // If permission is lost or not granted yet, and we are not in Welcome or Session Config
                if (navController.currentDestination?.route != Destinations.PERMISSION &&
                    navController.currentDestination?.route != Destinations.WELCOME &&
                    navController.currentDestination?.route != Destinations.SESSION_CONFIG &&
                    navController.currentDestination?.route != Destinations.COOLDOWN_CONFIG) { // <-- Added check
                    navController.navigate(Destinations.PERMISSION) {
                        popUpTo(Destinations.HOME) { inclusive = true }
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destinations.WELCOME) {
            WelcomeScreen(
                onDailyLimitClick = {
                    navController.navigate(Destinations.SESSION_CONFIG)
                },
                onBlockAllClick = {
                    // 1. Turn on the blocker
                    viewModel.setBlockingEnabled(true)
                    // 2. Set mode to Block All
                    viewModel.setBlockingMode(BlockingMode.BLOCK_ALL)
                    // 3. Finish onboarding (This automatically routes to Permission screen!)
                    viewModel.onWelcomeCompleted()
                }
            )
        }

        composable(Destinations.SESSION_CONFIG) {
            SessionConfigurationScreen(
                onContinue = { selectedMinutes ->
                    // Save the limit!
                    viewModel.setDailyLimitMinutes(selectedMinutes)
                    navController.navigate(Destinations.COOLDOWN_CONFIG)
                }
            )
        }

        composable(Destinations.COOLDOWN_CONFIG) {
            com.scrollblocker.ui.session.CooldownPeriodScreen(
                onGetStarted = { cooldownMinutes, selectedOption ->
                    // Save the cooldown settings!
                    viewModel.setCooldownConfig(cooldownMinutes, selectedOption)

                    // Turn on blocking and set mode to Specific Apps
                    viewModel.setBlockingEnabled(true)
                    viewModel.setBlockingMode(BlockingMode.BLOCK_SPECIFIC)

                    // Finish onboarding!
                    viewModel.onWelcomeCompleted()
                }
            )
        }

        composable(Destinations.PERMISSION) {
            AccessibilityPermissionScreen(
                onOpenSettings = {
                    PermissionUtils.openAccessibilitySettings(context)
                }
            )
        }

        composable(Destinations.HOME) {
            HomeScreen(
                isBlockingEnabled = uiState.isBlockingEnabled,
                blockingMode = uiState.blockingMode,
                blockedApps = uiState.blockedApps,
                isPauseToggleEnabled = uiState.isPauseToggleEnabled,
                isPauseTimerActive = uiState.isPauseTimerActive,
                pauseDuration = uiState.selectedPauseDurationMinutes,
                pauseTimeRemaining = uiState.pauseTimeRemainingSeconds,
                onToggleBlocking = { enabled ->
                    viewModel.setBlockingEnabled(enabled)
                },
                onBlockingModeChange = { mode ->
                    viewModel.setBlockingMode(mode)
                },
                onAppBlockToggle = { app, isBlocked ->
                    viewModel.toggleAppBlock(app, isBlocked)
                },
                onPauseToggle = { enabled ->
                    viewModel.setPauseToggleEnabled(enabled)
                },
                onPauseDurationChange = { duration ->
                    viewModel.setPauseDurationMinutes(duration)
                },
                onStartPauseTimer = {
                    viewModel.startPauseTimer()
                }
            )
        }
    }
}
