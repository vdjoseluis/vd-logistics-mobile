package com.vdjoseluis.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vdjoseluis.auth.LoginScreen
import com.vdjoseluis.auth.RegisterScreen
import com.vdjoseluis.ui.ClienteScreen
import com.vdjoseluis.ui.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("home")
            })
        }

        composable("register") {
            RegisterScreen(onRegisterSuccess = {
                navController.navigate("home")
            })
        }

        composable("home") {
            HomeScreen(navController)
        }

        composable("clientes") {
            ClienteScreen()
        }
    }
}
