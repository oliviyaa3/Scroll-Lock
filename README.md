# No Scroll – Project Documentation

## 1. Product Overview

**No Scroll** is an Android application designed to block infinite scrolling behavior across apps using the Android Accessibility Service.

### Project Status
- **Phase 1 (MVP):** ✅ **Completed**
- **Phase 2 (Enhancements):** 🚧 **Pending / In Progress**

---

## 2. Completed Features (MVP)

The following features have been implemented and validated:

- **Core Blocking Engine:** Uses Accessibility Service to interrupt scroll gestures.
- **Global Toggle:** Simple ON/OFF switch to enable or disable blocking globally.
- **Permission Handling:** Guided flow to request Accessibility permissions.
- **Architecture:** MVVM with Jetpack Compose.
- **Persistence:** Saves state (First launch, Blocking Active) via Preferences.

### MVP Screens (Implemented)
1.  **Welcome Screen:** Introduction and setup guide.
2.  **Accessibility Permission Screen:** Redirects user to system settings.
3.  **Home Screen:** Main dashboard with the master toggle.

---

## 3. Pending Features (Phase 2)

We are now expanding the app to provide granular control and better reliability.

### 3.1 Advanced Blocking Options
The user must be able to configure *how* blocking works beyond a simple global switch.
- **Block All Apps:** Continue supporting the global kill-switch.
- **Block Specific Apps:**
    - Allow users to select specific apps to block (Target focus: Instagram, X).
    - Implement an app picker list.
- **Stop Blocking:** Clear function to disable the service efficiently.

### 3.2 Temporary Pause
Allow users to take a short break without completely disabling the habit-building tool.
- **Functionality:** Pause blocking for a specific duration.
- **Range:** 1 minute to 10 minutes.
- **Behavior:** Auto-resume blocking after the timer expires.

### 3.3 System Reliability & Permissions
To ensure the Accessibility Service is not killed by the Android OS, we must request looser battery restrictions.
- **Battery Optimization:**
    - UI prompt on the Home Screen requesting to **disable battery optimization**.
- **Unrestricted Background Access:**
    - Request "Unrestricted" data/battery usage where applicable to ensure the service stays alive in the background.

---

## 4. Architecture Overview

### Architectural Style
- MVVM (simplified)
- Accessibility Service as system-level engine

```
UI → ViewModel → Preferences
               ↓
     Accessibility Service
```

**Key Rule:**
- UI never directly communicates with Accessibility Service. UI writes to Preferences; Service listens to Preferences.

---

## 5. Project Structure

```
com.example.noscroll
│
├── MainActivity.kt
│
├── accessibility/
│   └── ScrollBlockAccessibilityService.kt
│
├── ui/
│   ├── welcome/
│   │   └── WelcomeScreen.kt
│   │
│   ├── permission/
│   │   └── AccessibilityPermissionScreen.kt
│   │
│   └── home/
│       └── HomeScreen.kt
│
├── viewmodel/
│   └── MainViewModel.kt
│
├── navigation/
│   └── AppNavGraph.kt
│
├── data/
│   └── preferences/
│       └── AppPreferences.kt
│
└── util/
    └── PermissionUtils.kt
```

---

## 6. Implementation Plan for Phase 2

### Updated File Responsibilities

#### `data/preferences/AppPreferences.kt`
- **Update:** Need to store:
    - `blockedPackageNames: Set<String>` (for specific app blocking).
    - `pauseEndTime: Long` (for handling pause logic).

#### `accessibility/ScrollBlockAccessibilityService.kt`
- **Update:** Logic to check:
    - Is the current app in the `blockedPackageNames` list (if not "Block All")?
    - Is the `pauseEndTime` > `currentTime`? (If yes, allow scrolling).

#### `ui/home/HomeScreen.kt`
- **Update:**
    - Add UI for "Pause" (Dropdown or Dialogue).
    - Add UI/Navigation to an "App Selection" screen.
    - Add Card/Button for "Disable Battery Optimization".

#### `util/PermissionUtils.kt`
- **Update:** Add helper methods for Battery Optimization intents.

---

## 7. State Model (Updated)

### Persisted State
- `isFirstLaunch: Boolean`
- `isBlockingEnabled: Boolean`
- `blockingMode: Enum` (ALL_APPS vs SPECIFIC_APPS)
- `blockedApps: Set<String>`

### Runtime State
- `isAccessibilityEnabled`
- `remainingPauseTime`
- `isBatteryOptimizationDisabled`

