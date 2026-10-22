
package com.example.monitoreosilos.settings

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(val label: String) {
    SYSTEM("Automático"),
    LIGHT("Claro"),
    DARK("Oscuro")
}

object ThemePreferences {

    private const val PREFS_NAME = "silocontrol_settings"
    private const val THEME_KEY = "theme_mode"

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)

    val themeMode: StateFlow<ThemeMode> =
        _themeMode.asStateFlow()

    fun init(context: Context) {
        val prefs = context.applicationContext
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        val savedMode = prefs.getString(
            THEME_KEY,
            ThemeMode.SYSTEM.name
        )

        _themeMode.value = ThemeMode.entries
            .firstOrNull { it.name == savedMode }
            ?: ThemeMode.SYSTEM
    }

    fun setTheme(context: Context, mode: ThemeMode) {
        val prefs = context.applicationContext
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        prefs.edit()
            .putString(THEME_KEY, mode.name)
            .apply()

        _themeMode.value = mode
    }
}
