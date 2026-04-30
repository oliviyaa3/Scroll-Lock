package com.scrollblocker.service

import android.accessibilityservice.AccessibilityService
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

object XBlocker {

    private const val TAG = "XBlocker"
    private const val PACKAGE_NAME = "com.twitter.android"

    private val IMMERSIVE_VIDEO_IDS = listOf("com.twitter.android:id/video_player", "com.twitter.android:id/video_view", "com.twitter.android:id/immersive_player", "com.twitter.android:id/immersive_video_player", "com.twitter.android:id/full_screen_video", "com.twitter.android:id/video_pager", "com.twitter.android:id/immersive_container")
    private val DM_IDS = listOf("com.twitter.android:id/dm_inbox", "com.twitter.android:id/conversation_list", "com.twitter.android:id/message_text", "com.twitter.android:id/dm_composer")

    private var lastCheckTime = 0L
    private const val CHECK_INTERVAL_MS = 400L

    // NEW: Tracks when the scroll cooldown started
    private var scrollCooldownStartTime = 0L

    fun handleEvent(service: AccessibilityService, event: AccessibilityEvent, rootNode: AccessibilityNodeInfo?, mode: EnforcementMode) {
        if (event.packageName?.toString() != PACKAGE_NAME) return
        if (rootNode == null) return

        if (mode == EnforcementMode.NONE) {
            scrollCooldownStartTime = 0L
            return
        }

        // --- RULE 1: COOLDOWN SCROLL (Disable scroll after 5 seconds) ---
        if (mode == EnforcementMode.COOLDOWN_SCROLL) {
            if (scrollCooldownStartTime == 0L) {
                scrollCooldownStartTime = SystemClock.elapsedRealtime()
            }

            if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                if (SystemClock.elapsedRealtime() - scrollCooldownStartTime > 5000) {
                    Log.d(TAG, "Scroll Cooldown limit reached (5s) -> Blocking scroll")
                    performBlockAction(service)
                    return
                }
            }
        } else {
            scrollCooldownStartTime = 0L
        }

        // --- RULE 2: BLOCK SHORTS (Block All or Cooldown Shorts) ---
        if (mode == EnforcementMode.BLOCK_ALL_SHORTS || mode == EnforcementMode.COOLDOWN_SHORTS) {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastCheckTime < CHECK_INTERVAL_MS) return

            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
                event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED ||
                event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

                if (isInDMContext(rootNode)) {
                    Log.d(TAG, "DM context detected -> Allowing")
                    return
                }

                if (isImmersiveVideoMode(rootNode)) {
                    lastCheckTime = currentTime
                    Log.d(TAG, "IMMERSIVE VIDEO MODE detected -> Blocking")
                    performBlockAction(service)
                }
            }
        }
    }

    private fun isImmersiveVideoMode(root: AccessibilityNodeInfo): Boolean {
        for (id in IMMERSIVE_VIDEO_IDS) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            if (nodes?.any { it.isVisibleToUser } == true) {
                if (!isInDMContext(root) && isFullScreenVideo(root)) return true
            }
        }
        val immersiveKeywords = listOf("Swipe up for next video", "Video", "Playing")
        for (keyword in immersiveKeywords) {
            val nodes = root.findAccessibilityNodeInfosByText(keyword)
            if (nodes != null) {
                for (node in nodes) {
                    if (node.isVisibleToUser && isFullScreenVideo(root)) return true
                }
            }
        }
        return false
    }

    private fun isFullScreenVideo(root: AccessibilityNodeInfo): Boolean {
        val bottomNavIds = listOf("com.twitter.android:id/bottom_nav", "com.twitter.android:id/bottom_navigation", "com.twitter.android:id/bottom_bar")
        var bottomNavVisible = false
        for (id in bottomNavIds) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            if (nodes?.any { it.isVisibleToUser } == true) {
                bottomNavVisible = true
                break
            }
        }
        val immersiveControlIds = listOf("com.twitter.android:id/video_controls", "com.twitter.android:id/player_controls", "com.twitter.android:id/immersive_controls")
        for (id in immersiveControlIds) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            if (nodes?.any { it.isVisibleToUser } == true) return true
        }
        return !bottomNavVisible
    }

    private fun isInDMContext(root: AccessibilityNodeInfo): Boolean {
        for (id in DM_IDS) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            if (nodes != null && nodes.isNotEmpty()) return true
        }
        val dmIndicators = listOf("Start a new message", "Send a message", "Message", "Direct Messages")
        for (indicator in dmIndicators) {
            val nodes = root.findAccessibilityNodeInfosByText(indicator)
            if (nodes != null && nodes.isNotEmpty()) return true
        }
        return false
    }

    private fun performBlockAction(service: AccessibilityService) {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }
}
