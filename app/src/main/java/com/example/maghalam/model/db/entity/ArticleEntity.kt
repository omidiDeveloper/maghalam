package com.example.maghalam.model.db.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.maghalam.model.data.Article

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "author")
    val author: String = "",

    @ColumnInfo(name = "keywords")
    val keywords: String = "",

    @ColumnInfo(name = "language")
    val language: String = "",

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "generatedContent")
    val content: String = "",

    @ColumnInfo(name = "generatedAbstract")
    val abstract: String = "",

    @ColumnInfo(name = "word_count")
    val wordCount: Int = 0,

    @ColumnInfo(name = "is_published")
    val isPublished: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,

    @ColumnInfo(name = "user_id")
    val userId: Long? = 0
)

// Mappers
fun ArticleEntity.toArticle(): Article = Article(
    id = id,
    title = title,
    author = author,
    keywords = keywords,
    language = language,
    description = description,
    content = content,
    abstract = abstract,
    wordCount = wordCount,
    isPublished = isPublished,
    createdAt = createdAt,
    userId = userId
)

fun Article.toEntity(): ArticleEntity = ArticleEntity(
    id = id ?: 0,
    title = title,
    author = author,
    keywords = keywords,
    language = language,
    description = description,
    content = content,
    abstract = abstract,
    wordCount = wordCount,
    isPublished = isPublished,
    createdAt = createdAt,
    userId = userId
)
