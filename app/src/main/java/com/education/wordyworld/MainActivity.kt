package com.education.wordyworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.education.wordyworld.ui.theme.WordyWorldTheme
import com.education.wordyworld.ui.WordyWorldApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordyWorldTheme {
                WordyWorldApp()
            }
        }
    }
}