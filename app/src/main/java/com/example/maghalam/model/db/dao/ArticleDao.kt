package com.example.maghalam.model.db.dao

import androidx.room.*
import com.example.maghalam.model.db.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles ORDER BY created_at DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticleById(id: Long): ArticleEntity?

    @Query("SELECT * FROM articles WHERE user_id = :userId ORDER BY created_at DESC")
    fun getArticlesByUser(userId: Long): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE is_published = 1 ORDER BY created_at DESC")
    fun getPublishedArticles(): Flow<List<ArticleEntity>>

    @Query("""
        SELECT * FROM articles 
        WHERE title LIKE '%' || :keyword || '%' 
        OR keywords LIKE '%' || :keyword || '%'
        OR description LIKE '%' || :keyword || '%'
        ORDER BY created_at DESC
    """)
    fun searchArticles(keyword: String): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Update
    suspend fun updateArticle(article: ArticleEntity)

    @Query("DELETE FROM articles WHERE id = :id")
    suspend fun deleteArticleById(id: Long)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()

}
