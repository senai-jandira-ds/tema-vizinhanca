package com.example.mobilevizinhaa.ui.theme.configuraçoes


import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ThemeManager {
    private const val PREFS_NAME = "vizinhanca_theme_prefs"
    private const val KEY_IS_DARK = "is_dark_mode"

    private lateinit var prefs: SharedPreferences

    // Flow reativo que o Compose vai observar para mudar de cor em tempo real
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isDarkMode.value = prefs.getBoolean(KEY_IS_DARK, false)
    }

    fun setDarkMode(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DARK, isDark).apply()
        _isDarkMode.value = isDark
    }
}