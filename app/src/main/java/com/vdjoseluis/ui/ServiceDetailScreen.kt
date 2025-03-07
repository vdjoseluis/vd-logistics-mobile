package com.vdjoseluis.ui

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.vdjoseluis.R
import com.vdjoseluis.data.models.Customer
import com.vdjoseluis.data.models.Service
import com.vdjoseluis.shared.CustomButton
import com.vdjoseluis.shared.formatDate
import com.vdjoseluis.shared.formatTime
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: String,
    toggleTheme: () -> Unit,
    isDarkTheme: Boolean,
    onBack: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var service by remember { mutableStateOf<Service?>(null) }
    var customer by remember { mutableStateOf<Customer?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(serviceId) {
        val serviceSnapshot = db.collection("services").document(serviceId).get().await()
        service = serviceSnapshot.toObject(Service::class.java)

        service?.refCustomer?.get()?.addOnSuccessListener { customerSnapshot ->
            customer = customerSnapshot.toObject(Customer::class.java)
            isLoading = false
        }?.addOnFailureListener {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Servicio") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(40.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
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
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (service != null && customer != null) {
                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        val formattedDate: String = formatDate(service!!.date.toDate())
                        val formattedTime: String = formatTime(service!!.date.toDate())
                        RowData("Fecha: ", "$formattedDate - $formattedTime")
                        RowData("Tipo: ", service!!.type)
                        RowData("Estado: ", service!!.status)
                        RowData("Descripción: ", service!!.description)

                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            Text(
                                text = "Datos del cliente",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        RowData("Nombre: ", customer!!.name)
                        RowData("Email: ", customer!!.email)
                        RowData("Teléfono: ", customer!!.phone)
                        RowData("Dirección: ", customer!!.address)
                        RowData("Adicional: ", customer!!.addressAdditional)
                        Spacer(modifier = Modifier.height(30.dp))

                        Card (modifier = Modifier
                            .padding(horizontal = 50.dp)
                            .align(Alignment.CenterHorizontally),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)){
                            Row (modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
                                CustomButton("Call", onClick = {/*todo:*/ })
                                CustomButton("Location", onClick = {/*todo:*/ })
                            }
                            Row (modifier = Modifier
                                .padding(vertical = 8.dp).padding(start = 8.dp)
                                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
                                Text("Llamar")
                                Text("Ubicación")
                            }
                        }

                    }
                } else {
                    Text("No se pudo cargar el servicio o cliente.")
                }

            }
        }
    }
}

@Composable
fun RowData(title: String, text: String) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewServiceDetailScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Servicio") },
                navigationIcon = {
                    IconButton(onClick = { /*todo*/ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(40.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
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
                    IconButton(onClick = { /*todo*/ }) {
                        Icon(
                            painter = painterResource(R.drawable.dark_mode),
                            modifier = Modifier.size(30.dp),
                            contentDescription = "Cambiar modo claro/oscuro"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .alpha(0.07f)
                        .fillMaxSize()
                )
                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        RowData("Fecha: ", "18-03-2025 - 07:30")
                        RowData("Tipo: ", "Montaje")
                        RowData("Estado: ", "Pendiente")
                        RowData("Descripción: ", "Instalación completa.")

                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            Text(
                                text = "Datos del cliente",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        RowData("Nombre: ", "Sophie Vásquez Rubio")
                        RowData("Email: ", "sophie@vasquez.com")
                        RowData("Teléfono: ", "622419801")
                        RowData("Dirección: ", "Calle Rafael Alberti, 141 29170 Colmenar")
                        RowData("Adicional: ", "Casa")
                        Spacer(modifier = Modifier.height(30.dp))

                        Card (modifier = Modifier
                            .padding(horizontal = 50.dp)
                            .align(Alignment.CenterHorizontally),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)){
                            Row (modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
                                CustomButton("Call", onClick = {/*todo:*/ })
                                CustomButton("Location", onClick = {/*todo:*/ })
                            }
                            Row (modifier = Modifier
                                .padding(vertical = 8.dp).padding(start = 8.dp)
                                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
                                Text("Llamar")
                                Text("Ubicación")
                            }
                        }

                    }


            }
        }
    }
}
