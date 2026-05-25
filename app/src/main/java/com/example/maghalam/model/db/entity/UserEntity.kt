package com.example.maghalam.model.db.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.maghalam.model.data.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "full_name")
    val fullName: String = "",

    @ColumnInfo(name = "username")
    val username: String = "",

    @ColumnInfo(name = "role")
    val role: String = "USER",

    @ColumnInfo(name = "email")
    val email: String = "",

    @ColumnInfo(name = "published_articles_count")
    val publishedArticlesCount: Int = 0,

    @ColumnInfo(name = "dark_mode")
    val darkMode: Boolean = false,

    @ColumnInfo(name = "font_size")
    val fontSize: String = "Vazir",

    @ColumnInfo(name = "created_at")
    val createdAt: String? = null
)

// Mappers
fun UserEntity.toUser(): User = User(
    id = id,
    fullName = fullName,
    username = username,
    role = role,
    email = email,
    publishedArticlesCount = publishedArticlesCount,
    darkMode = darkMode,
    fontSize = fontSize,
    createdAt = createdAt
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id ?: 0,
    fullName = fullName,
    username = username,
    role = role,
    email = email,
    publishedArticlesCount = publishedArticlesCount,
    darkMode = darkMode,
    fontSize = fontSize,
    createdAt = createdAt
)
