package com.scrollblocker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.scrollblocker.data.preferences.AppPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val appPreferences: AppPreferences) : ViewModel() {

    private var timerJob: Job? = null

    private val _uiState = MutableStateFlow(
        MainUiState(
            isFirstLaunch = appPreferences.isFirstLaunch(),
            isBlockingEnabled = appPreferences.isBlockingEnabled(),
            blockingMode = BlockingMode.valueOf(appPreferences.getBlockingMode())
        )
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        val savedBlockedApps = appPreferences.getBlockedApps()
        val targetApps = listOf("Instagram", "X", "YouTube")

        // If an app is in savedBlockedApps, it's true. Otherwise false.
        // If it's the very first time, we default to true for maximum protection.
        val initialMap = targetApps.associateWith { appName ->
            if (appPreferences.isFirstLaunch()) true else savedBlockedApps.contains(appName)
        }

        _uiState.update {
            it.copy(
                isFirstLaunch = appPreferences.isFirstLaunch(),
                blockedApps = initialMap,
                dailyLimitMinutes = appPreferences.getDailyLimitMinutes(),
                cooldownMinutes = appPreferences.getCooldownMinutes(),
                cooldownMode = appPreferences.getCooldownMode()
            )
        }

        checkExistingPause()

    }

    fun setDailyLimitMinutes(minutes: Int) {
        appPreferences.setDailyLimitMinutes(minutes) // Actually saves it!
        _uiState.update { it.copy(dailyLimitMinutes = minutes) }
    }

    fun setCooldownConfig(minutes: Int, mode: String) {
        appPreferences.setCooldownMinutes(minutes) // Actually saves it!
        appPreferences.setCooldownMode(mode)       // Actually saves it!
        _uiState.update {
            it.copy(
                cooldownMinutes = minutes,
                cooldownMode = mode
            )
        }
    }

    // --------------------------------

    private fun checkExistingPause() {
        val endTime = appPreferences.getPauseEndTime()
        val now = System.currentTimeMillis()
        if (endTime > now) {
            // Resume timer
            val remainingSeconds = (endTime - now) / 1000
            _uiState.update {
                it.copy(
                    isPauseToggleEnabled = true,
                    isPauseTimerActive = true,
                    pauseTimeRemainingSeconds = remainingSeconds
                )
            }
            startTimerCountdown(remainingSeconds)
        } else {
            // Reset if expired
            if (endTime != 0L) {
                appPreferences.setPauseEndTime(0)
            }
        }
    }

    fun onWelcomeCompleted() {
        appPreferences.setFirstLaunchCompleted()
        _uiState.update { it.copy(isFirstLaunch = false) }
    }

    fun updateAccessibilityPermissionState(isEnabled: Boolean) {
        _uiState.update { it.copy(isAccessibilityEnabled = isEnabled) }
    }

    fun setBlockingEnabled(enabled: Boolean) {
        appPreferences.setBlockingEnabled(enabled)
        _uiState.update { it.copy(isBlockingEnabled = enabled) }

        // If global blocking is disabled, turn off pause toggle and stop timer
        if (!enabled) {
            setPauseToggleEnabled(false)
        }
    }

    fun setBlockingMode(mode: BlockingMode) {
        appPreferences.setBlockingMode(mode.name)
        _uiState.update { it.copy(blockingMode = mode) }
    }

    fun toggleAppBlock(appName: String, isBlocked: Boolean) {
        val currentMap = _uiState.value.blockedApps.toMutableMap()
        currentMap[appName] = isBlocked
        _uiState.update { it.copy(blockedApps = currentMap) }

        // Save to prefs
        val blockedSet = currentMap.filter { it.value }.keys
        appPreferences.setBlockedApps(blockedSet)
    }

    fun setPauseToggleEnabled(enabled: Boolean) {
        if (!enabled) {
            // Turn OFF: Stop timer, clear prefs
            stopPauseTimer()
        }
        _uiState.update { it.copy(isPauseToggleEnabled = enabled) }
    }

    fun setPauseDurationMinutes(minutes: Int) {
        _uiState.update { it.copy(selectedPauseDurationMinutes = minutes) }
    }

    fun startPauseTimer() {
        val minutes = _uiState.value.selectedPauseDurationMinutes
        val durationMillis = minutes * 60 * 1000L
        val endTime = System.currentTimeMillis() + durationMillis

        appPreferences.setPauseEndTime(endTime)

        _uiState.update {
            it.copy(
                isPauseTimerActive = true,
                pauseTimeRemainingSeconds = durationMillis / 1000
            )
        }

        startTimerCountdown(durationMillis / 1000)
    }

    private fun stopPauseTimer() {
        timerJob?.cancel()
        appPreferences.setPauseEndTime(0)
        _uiState.update {
            it.copy(
                isPauseTimerActive = false,
                pauseTimeRemainingSeconds = 0
            )
        }
    }

    private fun startTimerCountdown(totalSeconds: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = totalSeconds
            while (remaining > 0) {
                _uiState.update { it.copy(pauseTimeRemainingSeconds = remaining) }
                delay(1000)
                remaining--
            }
            // Timer finished
            stopPauseTimer()
            _uiState.update { it.copy(isPauseToggleEnabled = false) }
        }
    }

    class Factory(private val appPreferences: AppPreferences) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(appPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
