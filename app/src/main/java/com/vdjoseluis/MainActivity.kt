package com.vdjoseluis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import com.vdjoseluis.navigation.AppNavigation
import com.vdjoseluis.ui.theme.VDLogisticsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VDLogisticsTheme {
                Surface { AppNavigation() }
            }
        }
    }
}
