package com.example.maghalam.ui.features.startup

import androidx.lifecycle.ViewModel
import com.example.maghalam.utills.SharedPreferencesManager
import com.example.maghalam.utills.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StartupViewModel(
    private val preferences: SharedPreferencesManager
) : ViewModel() {

    private val _isAdmin = MutableStateFlow(preferences.isAdmin())
    val isAdmin = _isAdmin.asStateFlow()

    fun firstDestinationAfterSplash(): String {
        return when {
            !preferences.hasSeenIntro() -> Screens.IntroScreen.rute
            preferences.isLoggedIn() -> Screens.AiScreen.rute
            else -> Screens.LoginScreen.rute
        }
    }

    fun completeIntro(): String {
        preferences.markIntroSeen()
        return if (preferences.isLoggedIn()) Screens.AiScreen.rute else Screens.LoginScreen.rute
    }

    fun refreshRole() {
        _isAdmin.value = preferences.isAdmin()
    }
}
