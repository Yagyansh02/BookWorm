package com.example.bookworm.Navigation

sealed class Screens(val route:String){
    object LoginScreen: Screens("login_screen")
    object HomeScreen: Screens("home_screen")
    object SearchScreen: Screens("search_screen")
    object SignUpScreen: Screens("sign_up_screen")
    object SplashScreen: Screens("splash_screen")
}