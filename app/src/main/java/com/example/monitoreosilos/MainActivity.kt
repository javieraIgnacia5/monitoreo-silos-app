package com.example.monitoreosilos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.monitoreosilos.ui.navigation.AppNavigation
import com.example.monitoreosilos.ui.theme.MonitoreoSilosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MonitoreoSilosTheme {
                AppNavigation()
            }
        }
    }
}