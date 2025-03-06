package com.vdjoseluis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vdjoseluis.data.models.UserViewModel
import com.vdjoseluis.navigation.AppNavigation
import com.vdjoseluis.ui.theme.VDLogisticsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }

            VDLogisticsTheme(darkTheme = isDarkTheme) {
                val userViewModel: UserViewModel = viewModel()
                Surface {
                    AppNavigation(
                        userViewModel = userViewModel,
                        toggleTheme = { isDarkTheme = !isDarkTheme},
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
}
