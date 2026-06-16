package com.example.maghalam.utills

sealed class Screens(val rute : String){
    object SplashScreen : Screens("splashScreen")
    object IntroScreen : Screens("introScreen")
    object RegisterScreen : Screens("RegisterScreen")
    object LoginScreen : Screens("loginScreen")
    object ForgotPasswordScreen : Screens("forgotPasswordScreen")
    object ItemsScreen : Screens("itemsScreen")
    object AiScreen : Screens("aiScreen")
    object ProfileScreen : Screens("profileScreen")
    object AdminScreen : Screens("adminScreen")
    object ArticleDetailScreen : Screens("articleDetailScreen")
}
