// presentation/items/ItemsViewModel.kt
package com.example.maghalam.ui.features.Items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maghalam.model.data.Article
import com.example.maghalam.model.repository.article.ArticleRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ویومدل مدیریت لیست مقالات
 */
class ItemsViewModel : ViewModel() {

    private val repository = ArticleRepository.getInstance()

    // لیست مقالات
    val articles: StateFlow<List<Article>> = repository.articles

    /**
     * حذف مقاله
     */
    fun deleteArticle(articleId: String) {
        viewModelScope.launch {
            repository.removeArticle(articleId)
        }
    }

    /**
     * دریافت مقاله با شناسه
     */
    fun getArticleById(articleId: String): Article? {
        return repository.getArticleById(articleId)
    }
}
