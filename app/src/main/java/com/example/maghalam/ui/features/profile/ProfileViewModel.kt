// ProfileViewModel.kt
package com.example.maghalam.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserProfile(
    val fullName: String = "",
    val username: String = "",
    val email: String = ""
)

class ProfileViewModel : ViewModel() {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _articlesCount = MutableStateFlow(0)
    val articlesCount: StateFlow<Int> = _articlesCount.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _selectedFont = MutableStateFlow("Vazir")
    val selectedFont: StateFlow<String> = _selectedFont.asStateFlow()

    init {
        loadUserProfile()
        loadArticlesCount()
        loadSettings()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            // TODO: بارگذاری اطلاعات کاربر از دیتابیس یا SharedPreferences
            _userProfile.value = UserProfile(
                fullName = "محمد امیدی",
                username = "mamad_omidi",
                email = "mmd@m.com"
            )
        }
    }

    private fun loadArticlesCount() {
        viewModelScope.launch {
            // TODO: بارگذاری تعداد مقالات از دیتابیس
            _articlesCount.value = 0
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // TODO: بارگذاری تنظیمات از SharedPreferences
            // _isDarkMode.value = preferences.getBoolean("dark_mode", false)
            // _selectedFont.value = preferences.getString("font", "Vazir") ?: "Vazir"
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            _isDarkMode.value = !_isDarkMode.value
            // TODO: ذخیره در SharedPreferences
            // preferences.edit().putBoolean("dark_mode", _isDarkMode.value).apply()
        }
    }

    fun setFont(font: String) {
        viewModelScope.launch {
            _selectedFont.value = font
            // TODO: ذخیره در SharedPreferences
            // preferences.edit().putString("font", font).apply()
        }
    }

    fun logout() {
        viewModelScope.launch {
            // TODO: پاک کردن اطلاعات کاربر از SharedPreferences
            // preferences.edit().clear().apply()
        }
    }

    fun incrementArticlesCount() {
        viewModelScope.launch {
            _articlesCount.value += 1
            // TODO: ذخیره در دیتابیس یا SharedPreferences
        }
    }

}
