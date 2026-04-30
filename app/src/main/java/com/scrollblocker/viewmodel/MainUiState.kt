package com.scrollblocker.viewmodel

/**
 * Single source of truth for the Main UI state.
 */
data class MainUiState(
    val isFirstLaunch: Boolean = true,
    val isAccessibilityEnabled: Boolean = false,
    val isBlockingEnabled: Boolean = true,
    val blockingMode: BlockingMode = BlockingMode.BLOCK_ALL,

    // UPDATED: Only the 3 apps you requested!
    val blockedApps: Map<String, Boolean> = mapOf(
        "Instagram" to true,
        "X" to true,
        "YouTube" to true
    ),

    // Pause Feature State
    val isPauseToggleEnabled: Boolean = false, // Switch State
    val isPauseTimerActive: Boolean = false,   // Timer Running State
    val selectedPauseDurationMinutes: Int = 1, // Slider Value
    val pauseTimeRemainingSeconds: Long = 0,   // Timer Countdown

    // NEW: Session & Cooldown State
    val dailyLimitMinutes: Int = 15,
    val cooldownMinutes: Int = 15,
    val cooldownMode: String = "SCROLL" // "SCROLL" or "SHORTS"
)

enum class BlockingMode {
    BLOCK_ALL,
    BLOCK_SPECIFIC,
    STOPPED
}
