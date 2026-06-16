package com.example.maghalam.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maghalam.model.net.dto.ApiResponse
import com.example.maghalam.model.repository.article.ArticleRepository
import com.example.maghalam.model.repository.user.UserRepository
import com.example.maghalam.utills.SharedPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserProfile(
    val fullName: String = "",
    val username: String = "",
    val email: String = ""
)

class ProfileViewModel(
    private val preferences: SharedPreferencesManager,
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _articlesCount = MutableStateFlow(0)
    val articlesCount: StateFlow<Int> = _articlesCount.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _selectedFont = MutableStateFlow("Vazir")
    val selectedFont = _selectedFont.asStateFlow()

    init {
        loadSettings()
        loadUserProfile()
        loadArticlesCount()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _userProfile.value = UserProfile(
                fullName = preferences.getFullName(),
                username = preferences.getUsername().orEmpty(),
                email = preferences.getEmail()
            )

            userRepository.getUserProfile().collect { result ->
                if (result is ApiResponse.Success) {
                    val user = result.data
                    _userProfile.value = UserProfile(
                        fullName = user.fullName,
                        username = user.username,
                        email = user.email
                    )
                    preferences.saveUserInfo(
                        username = user.username,
                        userId = user.id ?: preferences.getUserId(),
                        role = user.role,
                        fullName = user.fullName,
                        email = user.email
                    )
                }
            }
        }
    }

    private fun loadArticlesCount() {
        viewModelScope.launch {
            val currentUserId = preferences.getUserId()
            articleRepository.getArticles().collect { result ->
                if (result is ApiResponse.Success) {
                    _articlesCount.value = result.data.count { article ->
                        article.isPublished && (currentUserId <= 0L || article.userId == currentUserId)
                    }
                }
            }
        }
    }

    private fun loadSettings() {
        _isDarkMode.value = preferences.getDarkMode()
        _selectedFont.value = preferences.getFontSize()
    }

    fun toggleDarkMode() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        preferences.saveDarkMode(newValue)
    }

    fun setFont(font: String) {
        _selectedFont.value = font
        preferences.saveFontSize(font)
    }

    fun logout(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            userRepository.logout().collect { result ->
                if (result is ApiResponse.Success || result is ApiResponse.NetworkError || result is ApiResponse.Error) {
                    preferences.clearAll()
                    _userProfile.value = UserProfile()
                    _isDarkMode.value = false
                    _selectedFont.value = "Vazir"
                    _articlesCount.value = 0
                    onComplete()
                }
            }
        }
    }
}
