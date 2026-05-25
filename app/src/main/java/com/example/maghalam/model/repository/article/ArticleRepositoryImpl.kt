package com.example.maghalam.model.repository.article

import ArticleRepository
import com.example.maghalam.model.data.Article
import com.example.maghalam.model.db.dao.ArticleDao
import com.example.maghalam.model.db.entity.toArticle
import com.example.maghalam.model.db.entity.toEntity
import com.example.maghalam.model.net.api.ApiResponse
import com.example.maghalam.model.net.api.ArticleApiService
import com.example.maghalam.model.net.dto.request.ArticleGenerationRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class ArticleRepositoryImpl(
    private val articleApiService: ArticleApiService,
    private val articleDao: ArticleDao
) : ArticleRepository {



    // این تابع جداست: فقط sync می‌کنه
    suspend fun syncArticles(): ApiResponse<Unit> {
        return try {
            val response = articleApiService.getArticles()
            if (response.isSuccessful) {
                val articles = response.body() ?: emptyList()
                // ذخیره در Room
                articleDao.insertArticles(articles.map { it.toEntity() })
                ApiResponse.Success(Unit)
            } else {
                ApiResponse.Error(response.code(), response.message())
            }
        } catch (e: IOException) {
            // اینترنت قطعه، از cache استفاده می‌شه
            ApiResponse.Error(-1, "No internet connection")
        } catch (e: HttpException) {
            ApiResponse.Error(e.code(), e.message())
        }
    }

    override fun getArticles(page: Int, size: Int): Flow<ApiResponse<List<Article>>> = flow {
        emit(ApiResponse.Loading)

        // ۱. داده لوکال رو فوری بده
        val localArticles = articleDao.getAllArticles()
        localArticles.collect { entities ->
            if (entities.isNotEmpty()) {
                emit(ApiResponse.Success(entities.map { it.toArticle() }))
            }

            // ۲. از network بگیر و cache کن
            try {
                val response = articleApiService.getArticles(page, size)
                if (response.isSuccessful) {
                    val articles = response.body() ?: emptyList()
                    articleDao.insertArticles(articles.map { it.toEntity() })
                    // Room Flow خودش آپدیت می‌شه
                } else {
                    if (entities.isEmpty()) {
                        emit(ApiResponse.Error(response.code(), response.message()))
                    }
                }
            } catch (e: IOException) {
                if (entities.isEmpty()) {
                    emit(ApiResponse.Error(-1, "اینترنت در دسترس نیست"))
                }
                // اگه cache داریم، همون رو نشون می‌ده
            }
        }
    }

    override fun getArticleById(id: Long): Flow<ApiResponse<Article>> = flow {
        emit(ApiResponse.Loading)

        // ۱. از cache بخون
        val cached = articleDao.getArticleById(id)
        if (cached != null) {
            emit(ApiResponse.Success(cached.toArticle()))
        }

        // ۲. از network آپدیت کن
        try {
            val response = articleApiService.getArticleById(id)
            if (response.isSuccessful) {
                response.body()?.let { article ->
                    articleDao.insertArticle(article.toEntity())
                    emit(ApiResponse.Success(article))
                }
            } else {
                if (cached == null) {
                    emit(ApiResponse.Error(response.code(), response.message()))
                }
            }
        } catch (e: IOException) {
            if (cached == null) {
                emit(ApiResponse.Error(-1, "اینترنت در دسترس نیست"))
            }
        }
    }

    override fun searchArticles(
        query: String,
        page: Int,
        size: Int
    ): Flow<ApiResponse<List<Article>>> = flow {
        emit(ApiResponse.Loading)

        // ۱. جستجو در cache
        articleDao.searchArticles(query).collect { localResults ->
            if (localResults.isNotEmpty()) {
                emit(ApiResponse.Success(localResults.map { it.toArticle() }))
            }

            // ۲. جستجو در network
            try {
                val response = articleApiService.searchArticles(query, page, size)
                if (response.isSuccessful) {
                    val articles = response.body() ?: emptyList()
                    articleDao.insertArticles(articles.map { it.toEntity() })
                    emit(ApiResponse.Success(articles))
                } else {
                    if (localResults.isEmpty()) {
                        emit(ApiResponse.Error(response.code(), response.message()))
                    }
                }
            } catch (e: IOException) {
                if (localResults.isEmpty()) {
                    emit(ApiResponse.Error(-1, "اینترنت در دسترس نیست"))
                }
            } catch (e: HttpException) {
                if (localResults.isEmpty()) {
                    emit(ApiResponse.Error(e.code(), e.message()))
                }
            }
        }
    }


    override fun generateArticle(
        request: ArticleGenerationRequest
    ): Flow<ApiResponse<Article>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = articleApiService.generateArticle(request)
            if (response.isSuccessful) {
                response.body()?.let { article ->
                    // مقاله generate شده رو هم cache می‌کنیم
                    articleDao.insertArticle(article.toEntity())
                    emit(ApiResponse.Success(article))
                } ?: emit(ApiResponse.Error(-1, "پاسخ خالی از سرور"))
            } else {
                emit(ApiResponse.Error(response.code(), response.message()))
            }
        } catch (e: IOException) {
            emit(ApiResponse.Error(-1, "اینترنت در دسترس نیست"))
        } catch (e: HttpException) {
            emit(ApiResponse.Error(e.code(), e.message()))
        }
    }


    override fun createArticle(
        title: String,
        content: String,
        summary: String?,
        keywords: String?
    ): Flow<ApiResponse<Article>> = flow {
        emit(ApiResponse.Loading)
        try {
            val body = HashMap<String, String>().apply {
                put("title", title)
                put("keywords", keywords!!)
                put("language", summary!!)
                put("description", content)
            }
            val response = articleApiService.createArticle(body)
            if (response.isSuccessful) {
                response.body()?.let { article ->
                    // ذخیره مقاله جدید در Room
                    articleDao.insertArticle(article.toEntity())
                    emit(ApiResponse.Success(article))
                }
            } else {
                emit(ApiResponse.Error(response.code(), response.message()))
            }
        } catch (e: IOException) {
            emit(ApiResponse.Error(-1, "اینترنت در دسترس نیست"))
        } catch (e: HttpException) {
            emit(ApiResponse.Error(e.code(), e.message()))
        }
    }
    /** update and delete articles =>

    override fun updateArticle(id: Long, article: Article): Flow<ApiResponse<Article>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = articleApiService.updateArticle(id, article)
            if (response.isSuccessful) {
                response.body()?.let { updated ->
                    // آپدیت در Room
                    articleDao.updateArticle(updated.toEntity())
                    emit(ApiResponse.Success(updated))
                }
            } else {
                emit(ApiResponse.Error(response.code(), response.message()))
            }
        } catch (e: IOException) {
            emit(ApiResponse.Error(-1, "اینترنت در دسترس نیست"))
        }
    }

    override fun deleteArticle(id: Long): Flow<ApiResponse<Unit>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = articleApiService.deleteArticle(id)
            if (response.isSuccessful) {
                // حذف از Room
                articleDao.deleteArticleById(id)
                emit(ApiResponse.Success(Unit))
            } else {
                emit(ApiResponse.Error(response.code(), response.message()))
            }
        } catch (e: IOException) {
            emit(ApiResponse.Error(-1, "اینترنت در دسترس نیست"))
        }
    }
    */
}
