package com.vdjoseluis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vdjoseluis.data.models.UserViewModel
import com.vdjoseluis.navigation.AppNavigation
import com.vdjoseluis.ui.theme.VDLogisticsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VDLogisticsTheme {
                val userViewModel: UserViewModel = viewModel()
                Surface { AppNavigation(userViewModel = userViewModel) }
            }
        }
    }
}
