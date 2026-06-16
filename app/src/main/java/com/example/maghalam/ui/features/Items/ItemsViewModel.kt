package com.example.maghalam.ui.features.Items

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maghalam.model.data.Article
import com.example.maghalam.model.net.dto.ApiResponse
import com.example.maghalam.model.repository.article.ArticleRepository
import com.example.maghalam.ui.features.AIScreen.AiUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutionException

/**
 * ویومدل مدیریت لیست مقالات
 */
class ItemsViewModel(
    private val repository: ArticleRepository
) : ViewModel() {

    //------------------------------------------------------------------------
    private val _navigateToArticle = MutableSharedFlow<Long>()
    val navigateToArticle = _navigateToArticle.asSharedFlow()

    //------------------------------------------------------------------------
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val article = _articles.asStateFlow()

    //------------------------------------------------------------------------
    private val _deleteState = MutableStateFlow<ApiResponse<Boolean>?>(null)

    //------------------------------------------------------------------------
    val deleteState = _deleteState.asStateFlow()

    //------------------------------------------------------------------------
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadArticles()
    }

    // Keeps the list synchronized with repository/cache updates.
    fun loadArticles() {
        viewModelScope.launch {
            repository.getArticles().collect { result ->
                when (result) {
                    ApiResponse.Loading -> _isLoading.value = true
                    is ApiResponse.Success -> {
                        _articles.value = result.data
                        _isLoading.value = false
                        _error.value = null
                    }
                    is ApiResponse.Error -> {
                        _isLoading.value = false
                        _error.value = result.message
                    }
                    ApiResponse.NetworkError -> {
                        _isLoading.value = false
                        _error.value = "اینترنت در دسترس نیست"
                    }
                }
            }
        }
    }

    //------------------------------------------------------------------------
    fun deleteArticle(articleId: Long) {

        viewModelScope.launch {

            repository.deleteArticle(articleId)
                .collect { result ->

                    _deleteState.value = result
                    if (result is ApiResponse.Success) {
                        _articles.value = _articles.value.filterNot { it.id == articleId }
                    }

                }
        }
    }

    //------------------------------------------------------------------------
    fun onArticleClicked(articleId: Long) {
        viewModelScope.launch {
            _navigateToArticle.emit(articleId)
        }
    }

}
