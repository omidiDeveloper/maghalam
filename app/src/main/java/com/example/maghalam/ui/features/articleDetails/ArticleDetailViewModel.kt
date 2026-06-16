package com.example.maghalam.ui.features.articleDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maghalam.model.data.Article
import com.example.maghalam.model.net.dto.ApiResponse
import com.example.maghalam.model.repository.article.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ArticleDetailUiState(
    val isLoading: Boolean = true,
    val isDownloading: Boolean = false,
    val article: Article? = null,
    val downloadPath: String? = null,
    val error: String? = null
)

class ArticleDetailViewModel(
    private val repository: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadArticle(articleId: String) {
        val id = articleId.toLongOrNull()
        if (id == null) {
            _uiState.value = ArticleDetailUiState(isLoading = false, error = "شناسه مقاله معتبر نیست")
            return
        }

        viewModelScope.launch {
            repository.getArticleById(id).collect { result ->
                when (result) {
                    ApiResponse.Loading -> _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    is ApiResponse.Success -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        article = result.data,
                        error = null
                    )
                    is ApiResponse.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                    ApiResponse.NetworkError -> _uiState.value = _uiState.value.copy(isLoading = false, error = "اینترنت در دسترس نیست")
                }
            }
        }
    }

    fun downloadPdf() {
        val article = _uiState.value.article ?: return

        viewModelScope.launch {
            repository.downloadArticle(article, "pdf").collect { result ->
                when (result) {
                    ApiResponse.Loading -> _uiState.value = _uiState.value.copy(isDownloading = true, error = null)
                    is ApiResponse.Success -> _uiState.value = _uiState.value.copy(
                        isDownloading = false,
                        downloadPath = result.data.absolutePath
                    )
                    is ApiResponse.Error -> _uiState.value = _uiState.value.copy(isDownloading = false, error = result.message)
                    ApiResponse.NetworkError -> _uiState.value = _uiState.value.copy(isDownloading = false, error = "اینترنت در دسترس نیست")
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(downloadPath = null, error = null)
    }
}
