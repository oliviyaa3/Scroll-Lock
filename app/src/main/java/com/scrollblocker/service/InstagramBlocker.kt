package com.scrollblocker.service

import android.accessibilityservice.AccessibilityService
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

object InstagramBlocker {

    private const val TAG = "InstagramBlocker"
    private const val PACKAGE_NAME = "com.instagram.android"

    private val REELS_IDS = listOf("com.instagram.android:id/clips_video_container", "com.instagram.android:id/reel_viewer_root", "com.instagram.android:id/clips_root_container")
    private val REELS_TAB_IDS = listOf("com.instagram.android:id/clips_tab")
    private val EXPLORE_TAB_IDS = listOf("com.instagram.android:id/search_tab")
    private val SAFE_IDS = listOf("com.instagram.android:id/action_bar_button_back", "com.instagram.android:id/back_button")

    private var lastCheckTime = 0L
    private const val CHECK_INTERVAL_MS = 400L

    // NEW: Tracks when the scroll cooldown started
    private var scrollCooldownStartTime = 0L

    fun handleEvent(service: AccessibilityService, event: AccessibilityEvent, rootNode: AccessibilityNodeInfo?, mode: EnforcementMode) {
        if (event.packageName?.toString() != PACKAGE_NAME) return
        if (rootNode == null) return

        // If no rules apply, reset the timer and let them browse freely
        if (mode == EnforcementMode.NONE) {
            scrollCooldownStartTime = 0L
            return
        }

        // --- RULE 1: COOLDOWN SCROLL (Disable scroll after 5 seconds) ---
        if (mode == EnforcementMode.COOLDOWN_SCROLL) {
            if (scrollCooldownStartTime == 0L) {
                scrollCooldownStartTime = SystemClock.elapsedRealtime() // Start the 5s timer
            }

            // If they try to scroll...
            if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                // And 5 seconds have passed...
                if (SystemClock.elapsedRealtime() - scrollCooldownStartTime > 5000) {
                    Log.d(TAG, "Scroll Cooldown limit reached (5s) -> Blocking scroll")
                    performBlockAction(service) // Kick them back!
                    return
                }
            }
        } else {
            scrollCooldownStartTime = 0L // Reset if not in scroll mode
        }

        // --- RULE 2: BLOCK SHORTS (Block All or Cooldown Shorts) ---
        if (mode == EnforcementMode.BLOCK_ALL_SHORTS || mode == EnforcementMode.COOLDOWN_SHORTS) {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastCheckTime < CHECK_INTERVAL_MS) return

            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
                event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED ||
                event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

                if (isReelsTab(rootNode)) {
                    lastCheckTime = currentTime
                    Log.d(TAG, "REELS TAB detected -> Blocking")
                    performBlockAction(service)
                } else if (isExploreTab(rootNode)) {
                    lastCheckTime = currentTime
                    Log.d(TAG, "EXPLORE TAB detected -> Blocking")
                    performBlockAction(service)
                } else if (isReelsFromFeed(rootNode)) {
                    lastCheckTime = currentTime
                    Log.d(TAG, "REELS FROM FEED detected -> Blocking")
                    performBlockAction(service)
                }
            }
        }
    }

    private fun isReelsTab(root: AccessibilityNodeInfo): Boolean {
        for (id in REELS_TAB_IDS) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            if (nodes != null) {
                for (node in nodes) {
                    val isSelected = node.isSelected || node.contentDescription?.contains("selected", ignoreCase = true) == true
                    if (node.isVisibleToUser && isSelected) return true
                }
            }
        }
        return !hasBackButton(root) && hasReelsContent(root)
    }

    private fun isExploreTab(root: AccessibilityNodeInfo): Boolean {
        for (id in EXPLORE_TAB_IDS) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            if (nodes != null) {
                for (node in nodes) {
                    val isSelected = node.isSelected || node.contentDescription?.contains("selected", ignoreCase = true) == true
                    if (node.isVisibleToUser && isSelected) return true
                }
            }
        }
        return false
    }

    private fun isReelsFromDM(root: AccessibilityNodeInfo): Boolean {
        if (!hasReelsContent(root)) return false
        if (!hasBackButton(root)) return false

        val dmIndicators = listOf("Type a message", "Direct", "conversation", "video call", "audio call")
        for (indicator in dmIndicators) {
            val nodes = root.findAccessibilityNodeInfosByText(indicator)
            if (nodes != null && nodes.isNotEmpty()) return true
        }

        val potentialDmIds = listOf("com.instagram.android:id/thread_title", "com.instagram.android:id/row_thread_composer_edittext", "com.instagram.android:id/direct_header_title")
        for (id in potentialDmIds) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            if (nodes != null && nodes.isNotEmpty()) return true
        }
        return false
    }

    private fun isReelsFromFeed(root: AccessibilityNodeInfo): Boolean {
        if (!hasReelsContent(root)) return false
        if (isReelsFromDM(root)) return false
        return hasBackButton(root)
    }

    private fun hasReelsContent(root: AccessibilityNodeInfo): Boolean {
        for (id in REELS_IDS) {
            val nodes = root.findAccessibilityNodeInfosByViewId(id)
            if (nodes?.any { it.isVisibleToUser } == true) return true
        }
        return false
    }

    private fun hasBackButton(root: AccessibilityNodeInfo): Boolean {
        for (safeId in SAFE_IDS) {
            val nodes = root.findAccessibilityNodeInfosByViewId(safeId)
            if (nodes?.any { it.isVisibleToUser } == true) return true
        }
        val safeKeywords = listOf("Back", "Navigate up")
        for (keyword in safeKeywords) {
            val nodes = root.findAccessibilityNodeInfosByText(keyword)
            if (nodes != null) {
                for (node in nodes) {
                    val desc = node.contentDescription?.toString()
                    val text = node.text?.toString()
                    if (node.isVisibleToUser && (desc?.equals(keyword, ignoreCase = true) == true || text?.equals(keyword, ignoreCase = true) == true)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun performBlockAction(service: AccessibilityService) {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }
}
