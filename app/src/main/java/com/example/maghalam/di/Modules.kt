package com.example.maghalam.di

import ArticleRepository
import com.example.maghalam.MyApplication
import com.example.maghalam.model.repository.user.UserRepository
import com.example.maghalam.model.repository.user.UserRepositoryImpl
import com.example.maghalam.ui.features.AIScreen.AiViewModel
import com.example.maghalam.ui.features.Items.ItemsViewModel
import com.example.maghalam.ui.features.articleDetails.ArticleDetailViewModel
import com.example.maghalam.ui.features.profile.ProfileViewModel
import com.example.maghalam.ui.features.register.RegisterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val myModule = module{
    // SharedPreferencesManager
    single { MyApplication.instance.sharedPreferencesManager }

    // Repositories
    single { ArticleRepository() }
    single<UserRepository> { UserRepositoryImpl(get()) }

    // ViewModels
    viewModel { AiViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }

}