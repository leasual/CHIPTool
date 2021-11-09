package com.se.wiser.compose.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.se.wiser.App
import com.se.wiser.compose.ChipControllerApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@AndroidEntryPoint
class HomeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val appContainer = (application as App).container
        val splashWasDisplayed = savedInstanceState != null
//        if(!splashWasDisplayed) {
            //it should execute before setContent
            val splashScreen = installSplashScreen()
//        }
        setContent {
            ChipControllerApp(appContainer)
        }
    }
}