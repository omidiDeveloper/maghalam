package com.example.maghalam.ui.features.AIScreen

import com.example.maghalam.model.data.Article
import com.example.maghalam.model.data.User

data class AiUiState(
    val title: String = "",
    val titleError: String? = null,

    val userId : Long? = null,

    val wordCount: Int = 0,
    val wordCountError: String? = null,

    val author: String = "",
    val authorError: String? = null,

    val keywords: String = "",
    val keywordsError: String? = null,

    val selectedLanguage: String = "فارسی",
    val isLanguageDropdownExpanded: Boolean = false,

    val description: String = "",
    val descriptionError: String? = null,

    val content : String = "",
    val contentError: String? = null,

    val isLoading: Boolean = false,
    val loadingMessage: String? = null,

    val errorMessage: String? = null,

    val isSuccess: Boolean = false,
    val successMessage: String? = null,

    val showActionDialog: Boolean = false,
    val isDownloading: Boolean = false,
    val isPublishing: Boolean = false,
    val articleId: Long? = null,
    val generatedArticle: Article? = null
)

val availableLanguages = listOf(
    "فارسی",
    "English",
    "العربية"
)
