package com.scrollblocker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import com.scrollblocker.data.preferences.AppPreferences
import com.scrollblocker.navigation.AppNavGraph
import com.scrollblocker.service.ScrollBlockService
import com.scrollblocker.ui.theme.NoScrollTheme
import com.scrollblocker.util.PermissionUtils
import com.scrollblocker.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val prefs = AppPreferences(applicationContext)
        val factory = MainViewModel.Factory(prefs)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setContent {
            NoScrollTheme {
                val lifecycleOwner = LocalLifecycleOwner.current

                // This perfectly handles the user returning from the Android Settings screen!
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            checkPermission()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                AppNavGraph(viewModel = viewModel)
            }
        }
    }

    private fun checkPermission() {
        val isEnabled = PermissionUtils.isAccessibilityServiceEnabled(
            this,
            ScrollBlockService::class.java
        )
        viewModel.updateAccessibilityPermissionState(isEnabled)
    }
}
