package com.scrollblocker.data.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Persistent storage for MVP app state.
 */
class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    companion object {
        private const val PREFS_NAME = "no_scroll_prefs"
        private const val KEY_IS_FIRST_LAUNCH = "is_first_launch"
        private const val KEY_IS_BLOCKING_ENABLED = "is_blocking_enabled"
        private const val KEY_BLOCKING_MODE = "blocking_mode"
        private const val KEY_BLOCKED_APPS = "blocked_apps"
        private const val KEY_PAUSE_END_TIME = "pause_end_time"
        private const val KEY_DAILY_LIMIT_MINUTES = "daily_limit_minutes"
        private const val KEY_COOLDOWN_MINUTES = "cooldown_minutes"
        private const val KEY_COOLDOWN_MODE = "cooldown_mode"
    }

    fun isFirstLaunch(): Boolean = true
        //prefs.getBoolean(KEY_IS_FIRST_LAUNCH, true)


    fun setFirstLaunchCompleted() {
        prefs.edit()
            .putBoolean(KEY_IS_FIRST_LAUNCH, false)
            .apply()
    }

    fun isBlockingEnabled(): Boolean =
        prefs.getBoolean(KEY_IS_BLOCKING_ENABLED, true)

    fun setBlockingEnabled(enabled: Boolean) {
        prefs.edit()
            .putBoolean(KEY_IS_BLOCKING_ENABLED, enabled)
            .apply()
    }

    fun getBlockingMode(): String =
        prefs.getString(KEY_BLOCKING_MODE, "BLOCK_ALL") ?: "BLOCK_ALL"

    fun setBlockingMode(mode: String) {
        prefs.edit()
            .putString(KEY_BLOCKING_MODE, mode)
            .apply()
    }

    fun getBlockedApps(): Set<String> =
        prefs.getStringSet(KEY_BLOCKED_APPS, setOf("Instagram", "X", "YouTube")) ?: setOf("Instagram", "X", "YouTube")

    fun setBlockedApps(apps: Set<String>) {
        prefs.edit()
            .putStringSet(KEY_BLOCKED_APPS, apps)
            .apply()
    }

    fun getPauseEndTime(): Long =
        prefs.getLong(KEY_PAUSE_END_TIME, 0L)

    fun setPauseEndTime(timestamp: Long) {
        prefs.edit()
            .putLong(KEY_PAUSE_END_TIME, timestamp)
            .apply()
    }

    fun isPauseActive(): Boolean {
        val endTime = getPauseEndTime()
        return endTime > System.currentTimeMillis()
    }

    fun getDailyLimitMinutes(): Int =
        prefs.getInt(KEY_DAILY_LIMIT_MINUTES, 15) // Default to 15 mins

    fun setDailyLimitMinutes(minutes: Int) {
        prefs.edit()
            .putInt(KEY_DAILY_LIMIT_MINUTES, minutes)
            .apply()
    }

    fun getCooldownMinutes(): Int =
        prefs.getInt(KEY_COOLDOWN_MINUTES, 15) // Default to 15 mins

    fun setCooldownMinutes(minutes: Int) {
        prefs.edit()
            .putInt(KEY_COOLDOWN_MINUTES, minutes)
            .apply()
    }

    fun getCooldownMode(): String =
        prefs.getString(KEY_COOLDOWN_MODE, "SCROLL") ?: "SCROLL" // Default to SCROLL

    fun setCooldownMode(mode: String) {
        prefs.edit()
            .putString(KEY_COOLDOWN_MODE, mode)
            .apply()
    }
    // --- NEW: TIME TRACKING FOR SERVICE ---

    fun getDailyUsage(): Long = prefs.getLong("daily_usage_millis", 0L)

    fun addDailyUsage(millis: Long) {
        val current = getDailyUsage()
        prefs.edit().putLong("daily_usage_millis", current + millis).apply()
    }

    fun resetDailyUsage() {
        prefs.edit().putLong("daily_usage_millis", 0L).apply()
    }

    fun getCooldownEndTime(): Long = prefs.getLong("cooldown_end_time_millis", 0L)

    fun setCooldownEndTime(time: Long) {
        prefs.edit().putLong("cooldown_end_time_millis", time).apply()
    }

}
