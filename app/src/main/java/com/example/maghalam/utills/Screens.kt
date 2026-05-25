package com.example.maghalam.utills

sealed class Screens(val rute : String){
    object IntroScreen : Screens("inroScreen")
    object RegisterScreen : Screens("RegisterScreen")
    object LoginScreen : Screens("loginScreen")
    object ItemsScreen : Screens("itemsScreen")
    object AiScreen : Screens("aiScreen")
    object ProfileScreen : Screens("profileScreen")
    object ArticleDetailScreen : Screens("articleDetailScreen")
}