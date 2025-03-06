package com.vdjoseluis.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vdjoseluis.SplashScreen
import com.vdjoseluis.auth.LoginScreen
import com.vdjoseluis.auth.RegisterScreen
import com.vdjoseluis.data.models.UserViewModel
import com.vdjoseluis.ui.HomeScreen
import com.vdjoseluis.ui.ServiceDetailScreen

@Composable
fun AppNavigation(
    userViewModel: UserViewModel,
    toggleTheme: () -> Unit,
    isDarkTheme: Boolean
) {
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

    NavHost(
        navController = navController,
        startDestination = if (userData != null) "home" else "splashScreen"
    ) {

        composable("splashScreen") {
            SplashScreen(navController = navController)
        }

        composable("login") {
            LoginScreen(
                userViewModel = userViewModel,
                onLoginSuccess = { userViewModel.loadUserData() },
                toggleTheme = toggleTheme,
                isDarkTheme = isDarkTheme
            )
        }

        composable("register") {
            RegisterScreen(onRegisterSuccess = {
                navController.navigate("home")
            })
        }

        composable("home") {
            HomeScreen(
                userViewModel = userViewModel,
                toggleTheme = toggleTheme,
                isDarkTheme = isDarkTheme,
                onServiceClick = { service ->
                    navController.navigate("serviceDetail/${service.id}")
                }
            )
        }

        composable(
            "serviceDetail/{serviceId}",
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            ServiceDetailScreen(
                serviceId,
                toggleTheme = toggleTheme,
                isDarkTheme = isDarkTheme,
                onBack = { navController.navigate("home")}
            )
        }
    }
}
