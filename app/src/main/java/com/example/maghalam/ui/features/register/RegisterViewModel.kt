package com.example.maghalam.ui.features.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maghalam.model.net.dto.ApiResponse
import com.example.maghalam.model.net.dto.RegisterRequest
import com.example.maghalam.model.repository.user.UserRepository
import com.example.maghalam.utills.coroutinesExceptionHandler
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    val name = MutableLiveData("")
    val username = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val confirmPassword = MutableLiveData("")


    fun registerUser(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(coroutinesExceptionHandler) {
            userRepository.register(
                RegisterRequest(
                    fullName = name.value.orEmpty().trim(),
                    username = username.value.orEmpty().trim(),
                    email = email.value.orEmpty().trim(),
                    password = password.value.orEmpty(),
                    confirmPassword = confirmPassword.value.orEmpty()
                )
            ).collect { result ->
                when (result) {
                    ApiResponse.Loading -> Unit
                    is ApiResponse.Success -> onSuccess()
                    is ApiResponse.Error -> onError(result.message)
                    ApiResponse.NetworkError -> onError("Network connection failed")
                }
            }
        }
    }
}
