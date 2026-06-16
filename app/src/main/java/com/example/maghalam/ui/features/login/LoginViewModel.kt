package com.example.maghalam.ui.features.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maghalam.model.net.dto.ApiResponse
import com.example.maghalam.model.net.dto.LoginRequest
import com.example.maghalam.model.repository.user.UserRepository
import com.example.maghalam.utills.coroutinesExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel (
    private val userRepository: UserRepository
) : ViewModel() {

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun login(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        viewModelScope.launch(coroutinesExceptionHandler) {
            userRepository.login(
                LoginRequest(
                    username = email.value.orEmpty().trim(),
                    password = password.value.orEmpty()
                )
            ).collect { result ->
                when (result) {
                    ApiResponse.Loading -> {
                        _uiState.value = LoginUiState(isLoading = true)
                    }
                    is ApiResponse.Success -> {
                        _uiState.value = LoginUiState(isSuccess = true)
                        onSuccess()
                    }
                    is ApiResponse.Error -> {
                        _uiState.value = LoginUiState(error = result.message)
                        onError(result.message)
                    }
                    ApiResponse.NetworkError -> {
                        val message = "Network connection failed"
                        _uiState.value = LoginUiState(error = message)
                        onError(message)
                    }
                }
            }
        }
    }
}
