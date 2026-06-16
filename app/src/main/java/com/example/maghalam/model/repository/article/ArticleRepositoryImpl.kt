package com.example.maghalam.model.repository.article

import android.content.Context
import android.os.Environment
import com.example.maghalam.model.data.Article
import com.example.maghalam.model.db.dao.ArticleDao
import com.example.maghalam.model.db.entity.toArticle
import com.example.maghalam.model.db.entity.toEntity
import com.example.maghalam.model.net.api.ApiService
import com.example.maghalam.model.net.dto.ApiResponse
import com.example.maghalam.model.net.dto.ArticleGenerationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException

class ArticleRepositoryImpl(
    private val articleApiService: ApiService,
    private val articleDao: ArticleDao,
    private val context: Context
) : ArticleRepository {

    override fun getArticles(): Flow<ApiResponse<List<Article>>> = flow {
        emit(ApiResponse.Loading)

        try {
            val response = articleApiService.getArticles()
            if (response.isSuccessful) {
                articleDao.insertArticles(response.body().orEmpty().map { it.toEntity() })
            } else {
                emit(ApiResponse.Error(response.code(), response.message()))
            }
        } catch (_: IOException) {
            // Offline mode intentionally falls back to cached Room data.
        }

        articleDao.getAllArticles()
            .map { entities -> ApiResponse.Success(entities.map { it.toArticle() }) }
            .collect { emit(it) }
    }.flowOn(Dispatchers.IO)

    override fun searchArticles(keyword: String): Flow<ApiResponse<List<Article>>> = flow {
        emit(ApiResponse.Loading)

        val query = keyword.trim()
        if (query.isBlank()) {
            articleDao.getAllArticles()
                .map { entities -> ApiResponse.Success(entities.map { it.toArticle() }) }
                .collect { emit(it) }
            return@flow
        }

        try {
            val response = articleApiService.searchArticles(query)
            if (response.isSuccessful) {
                articleDao.insertArticles(response.body().orEmpty().map { it.toEntity() })
            }
        } catch (_: IOException) {
            // Search falls back to the local cache when the backend is unavailable.
        }

        articleDao.searchArticles(query)
            .map { entities -> ApiResponse.Success(entities.map { it.toArticle() }) }
            .collect { emit(it) }
    }.flowOn(Dispatchers.IO)

    override fun getArticleById(id: Long): Flow<ApiResponse<Article>> = flow {
        emit(ApiResponse.Loading)

        val local = articleDao.getArticleById(id)?.toArticle()
        if (local != null) {
            emit(ApiResponse.Success(local))
        }

        try {
            val response = articleApiService.getArticleById(id)
            if (response.isSuccessful) {
                val article = response.body()
                if (article != null) {
                    articleDao.insertArticle(article.toEntity())
                    emit(ApiResponse.Success(article))
                }
            } else if (local == null) {
                emit(ApiResponse.Error(response.code(), response.message()))
            }
        } catch (_: IOException) {
            if (local == null) {
                emit(ApiResponse.Error(-1, "اینترنت در دسترس نیست"))
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun deleteArticle(id: Long): Flow<ApiResponse<Boolean>> = flow {
        emit(ApiResponse.Loading)

        try {
            val response = articleApiService.deleteArticle(id)
            if (!response.isSuccessful) {
                emit(ApiResponse.Error(response.code(), response.message()))
                return@flow
            }
        } catch (_: IOException) {
            // Allow offline delete from local cache so the admin/list UI stays usable.
        }

        articleDao.deleteArticleById(id)
        emit(ApiResponse.Success(true))
    }.flowOn(Dispatchers.IO)

    override fun insertArticle(article: Article): Flow<ApiResponse<Boolean>> = flow {
        emit(ApiResponse.Loading)
        val publishedArticle = article.copy(isPublished = true)
        articleDao.insertArticle(publishedArticle.toEntity())

        try {
            val response = articleApiService.insertArticle(publishedArticle)
            if (response.isSuccessful) {
                response.body()?.let { articleDao.insertArticle(it.toEntity()) }
                emit(ApiResponse.Success(true))
            } else {
                emit(ApiResponse.Error(response.code(), response.message()))
            }
        } catch (_: IOException) {
            emit(ApiResponse.Success(true))
        }
    }.flowOn(Dispatchers.IO)

    override fun generateArticle(
        request: ArticleGenerationRequest
    ): Flow<ApiResponse<Article>> = flow {
        emit(ApiResponse.Loading)

        try {
            val response = articleApiService.generateArticle(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    articleDao.insertArticle(it.toEntity())
                    emit(ApiResponse.Success(it))
                }
            } else {
                emit(ApiResponse.Error(response.code(), response.message()))
            }
        } catch (_: IOException) {
            emit(ApiResponse.Error(-1, "اینترنت در دسترس نیست"))
        }
    }.flowOn(Dispatchers.IO)

    override fun downloadArticle(
        article: Article,
        format: String
    ): Flow<ApiResponse<File>> = flow {
        emit(ApiResponse.Loading)

        val articleId = article.id
        if (articleId == null) {
            emit(ApiResponse.Error(-1, "شناسه مقاله معتبر نیست"))
            return@flow
        }

        try {
            val response = articleApiService.downloadArticle(articleId, format)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(ApiResponse.Success(saveResponseToDownloads(article, format, body)))
                } else {
                    emit(ApiResponse.Error(response.code(), "فایل دانلودی خالی است"))
                }
            } else {
                emit(ApiResponse.Error(response.code(), response.message()))
            }
        } catch (_: IOException) {
            emit(ApiResponse.Error(-1, "اینترنت در دسترس نیست"))
        }
    }.flowOn(Dispatchers.IO)

    private fun saveResponseToDownloads(
        article: Article,
        format: String,
        body: ResponseBody
    ): File {
        val safeTitle = article.title
            .ifBlank { "article-${article.id}" }
            .replace(Regex("[\\\\/:*?\"<>|]"), "-")
            .take(80)
        val extension = format.lowercase().ifBlank { "pdf" }
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: context.filesDir
        val file = File(directory, "$safeTitle.$extension")

        body.byteStream().use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return file
    }
}
