package com.example.maghalam.ui.features.AIScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maghalam.model.data.Article
import com.example.maghalam.model.net.dto.ApiResponse
import com.example.maghalam.model.repository.TokenInMemory
import com.example.maghalam.model.repository.article.ArticleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AiViewModel(
    private val repository: ArticleRepository
) : ViewModel() {

    //------------------------------------------------------------------------
    private val _uiState = MutableStateFlow(AiUiState())
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    //------------------------------------------------------------------------
    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, titleError = null) }
    }

    //------------------------------------------------------------------------
    fun onAuthorChange(newAuthor: String) {
        _uiState.update { it.copy(author = newAuthor, authorError = null) }
    }

    //------------------------------------------------------------------------
    fun onKeywordsChange(newKeywords: String) {
        _uiState.update { it.copy(keywords = newKeywords, keywordsError = null) }
    }

    //------------------------------------------------------------------------
    fun onLanguageDropdownToggle() {
        _uiState.update { it.copy(isLanguageDropdownExpanded = !it.isLanguageDropdownExpanded) }
    }

    //------------------------------------------------------------------------
    fun onLanguageSelect(language: String) {
        _uiState.update {
            it.copy(
                selectedLanguage = language,
                isLanguageDropdownExpanded = false
            )
        }
    }

    //------------------------------------------------------------------------
    fun onDescriptionChange(newDescription: String) {
        _uiState.update { it.copy(description = newDescription, descriptionError = null) }
    }

    //------------------------------------------------------------------------
    //authorization of values =>
    private fun validateFields(): Boolean {
        var isValid = true

        if (_uiState.value.title.isBlank()) {
            _uiState.update { it.copy(titleError = "عنوان مقاله الزامی است") }
            isValid = false
        }

        if (_uiState.value.author.isBlank()) {
            _uiState.update { it.copy(authorError = "نام نویسنده الزامی است") }
            isValid = false
        }

        val keywordsList =
            _uiState.value.keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (keywordsList.size < 3) {
            _uiState.update { it.copy(keywordsError = "حداقل ۳ کلمه کلیدی وارد کنید (با کاما جدا کنید)") }
            isValid = false
        }

        if (_uiState.value.description.isBlank()) {
            _uiState.update { it.copy(descriptionError = "توضیحات مقاله الزامی است") }
            isValid = false
        }

        return isValid
    }

    //------------------------------------------------------------------------
    //make summary via AI =>
    private suspend fun generateSummary(description: String, keywords: String): String {
        // شبیه‌سازی تاخیر API
        delay(1500)

        // در واقعیت اینجا باید API Call به سرور AI انجام بشه
        val keywordsList = keywords.split(",").map { it.trim() }.take(3)
        return "این مقاله به بررسی ${keywordsList.joinToString("، ")} می‌پردازد و جنبه‌های مختلف آن را تحلیل می‌کند. ${
            description.take(
                100
            )
        }..."
    }

    //------------------------------------------------------------------------
    //create article =>
    fun createArticle() {
        if (!validateFields()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val currentState = _uiState.value

                // ساخت چکیده توسط AI
                val summary = generateSummary(
                    currentState.description,
                    currentState.keywords
                )

                // شبیه‌سازی تاخیر ساخت مقاله
                delay(2000)

                // ایجاد شیء مقاله
                val article = Article(
                    id = currentState.articleId ?: System.currentTimeMillis(),
                    title = currentState.title,
                    author = currentState.author,
                    keywords = currentState.keywords,
                    language = currentState.selectedLanguage,
                    description = currentState.description,
                    content = currentState.content.ifBlank { currentState.description },
                    abstract = summary,
                    wordCount = currentState.description.split(Regex("\\s+")).count { it.isNotBlank() },
                    userId = currentState.userId ?: TokenInMemory.userId

                )

                // ذخیره موقت مقاله در state و نمایش دیالوگ
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showActionDialog = true,
                        articleId = article.id,
                        generatedArticle = article
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "خطا در ایجاد مقاله: ${e.message}"
                    )
                }
            }
        }
    }

    //------------------------------------------------------------------------
    //dismiss dialog of download article =>
    fun dismissActionDialog() {
        _uiState.update { it.copy(showActionDialog = false) }
        resetForm()
    }

    //------------------------------------------------------------------------
    //just download without share =>
    fun downloadOnly() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showActionDialog = false,
                    isDownloading = true
                )
            }
            try {
                // شبیه‌سازی دانلود
                delay(2000)

                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        isSuccess = true
                    )
                }

                // بعد از 2 ثانیه فرم را ریست کن
                delay(2000)
                resetForm()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        errorMessage = "خطا در دانلود مقاله: ${e.message}"
                    )
                }
            }
        }
    }

    //------------------------------------------------------------------------
    //download and share the article =>
    fun downloadAndPublish() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showActionDialog = false,
                    isPublishing = true
                )
            }

            try {
                val article = _uiState.value.generatedArticle

                if (article != null) {


                    // افزودن مقاله به repository
                    var published = false
                    repository.insertArticle(article).collect { result ->
                        when (result) {
                            ApiResponse.Loading -> Unit
                            is ApiResponse.Success -> {
                                published = true
                                _uiState.update {
                                    it.copy(
                                        isPublishing = false,
                                        isSuccess = true,
                                        successMessage = "مقاله شما با موفقیت در لیست مقالات منتشر شد!"
                                    )
                                }
                            }
                            is ApiResponse.Error -> {
                                _uiState.update {
                                    it.copy(
                                        isPublishing = false,
                                        errorMessage = result.message
                                    )
                                }
                            }
                            ApiResponse.NetworkError -> {
                                _uiState.update {
                                    it.copy(
                                        isPublishing = false,
                                        errorMessage = "لطفا اینترنت خود را چک کنید!"
                                    )
                                }
                            }
                        }
                    }

                    // بعد از 2 ثانیه فرم را ریست کن
                    delay(2000)
                    if (published) {
                        resetForm()
                    }

                } else {
                    throw Exception("مقاله یافت نشد")
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isPublishing = false,
                        errorMessage = "خطا در انتشار مقاله: ${e.message}"
                    )
                }
            }
        }
    }

    //------------------------------------------------------------------------
    //success reset =>
    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    //------------------------------------------------------------------------
    //clear error message =>
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    //------------------------------------------------------------------------
    //clear success message =>
    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    //------------------------------------------------------------------------
    //fully reset =>
    fun resetForm() {
        _uiState.value = AiUiState()
    }


}
