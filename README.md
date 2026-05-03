# <img src="https://github.com/oliviyaa3/Scroll-Lock/blob/main/app/screenshots/ScrollLock.png" width="40"/> ScrollLock

![Kotlin](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![MVVM Architecture](https://img.shields.io/badge/MVVM-Architecture-brightgreen?style=for-the-badge)

**ScrollLock** is a modern Android productivity and digital wellness app designed to combat doomscrolling. By leveraging the Android Accessibility Service API, the app monitors social media usage in real-time and gently blocks access when user-defined daily limits are exceeded. 

Featuring a custom themed UI, ScrollLock makes taking back control of your screen time visually stunning and highly effective.

## ✨ Features
* **Real-Time App Blocking:** Instantly detects and pulls you out of the screen when clicking short videos on social media apps (like Instagram, YouTube, etc.) when time is up.
* **Custom Daily Limits:** Set exact minute allowances for your most distracting apps.
* **Cooldown Timers:** Enforces a mandatory break period before you can open a blocked app again.
* **Quick Pause:** Temporarily pause the blocker for a set duration with a background countdown timer.
* **Cyberpunk UI:** Built with fully custom, highly responsive dark/neon theme.

## 📸 Screenshots

| Welcome Screen | Daily Limit Plan Screen | Cooldown period Screen |
| :---: | :---: | :---: |
| <img src="https://github.com/oliviyaa3/Scroll-Lock/blob/main/app/screenshots/Welcome.jpeg" width="250"/> | <img src="https://github.com/oliviyaa3/Scroll-Lock/blob/main/app/screenshots/DailyLimit.jpeg" width="250"/> | <img src="https://github.com/oliviyaa3/Scroll-Lock/blob/main/app/screenshots/CoolDown.jpeg" width="250"/> |
| **Access Permission Screen** | **Daily Limit Plan - Active Screen** | **Block All - Active Screen** |
| :---: | :---: | :---: |
| <img src="https://github.com/oliviyaa3/Scroll-Lock/blob/main/app/screenshots/AccessPermission.jpeg" width="250"/> | <img src="https://github.com/oliviyaa3/Scroll-Lock/blob/main/app/screenshots/DailyLimitMode-Active.jpeg" width="250"/> | <img src="https://github.com/oliviyaa3/Scroll-Lock/blob/main/app/screenshots/BlockAllMode-Active.jpeg" width="250"/> |
| **Block-Specific Apps Screen** | **Pause Mode Active Screen** | 
| :---: | :---: | :---: |
| <img src="https://github.com/oliviyaa3/Scroll-Lock/blob/main/app/screenshots/BlockSpecificApps.jpeg" width="250"/> | <img src="https://github.com/oliviyaa3/Scroll-Lock/blob/main/app/screenshots/PauseMode-Active.jpeg" width="250"/> | 


## 🛠️ Tech Stack & Architecture
This project is built using modern Android development standards and Google's recommended best practices:

* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (100% declarative UI)
* **Architecture:** MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF)
* **Core Engine:** `AccessibilityService API` for background app monitoring and overlay injection
* **State Management:** `StateFlow` and `Coroutines` for reactive, non-blocking UI updates
* **Navigation:** Jetpack Navigation Compose (`NavHost`)
* **Local Storage:** `SharedPreferences` for lightweight, persistent user settings


## 🧠 Under the Hood: The Accessibility Service
The core functionality of ScrollLock relies on the Android Accessibility Service API. Instead of constantly polling the system (which drains battery), the app listens for `TYPE_WINDOW_STATE_CHANGED` events. 

When the user opens a target app, the service instantly checks the `StateFlow` data to see if the daily limit has been reached. If so, it pulls out of the distracting app, preventing further scrolling.

## 🚀 How to Run the Project
1. Clone this repository: `git clone https://github.com/oliviyaa3/ScrollLock.git`
2. Open the project in **Android Studio**.
3. Build and run the app on an emulator or physical device.
4. **Important:** On your first launch, the app will prompt you to enable Accessibility Permissions in your phone's settings. This is required for the app blocker to function!


