package com.scrollblocker.service

import android.accessibilityservice.AccessibilityService
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

object YouTubeBlocker {

    private const val TAG = "YouTubeBlocker"
    private const val PACKAGE_NAME = "com.google.android.youtube"

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
                    service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
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

            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (isShortsScreen(rootNode)) {
                    lastCheckTime = currentTime
                    Log.d(TAG, "SHORTS detected -> Clicking Home Tab")
                    clickYouTubeHomeTab(rootNode)
                }
            }
        }
    }

    private fun isShortsScreen(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        if (containsReelNode(node)) return true
        for (i in 0 until node.childCount) {
            if (isShortsScreen(node.getChild(i))) return true
        }
        return false
    }

    private fun containsReelNode(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        val viewId = node.viewIdResourceName ?: ""
        if (viewId.contains(":id/reel_player_page_container")) return true
        for (i in 0 until node.childCount) {
            if (containsReelNode(node.getChild(i))) return true
        }
        return false
    }

    private fun clickYouTubeHomeTab(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        val text = node.text?.toString()?.lowercase()
        val desc = node.contentDescription?.toString()?.lowercase()

        if ((desc == "navigate up" || text == "shorts" || desc == "shorts" || text == "home" || desc == "home") && node.isClickable) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return true
        }

        for (i in 0 until node.childCount) {
            if (clickYouTubeHomeTab(node.getChild(i))) return true
        }
        return false
    }
}
