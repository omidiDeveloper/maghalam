package com.example.maghalam.ui.features.AIScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maghalam.model.net.dto.ApiResponse
import com.example.maghalam.model.net.dto.ArticleGenerationRequest
import com.example.maghalam.model.repository.TokenInMemory
import com.example.maghalam.model.repository.article.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AiViewModel(
    private val repository: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiUiState())
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, titleError = null, errorMessage = null) }
    }

    fun onAuthorChange(newAuthor: String) {
        _uiState.update { it.copy(author = newAuthor, authorError = null, errorMessage = null) }
    }

    fun onKeywordsChange(newKeywords: String) {
        _uiState.update { it.copy(keywords = newKeywords, keywordsError = null, errorMessage = null) }
    }

    fun onLanguageDropdownToggle() {
        _uiState.update { it.copy(isLanguageDropdownExpanded = !it.isLanguageDropdownExpanded) }
    }

    fun onLanguageSelect(language: String) {
        _uiState.update {
            it.copy(
                selectedLanguage = language,
                isLanguageDropdownExpanded = false
            )
        }
    }

    fun onDescriptionChange(newDescription: String) {
        _uiState.update { it.copy(description = newDescription, descriptionError = null, errorMessage = null) }
    }

    private fun validateFields(): Boolean {
        val current = _uiState.value
        val errors = mutableListOf<String>()
        val keywords = current.keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        val titleError = if (current.title.isBlank()) "عنوان مقاله الزامی است" else null
        val authorError = if (current.author.isBlank()) "نام نویسنده الزامی است" else null
        val keywordsError = if (keywords.size < 3) "حداقل ۳ کلمه کلیدی وارد کنید و آن‌ها را با کاما جدا کنید" else null
        val descriptionError = if (current.description.isBlank()) "توضیحات مقاله الزامی است" else null

        listOfNotNull(titleError, authorError, keywordsError, descriptionError).let { errors.addAll(it) }

        _uiState.update {
            it.copy(
                titleError = titleError,
                authorError = authorError,
                keywordsError = keywordsError,
                descriptionError = descriptionError,
                errorMessage = errors.takeIf { list -> list.isNotEmpty() }?.joinToString("\n")
            )
        }

        return errors.isEmpty()
    }

    fun createArticle() {
        if (!validateFields()) return

        viewModelScope.launch {
            val current = _uiState.value
            val keywords = current.keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            repository.generateArticle(
                ArticleGenerationRequest(
                    title = current.title.trim(),
                    author = current.author.trim(),
                    keywords = keywords,
                    language = current.selectedLanguage,
                    description = current.description.trim()
                )
            ).collect { result ->
                when (result) {
                    ApiResponse.Loading -> _uiState.update {
                        it.copy(
                            isLoading = true,
                            loadingMessage = "در حال ساخت مقاله...",
                            errorMessage = null,
                            successMessage = null
                        )
                    }
                    is ApiResponse.Success -> {
                        val article = result.data.copy(
                            userId = result.data.userId ?: TokenInMemory.userId,
                            isPublished = false
                        )
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loadingMessage = null,
                                showActionDialog = true,
                                articleId = article.id,
                                generatedArticle = article,
                                successMessage = "مقاله با موفقیت ساخته شد"
                            )
                        }
                    }
                    is ApiResponse.Error -> _uiState.update {
                        it.copy(isLoading = false, loadingMessage = null, errorMessage = result.message)
                    }
                    ApiResponse.NetworkError -> _uiState.update {
                        it.copy(isLoading = false, loadingMessage = null, errorMessage = "اینترنت در دسترس نیست")
                    }
                }
            }
        }
    }

    fun dismissActionDialog() {
        _uiState.update { it.copy(showActionDialog = false) }
    }

    fun downloadOnly() {
        viewModelScope.launch {
            val article = _uiState.value.generatedArticle
            if (article == null) {
                _uiState.update { it.copy(errorMessage = "مقاله‌ای برای دانلود پیدا نشد") }
                return@launch
            }

            repository.downloadArticle(article, "pdf").collect { result ->
                when (result) {
                    ApiResponse.Loading -> _uiState.update {
                        it.copy(showActionDialog = false, isDownloading = true, errorMessage = null)
                    }
                    is ApiResponse.Success -> _uiState.update {
                        it.copy(
                            isDownloading = false,
                            successMessage = "دانلود با موفقیت انجام شد:\n${result.data.absolutePath}"
                        )
                    }
                    is ApiResponse.Error -> _uiState.update {
                        it.copy(isDownloading = false, errorMessage = result.message)
                    }
                    ApiResponse.NetworkError -> _uiState.update {
                        it.copy(isDownloading = false, errorMessage = "اینترنت در دسترس نیست")
                    }
                }
            }
        }
    }

    fun downloadAndPublish() {
        viewModelScope.launch {
            val article = _uiState.value.generatedArticle
            if (article == null) {
                _uiState.update { it.copy(errorMessage = "مقاله‌ای برای انتشار پیدا نشد") }
                return@launch
            }

            repository.insertArticle(article).collect { result ->
                when (result) {
                    ApiResponse.Loading -> _uiState.update {
                        it.copy(showActionDialog = false, isPublishing = true, errorMessage = null)
                    }
                    is ApiResponse.Success -> _uiState.update {
                        it.copy(
                            isPublishing = false,
                            isSuccess = true,
                            successMessage = "مقاله شما با موفقیت منتشر شد و به لیست مقاله‌ها اضافه شد."
                        )
                    }
                    is ApiResponse.Error -> _uiState.update {
                        it.copy(isPublishing = false, errorMessage = result.message)
                    }
                    ApiResponse.NetworkError -> _uiState.update {
                        it.copy(isPublishing = false, errorMessage = "اینترنت در دسترس نیست")
                    }
                }
            }
        }
    }

    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun resetForm() {
        _uiState.value = AiUiState()
    }
}
