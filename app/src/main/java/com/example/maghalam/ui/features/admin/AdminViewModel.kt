package com.example.maghalam.ui.features.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maghalam.model.data.Article
import com.example.maghalam.model.data.User
import com.example.maghalam.model.net.dto.ApiResponse
import com.example.maghalam.model.repository.article.ArticleRepository
import com.example.maghalam.model.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val articles: List<Article> = emptyList(),
    val message: String? = null,
    val error: String? = null
)

class AdminViewModel(
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        loadUsers()
        loadArticles()
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            userRepository.deleteUser(userId).collect { result ->
                when (result) {
                    ApiResponse.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is ApiResponse.Success -> {
                        _uiState.value = _uiState.value.copy(message = "کاربر حذف شد", isLoading = false)
                        loadUsers()
                    }
                    is ApiResponse.Error -> _uiState.value = _uiState.value.copy(error = result.message, isLoading = false)
                    ApiResponse.NetworkError -> _uiState.value = _uiState.value.copy(error = "اینترنت در دسترس نیست", isLoading = false)
                }
            }
        }
    }

    fun deleteArticle(articleId: Long) {
        viewModelScope.launch {
            articleRepository.deleteArticle(articleId).collect { result ->
                when (result) {
                    ApiResponse.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is ApiResponse.Success -> {
                        _uiState.value = _uiState.value.copy(message = "مقاله حذف شد", isLoading = false)
                        loadArticles()
                    }
                    is ApiResponse.Error -> _uiState.value = _uiState.value.copy(error = result.message, isLoading = false)
                    ApiResponse.NetworkError -> _uiState.value = _uiState.value.copy(error = "اینترنت در دسترس نیست", isLoading = false)
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }

    private fun loadUsers() {
        viewModelScope.launch {
            userRepository.getUsers().collect { result ->
                when (result) {
                    ApiResponse.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is ApiResponse.Success -> _uiState.value = _uiState.value.copy(users = result.data, isLoading = false)
                    is ApiResponse.Error -> _uiState.value = _uiState.value.copy(error = result.message, isLoading = false)
                    ApiResponse.NetworkError -> _uiState.value = _uiState.value.copy(error = "اینترنت در دسترس نیست", isLoading = false)
                }
            }
        }
    }

    private fun loadArticles() {
        viewModelScope.launch {
            articleRepository.getArticles().collect { result ->
                when (result) {
                    ApiResponse.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is ApiResponse.Success -> _uiState.value = _uiState.value.copy(articles = result.data, isLoading = false)
                    is ApiResponse.Error -> _uiState.value = _uiState.value.copy(error = result.message, isLoading = false)
                    ApiResponse.NetworkError -> _uiState.value = _uiState.value.copy(error = "اینترنت در دسترس نیست", isLoading = false)
                }
            }
        }
    }
}
