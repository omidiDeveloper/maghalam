package com.example.maghalam.model.repository.article

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import com.google.gson.JsonParser

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
                articleDao.deleteAllArticles()
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
                articleDao.insertArticles(response.body()?.content.orEmpty().map { it.toEntity() })
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

    override fun publishArticle(article: Article): Flow<ApiResponse<Article>> = flow {
        emit(ApiResponse.Loading)

        val articleId = article.id
        if (articleId == null) {
            emit(ApiResponse.Error(-1, "شناسه مقاله معتبر نیست"))
            return@flow
        }

        try {
            val response = articleApiService.publishArticle(articleId)
            if (response.isSuccessful) {
                val publishedArticle = response.body() ?: article.copy(isPublished = true)
                articleDao.insertArticle(publishedArticle.toEntity())
                emit(ApiResponse.Success(publishedArticle))
            } else {
                emit(ApiResponse.Error(response.code(), response.errorMessage()))
            }
        } catch (_: IOException) {
            val publishedArticle = article.copy(isPublished = true)
            articleDao.insertArticle(publishedArticle.toEntity())
            emit(ApiResponse.Success(publishedArticle))
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
                emit(ApiResponse.Error(response.code(), response.errorMessage()))
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
            emit(ApiResponse.Success(saveArticleHtmlToDownloads(article)))
            return@flow
        }

        try {
            val response = articleApiService.downloadArticle(articleId, format)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(ApiResponse.Success(saveResponseToDownloads(article, format, body)))
                } else {
                    emit(ApiResponse.Success(saveArticleHtmlToDownloads(article)))
                }
            } else {
                emit(ApiResponse.Success(saveArticleHtmlToDownloads(article)))
            }
        } catch (_: IOException) {
            emit(ApiResponse.Success(saveArticleHtmlToDownloads(article)))
        } catch (e: Exception) {
            emit(ApiResponse.Error(-1, e.message ?: "دانلود انجام نشد"))
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
        val fileName = "$safeTitle.$extension"
        val mimeType = when (extension) {
            "pdf" -> "application/pdf"
            "html", "htm" -> "text/html"
            else -> "application/octet-stream"
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, mimeType)
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Cannot create download file")

            body.byteStream().use { input ->
                resolver.openOutputStream(uri)?.use { output ->
                    input.copyTo(output)
                } ?: throw IOException("Cannot open download file")
            }

            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)

            return File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
        }

        val canUsePublicDownloads = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val publicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val directory = if (canUsePublicDownloads && (publicDirectory.exists() || publicDirectory.mkdirs())) {
            publicDirectory
        } else {
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
        }
        val file = File(directory, fileName)

        body.byteStream().use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return file
    }

    private fun saveArticleHtmlToDownloads(article: Article): File {
        val safeTitle = article.title
            .ifBlank { "article-${article.id ?: System.currentTimeMillis()}" }
            .replace(Regex("[\\\\/:*?\"<>|]"), "-")
            .take(80)
        val fileName = "$safeTitle.html"
        val html = buildString {
            append("<!doctype html><html lang=\"fa\" dir=\"rtl\"><head>")
            append("<meta charset=\"utf-8\"><title>")
            append(article.title.escapeHtml())
            append("</title>")
            append("<style>body{font-family:Tahoma,Arial,sans-serif;line-height:1.9;max-width:840px;margin:32px auto;padding:0 20px;color:#1f2937}h1{line-height:1.5}.meta{color:#64748b;border-bottom:1px solid #d7e3ec;padding-bottom:16px;margin-bottom:24px}.section{margin-top:28px;white-space:pre-wrap}</style>")
            append("</head><body>")
            append("<h1>").append(article.title.escapeHtml()).append("</h1>")
            append("<div class=\"meta\">")
            append("نویسنده: ").append(article.author.escapeHtml()).append("<br>")
            append("زبان: ").append(article.language.escapeHtml()).append("<br>")
            append("تعداد کلمات: ").append(article.wordCount).append("<br>")
            article.getFormattedDate().takeIf { it.isNotBlank() }?.let {
                append("تاریخ ساخت مقاله: ").append(it.escapeHtml()).append("<br>")
            }
            append("کلمات کلیدی: ").append(article.keywords.escapeHtml())
            append("</div>")
            append("<h2>چکیده</h2><div class=\"section\">")
            append(article.abstract.ifBlank { article.description }.escapeHtml())
            append("</div><h2>متن مقاله</h2><div class=\"section\">")
            append(article.content.ifBlank { article.description }.escapeHtml())
            append("</div></body></html>")
        }

        return saveBytesToDownloads(fileName, "text/html", html.toByteArray(Charsets.UTF_8))
    }

    private fun saveBytesToDownloads(
        fileName: String,
        mimeType: String,
        bytes: ByteArray
    ): File {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, mimeType)
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Cannot create download file")

            resolver.openOutputStream(uri)?.use { output ->
                output.write(bytes)
            } ?: throw IOException("Cannot open download file")

            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)

            return File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
        }

        val canUsePublicDownloads = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val publicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val directory = if (canUsePublicDownloads && (publicDirectory.exists() || publicDirectory.mkdirs())) {
            publicDirectory
        } else {
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
        }
        val file = File(directory, fileName)
        file.writeBytes(bytes)
        return file
    }

    private fun String.escapeHtml(): String {
        return replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    private fun retrofit2.Response<*>.errorMessage(): String {
        val rawBody = errorBody()?.string().orEmpty()
        if (rawBody.isBlank()) return message()

        return runCatching {
            val json = JsonParser.parseString(rawBody).asJsonObject
            json.get("message")?.asString?.takeIf { it.isNotBlank() }
        }.getOrNull() ?: rawBody
    }
}
