package com.vdjoseluis.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vdjoseluis.data.models.UserViewModel
import com.vdjoseluis.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val userData by userViewModel.userData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Si hay datos de usuario, mostramos el nombre
                        if (userData != null) {
                            Text(
                                text = "Operario: ${userData?.name}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        } else {
                            Text(
                                text = "Cargando datos...",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        IconButton(onClick = {
                            // TODO: Lógica de cerrar sesión
                            navController.navigate("login")
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(40.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Copyright ® 2025 - VD LOGISTICS",
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    IconButton(onClick = {
                        // TODO: refrescar servicios o lo que necesites
                    }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refrescar servicios")
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            color = Color.LightGray,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.alpha(0.1f)
            )
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Servicios Confirmados",
                            Modifier.padding(12.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                item {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Servicios Por Confirmar",
                            Modifier.padding(12.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
