package com.example.bookworm.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bookworm.presentation.Authentication.LoginScreen
import com.example.bookworm.presentation.Authentication.SignUpScreen
import com.example.bookworm.presentation.Main.HomeScreen
import com.example.bookworm.presentation.Main.SearchScreen
import com.example.bookworm.presentation.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(navController, startDestination = Screens.SplashScreen.route) {
        composable(route = Screens.LoginScreen.route) {
            LoginScreen(
                navigateToHome = {
                    navController.navigate(Screens.HomeScreen.route) {
                        popUpTo(Screens.LoginScreen.route) {
                            inclusive = true
                        }
                    }
                },
                navigateToSignUp = { navController.navigate(Screens.SignUpScreen.route) {
                    launchSingleTop=true
                } }
            )
        }
        composable(route = Screens.SignUpScreen.route) {
            SignUpScreen(
                navigateToHome = { navController.navigate(Screens.HomeScreen.route) },
                navigateToLogin = { navController.navigate(Screens.LoginScreen.route) {
                    launchSingleTop=true
                } }
            )
        }
        composable(route = Screens.HomeScreen.route) {
            HomeScreen(
                onSearchClickButton = { navController.navigate(Screens.SearchScreen.route) },

                )
        }
        composable(route = Screens.SearchScreen.route) {
            SearchScreen()
        }
        composable(route = Screens.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
    }
}