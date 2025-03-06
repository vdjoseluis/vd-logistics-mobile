package com.vdjoseluis.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.google.firebase.auth.FirebaseAuth
import com.vdjoseluis.R
import com.vdjoseluis.data.models.Service
import com.vdjoseluis.data.models.UserViewModel
import com.vdjoseluis.shared.formatDate
import com.vdjoseluis.shared.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    toggleTheme: () -> Unit,
    isDarkTheme: Boolean,
    onServiceClick: (Service) -> Unit
) {
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
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (userData != null) {
                            Text(
                                text = "Operario: ${userData?.name}",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        } else {
                            Text(text = "Cargando datos...")
                        }

                        IconButton(onClick = {
                            FirebaseAuth.getInstance().signOut()
                            userViewModel.clearUserData()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(40.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
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
                    IconButton(onClick = { toggleTheme() }) {
                        Icon(
                            painter = painterResource(if (isDarkTheme) R.drawable.light_mode else R.drawable.dark_mode),
                            modifier = Modifier.size(30.dp),
                            contentDescription = "Cambiar modo claro/oscuro"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .alpha(if (isDarkTheme) 0.03f else 0.07f)
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
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            Text(
                                text = "Servicios Confirmados",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.1.em,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp),
                                color = Color.White
                            )
                        }
                    }
                    items(confirmedServices) { service ->
                        ServiceItem(service, onClick = { onServiceClick(service) })
                    }

                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Servicios Por Confirmar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.1.em,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp),
                                color = Color.White,
                            )
                        }
                    }
                    items(pendingServices) { service ->
                        ServiceItem(service, onClick = { onServiceClick(service) })
                    }
                }
            }
        }

    }
}

@Composable
fun ServiceItem(service: Service, onClick: (Service) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick(service) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color = MaterialTheme.colorScheme.tertiary, shape = CircleShape)
                ) {
                    val iconResource = when (service.type) {
                        "Transporte" -> {
                            R.drawable.local_shipping
                        }

                        "Montaje" -> {
                            R.drawable.construction
                        }

                        else -> {
                            R.drawable.construction //TODO: Icono para otros casos
                        }
                    }
                    Image(
                        painter = painterResource(id = iconResource),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Cliente: ${service.customerName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            val formattedDate: String = formatDate(service.date.toDate())
            val formattedTime: String = formatTime(service.date.toDate())
            Row {
                Text("Fecha: $formattedDate", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.width(25.dp))
                Text("Hora: $formattedTime", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

}
