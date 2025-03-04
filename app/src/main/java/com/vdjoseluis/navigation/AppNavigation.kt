package com.vdjoseluis.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vdjoseluis.SplashScreen
import com.vdjoseluis.auth.LoginScreen
import com.vdjoseluis.auth.RegisterScreen
import com.vdjoseluis.data.models.UserViewModel
import com.vdjoseluis.ui.ClienteScreen
import com.vdjoseluis.ui.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splashScreen") {

        composable("splashScreen") {
            SplashScreen(navController= navController)
        }

        composable("login") {
            LoginScreen(
                userViewModel = userViewModel,
                onLoginSuccess = { navController.navigate("home") }
            )
        }

        composable("register") {
            RegisterScreen(onRegisterSuccess = {
                navController.navigate("home")
            })
        }

        composable("home") {
            HomeScreen(
                navController,
                userViewModel = userViewModel
            )
        }

        composable("clientes") {
            ClienteScreen()
        }
    }
}
