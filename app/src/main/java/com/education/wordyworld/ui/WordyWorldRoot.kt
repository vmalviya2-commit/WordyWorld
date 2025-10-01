package com.education.wordyworld.ui

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.education.wordyworld.ui.splash.SplashScreen
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 1800L

@Composable
fun WordyWorldRoot() {
    var showSplash by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        showSplash = false
    }

    Crossfade(targetState = showSplash, label = "wordyworld_root") { isSplashVisible ->
        if (isSplashVisible) {
            SplashScreen()
        } else {
            WordyWorldApp()
        }
    }
}
