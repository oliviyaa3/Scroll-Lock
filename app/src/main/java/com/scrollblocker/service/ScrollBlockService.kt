package com.scrollblocker.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.scrollblocker.data.preferences.AppPreferences

// NEW: Tells the blockers exactly what rules to apply right now
enum class EnforcementMode {
    NONE,               // Do nothing, let the user browse
    BLOCK_ALL_SHORTS,   // "Block All" is active -> Strictly block short videos
    COOLDOWN_SCROLL,    // Limit reached -> Disable scroll after 5 secs
    COOLDOWN_SHORTS     // Limit reached -> Block short videos
}

class ScrollBlockService : AccessibilityService() {

    private lateinit var prefs: AppPreferences

    // Variables to track time spent in apps
    private var currentForegroundPackage: String? = null
    private var lastEventTime: Long = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = AppPreferences(applicationContext)
        Log.d("ScrollBlocker", "Accessibility Service CONNECTED")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!::prefs.isInitialized) prefs = AppPreferences(applicationContext)

        // 1. Check Global Switch & Pause
        if (!prefs.isBlockingEnabled() || prefs.isPauseActive()) {
            currentForegroundPackage = null // Reset tracking if paused/disabled
            return
        }

        if (event == null) return
        val pkg = event.packageName?.toString() ?: return

        // 2. Check if this app is on our radar
        if (!shouldBlockPackage(pkg)) {
            currentForegroundPackage = null
            return
        }

        // 3. Track Usage Time & Determine Mode
        val enforcementMode = calculateEnforcementMode(pkg)

        val root = rootInActiveWindow ?: return

        // 4. Dispatch to handlers with the specific Enforcement Mode!
        when (pkg) {
            "com.instagram.android" -> {
                InstagramBlocker.handleEvent(this, event, root, enforcementMode)
            }
            "com.twitter.android" -> {
                XBlocker.handleEvent(this, event, root, enforcementMode)
            }
            "com.google.android.youtube" -> {
                YouTubeBlocker.handleEvent(this, event, root, enforcementMode)
            }
        }
    }

    private fun calculateEnforcementMode(pkg: String): EnforcementMode {
        val mode = prefs.getBlockingMode()
        val now = System.currentTimeMillis()

        // SCENARIO A: "Block All" is active
        if (mode == "BLOCK_ALL") {
            return EnforcementMode.BLOCK_ALL_SHORTS
        }

        // SCENARIO B: "Daily Limit" (Specific Apps) is active

        // 1. Track Time
        if (pkg != currentForegroundPackage) {
            currentForegroundPackage = pkg
            lastEventTime = now
        } else {
            val timeSpentMillis = now - lastEventTime
            if (timeSpentMillis > 0 && timeSpentMillis < 5000) { // Safety check to ignore massive gaps
                prefs.addDailyUsage(timeSpentMillis)
            }
            lastEventTime = now
        }

        val usageMillis = prefs.getDailyUsage()
        val limitMillis = prefs.getDailyLimitMinutes() * 60 * 1000L

        // 2. Check if Limit is reached
        if (usageMillis >= limitMillis) {
            val cooldownEnd = prefs.getCooldownEndTime()

            if (cooldownEnd == 0L) {
                // Limit JUST reached! Start the cooldown timer.
                val cooldownDurationMillis = prefs.getCooldownMinutes() * 60 * 1000L
                prefs.setCooldownEndTime(now + cooldownDurationMillis)
                return getCooldownEnforcement()
            } else if (now < cooldownEnd) {
                // We are currently INSIDE the cooldown period
                return getCooldownEnforcement()
            } else {
                // Cooldown has FINISHED! Reset the daily usage for a new session
                prefs.resetDailyUsage()
                prefs.setCooldownEndTime(0L)
                return EnforcementMode.NONE
            }
        }

        // Under the limit, user is free to browse
        return EnforcementMode.NONE
    }

    private fun getCooldownEnforcement(): EnforcementMode {
        return if (prefs.getCooldownMode() == "SCROLL") {
            EnforcementMode.COOLDOWN_SCROLL
        } else {
            EnforcementMode.COOLDOWN_SHORTS
        }
    }

    override fun onInterrupt() {}

    private fun shouldBlockPackage(pkg: String): Boolean {
        val mode = prefs.getBlockingMode()
        if (pkg.contains("nexuslauncher") || pkg.contains("launcher")) return false

        return when (mode) {
            "BLOCK_ALL" -> {
                // Only target our 3 specific apps even in Block All
                val targetApps = listOf("com.instagram.android", "com.twitter.android", "com.google.android.youtube")
                targetApps.contains(pkg)
            }
            "BLOCK_SPECIFIC" -> {
                val blockedApps = prefs.getBlockedApps()
                val appMap = mapOf(
                    "Instagram" to "com.instagram.android",
                    "X" to "com.twitter.android",
                    "YouTube" to "com.google.android.youtube"
                )
                blockedApps.any { appName -> appMap[appName] == pkg }
            }
            else -> false
        }
    }
}
