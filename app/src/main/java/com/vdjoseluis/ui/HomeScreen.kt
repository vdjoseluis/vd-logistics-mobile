package com.vdjoseluis.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.vdjoseluis.data.models.UserViewModel
import com.vdjoseluis.R
import com.vdjoseluis.data.models.Service

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(userViewModel: UserViewModel) {
    val userData by userViewModel.userData.collectAsState()
    val confirmedServices by userViewModel.confirmedServices.collectAsState()
    val pendingServices by userViewModel.pendingServices.collectAsState()

    DisposableEffect(Unit) {
        userViewModel.startListeningForServices()
        onDispose { userViewModel.stopListeningForServices() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                            FirebaseAuth.getInstance().signOut()
                            userViewModel.clearUserData()
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
//                    IconButton(onClick = { userViewModel.loadUserServices() }) {
//                        Icon(Icons.Filled.Refresh, contentDescription = "Refrescar servicios")
//                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            color = Color.LightGray,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .alpha(0.1f)
                        .fillMaxSize()
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {

                            Text(
                                text = "Servicios Confirmados",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp)
                            )
                        }
                    }
                    items(confirmedServices) { service ->
                        ServiceItem(service)
                    }

                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Servicios Por Confirmar",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp)
                            )
                        }
                    }
                    items(pendingServices) { service ->
                        ServiceItem(service)
                    }
                }
            }
        }

    }
}

@Composable
fun ServiceItem(service: Service) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("Tipo: ${service.type}", style = MaterialTheme.typography.bodyMedium)
        Text("Descripción: ${service.description}", style = MaterialTheme.typography.bodyMedium)
        Text("Fecha: ${service.date.toDate()}", style = MaterialTheme.typography.bodySmall)
        Text("Estado: ${service.status}", style = MaterialTheme.typography.bodySmall)
    }
}
