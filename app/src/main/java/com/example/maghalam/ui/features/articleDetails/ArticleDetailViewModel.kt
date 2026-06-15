package com.example.maghalam.ui.features.articleDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maghalam.model.data.Article
import com.example.maghalam.ui.features.AIScreen.AiUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ArticleDetailViewModel : ViewModel() {
    val _uiState = MutableStateFlow(AiUiState())

    private val _article = MutableStateFlow<Article?>(null)
    val article: StateFlow<Article?> = _article.asStateFlow()

    private val _isApproved = MutableStateFlow(false)
    val isApproved: StateFlow<Boolean> = _isApproved.asStateFlow()

    fun getArticleById(articleId: String): Flow<Article?> = flow {
        // شبیه‌سازی بارگذاری از دیتابیس یا API
        val article = Article(
            id = articleId.toLong(),
            title = "عنوان مقاله شماره $articleId",
            abstract = """
                لورم ایپسوم متن ساختگی با تولید سادگی نامفهوم از صنعت چاپ و با استفاده از طراحان گرافیک است. چاپگرها و متون بلکه روزنامه و مجله در ستون و سطرآنچنان که لازم است و برای شرایط فعلی تکنولوژی مورد نیاز و کاربردهای متنوع با هدف بهبود ابزارهای کاربردی می باشد.
                
                کتابهای زیادی در شصت و سه درصد گذشته، حال و آینده شناخت فراوان جامعه و متخصصان را می طلبد تا با نرم افزارها شناخت بیشتری را برای طراحان رایانه ای علی الخصوص طراحان خلاقی و فرهنگ پیشرو در زبان فارسی ایجاد کرد.
                
                در این صورت می توان امید داشت که تمام و دشواری موجود در ارائه راهکارها و شرایط سخت تایپ به پایان رسد وزمان مورد نیاز شامل حروفچینی دستاوردهای اصلی و جوابگوی سوالات پیوسته اهل دنیای موجود طراحی اساسا مورد استفاده قرار گیرد.
            """.trimIndent(),
            keywords = listOf("هوش مصنوعی", "علم", "داده").toString(),
            author = "محمد امیدی",
            createdAt = 1403021500L.toString(),
            language = "فارسی",
            description = "",
            isPublished = false,
            wordCount = 300,
            userId = 100
        )

        _article.value = article
        _isApproved.value = article.isPublished

        emit(article)
    }

    fun loadArticle(articleId: String) {
        viewModelScope.launch {
            _article.value = Article(
                id = articleId.toLong(),
                title = "عنوان مقاله شماره $articleId",
                abstract = """
                    لورم ایپسوم متن ساختگی با تولید سادگی نامفهوم از صنعت چاپ و با استفاده از طراحان گرافیک است. چاپگرها و متون بلکه روزنامه و مجله در ستون و سطرآنچنان که لازم است و برای شرایط فعلی تکنولوژی مورد نیاز و کاربردهای متنوع با هدف بهبود ابزارهای کاربردی می باشد.
                    
                    کتابهای زیادی در شصت و سه درصد گذشته، حال و آینده شناخت فراوان جامعه و متخصصان را می طلبد تا با نرم افزارها شناخت بیشتری را برای طراحان رایانه ای علی الخصوص طراحان خلاقی و فرهنگ پیشرو در زبان فارسی ایجاد کرد.
                    
                    در این صورت می توان امید داشت که تمام و دشواری موجود در ارائه راهکارها و شرایط سخت تایپ به پایان رسد وزمان مورد نیاز شامل حروفچینی دستاوردهای اصلی و جوابگوی سوالات پیوسته اهل دنیای موجود طراحی اساسا مورد استفاده قرار گیرد.
                """.trimIndent(),
                keywords = listOf("هوش مصنوعی", "علم", "داده").toString(),
                author = "محمد امیدی",
                createdAt = 1403021500L.toString(),
                language = "فارسی",
                description = "",
                isPublished = false,
                wordCount = 300,
                userId = 100L
            )
            _isApproved.value = _article.value?.isPublished ?: false
        }
    }

    fun approveArticle() {
        viewModelScope.launch {
            _isApproved.value = true
            _article.value = _article.value?.copy(isPublished = true)
        }
    }

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

    //fully reset =>
    fun resetForm() {
        _uiState.value = AiUiState()
    }

    fun rejectArticle() {
        viewModelScope.launch {
            _isApproved.value = false
            _article.value = _article.value?.copy(isPublished = false)
        }
    }
}
