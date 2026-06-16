package com.example.maghalam.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.maghalam.model.db.dao.ArticleDao
import com.example.maghalam.model.db.dao.UserDao
import com.example.maghalam.model.db.entity.ArticleEntity
import com.example.maghalam.model.db.entity.UserEntity
import com.example.maghalam.utills.Converters


@Database(
    entities = [ArticleEntity::class, UserEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao
    abstract fun userDao(): UserDao

    companion object {
        private const val DATABASE_NAME = "maghalam_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
