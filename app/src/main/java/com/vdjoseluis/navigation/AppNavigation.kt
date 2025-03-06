package com.vdjoseluis.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun AppNavigation(userViewModel: UserViewModel) {
    val navController = rememberNavController()

    val userData by userViewModel.userData.collectAsState()

    LaunchedEffect(userData) {
        if (userData != null) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        } else {
            navController.navigate("splashScreen") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = if (userData!=null) "home" else "splashScreen") {

        composable("splashScreen") {
            SplashScreen(navController= navController)
        }

        composable("login") {
            LoginScreen(
                userViewModel = userViewModel,
                onLoginSuccess = { userViewModel.loadUserData() }
            )
        }

        composable("register") {
            RegisterScreen(onRegisterSuccess = {
                navController.navigate("home")
            })
        }

        composable("home") {
            HomeScreen(
                userViewModel = userViewModel
            )
        }

        composable("clientes") {
            ClienteScreen()
        }
    }
}
