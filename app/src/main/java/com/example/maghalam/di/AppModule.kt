package com.example.maghalam.di

import com.example.maghalam.model.db.AppDatabase
import com.example.maghalam.model.net.api.ApiService
import com.example.maghalam.model.net.api.RetrofitClient
import com.example.maghalam.model.repository.article.ArticleRepository
import com.example.maghalam.model.repository.article.ArticleRepositoryImpl
import com.example.maghalam.model.repository.user.UserRepository
import com.example.maghalam.model.repository.user.UserRepositoryImpl
import com.example.maghalam.ui.features.AIScreen.AiViewModel
import com.example.maghalam.ui.features.Items.ItemsViewModel
import com.example.maghalam.ui.features.admin.AdminViewModel
import com.example.maghalam.ui.features.articleDetails.ArticleDetailViewModel
import com.example.maghalam.ui.features.login.LoginViewModel
import com.example.maghalam.ui.features.profile.ProfileViewModel
import com.example.maghalam.ui.features.register.RegisterViewModel
import com.example.maghalam.ui.features.startup.StartupViewModel
import com.example.maghalam.utills.SharedPreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {


    single { SharedPreferencesManager(androidContext()) }

    single {
        RetrofitClient
            .create(get(), androidContext())
            .create(ApiService::class.java)
    }

    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().articleDao() }
    single { get<AppDatabase>().userDao() }

    single<ArticleRepository> { ArticleRepositoryImpl(get(), get(), androidContext()) }

    single<UserRepository> { UserRepositoryImpl(get() , get() , get()) }


    viewModel {
        StartupViewModel(
            preferences = get()
        )
    }

    viewModel {
        AdminViewModel(
            userRepository = get(),
            articleRepository = get()
        )
    }

    viewModel {
        ArticleDetailViewModel(
            repository = get()
        )
    }

    viewModel {
        ItemsViewModel(
            repository = get()
        )
    }

    viewModel {
        AiViewModel(
            get()
        )
    }

    viewModel {
        ProfileViewModel(
            preferences = get(),
            userRepository = get(),
            articleRepository = get()
        )
    }


    viewModel {
        LoginViewModel(
            userRepository = get()
        )
    }


    viewModel {
        RegisterViewModel(
            userRepository = get()
        )
    }
}
