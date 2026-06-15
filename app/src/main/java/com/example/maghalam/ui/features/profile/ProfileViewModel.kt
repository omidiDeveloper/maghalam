package com.example.maghalam.ui.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val preferences: SharedPreferencesManager
) : ViewModel() {


    private val _userProfile =
        MutableStateFlow(UserProfile())

    val userProfile: StateFlow<UserProfile> =
        _userProfile.asStateFlow()



    private val _articlesCount =
        MutableStateFlow(0)

    val articlesCount: StateFlow<Int> =
        _articlesCount.asStateFlow()



    private val _isDarkMode =
        MutableStateFlow(false)

    val isDarkMode =
        _isDarkMode.asStateFlow()



    private val _selectedFont =
        MutableStateFlow("Vazir")

    val selectedFont =
        _selectedFont.asStateFlow()



    init {

        loadUserProfile()

        loadSettings()

    }



    private fun loadUserProfile() {

        viewModelScope.launch {

            val username =
                preferences.getUsername() ?: ""

            _userProfile.value =
                UserProfile(
                    fullName = username,
                    username = username,
                    email = ""
                )
        }
    }




    private fun loadSettings() {

        _isDarkMode.value =
            preferences.getDarkMode()


        _selectedFont.value =
            preferences.getFontSize()

    }




    fun toggleDarkMode() {

        val newValue =
            !_isDarkMode.value


        _isDarkMode.value =
            newValue


        preferences.saveDarkMode(newValue)

    }




    fun setFont(font: String) {

        _selectedFont.value =
            font


        preferences.saveFontSize(font)

    }




    fun logout() {

        viewModelScope.launch {

            preferences.clearAll()


            _userProfile.value =
                UserProfile()


            _isDarkMode.value =
                false


            _selectedFont.value =
                "Vazir"
        }
    }





    fun incrementArticlesCount() {

        _articlesCount.value++

    }

}