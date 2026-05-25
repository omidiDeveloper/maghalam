package com.example.maghalam.ui.features.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maghalam.model.repository.user.UserRepository
import com.example.maghalam.utills.coroutinesExceptionHandler
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepo: UserRepository) : ViewModel() {

    val name = MutableLiveData("")
    val userName = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val rePassword = MutableLiveData("")


    fun registerUser(LoggingEvent: (String) -> Unit) {
        viewModelScope.launch(coroutinesExceptionHandler) {
            val result = userRepo.register(
                name = name.value!!,
                username = userName.value!!,
                email = email.value!!,
                password = password.value!!
            )
        }
    }
}