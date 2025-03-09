package com.vdjoseluis.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.vdjoseluis.R
import com.vdjoseluis.data.models.Customer
import com.vdjoseluis.data.models.Service
import com.vdjoseluis.navigation.openDialer
import com.vdjoseluis.navigation.openGoogleMaps
import com.vdjoseluis.shared.CustomButton
import com.vdjoseluis.shared.UploadImageDialog
import com.vdjoseluis.shared.ViewFilesDialog
import com.vdjoseluis.shared.formatDate
import com.vdjoseluis.shared.formatTime
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: String,
    toggleTheme: () -> Unit,
    isDarkTheme: Boolean,
    onBack: () -> Unit,
    onUpdateStatus: (String, Date?, String?) -> Unit,
    onReportIncident: (String) -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    var service by remember { mutableStateOf<Service?>(null) }
    var customer by remember { mutableStateOf<Customer?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var showMenu by remember { mutableStateOf(false) }
    var showEndServiceDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showIncidentDialog by remember { mutableStateOf(false) }
    var showServiceCommentsDialog by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var showViewFilesDialog by remember { mutableStateOf(false) }

    var showDateTimePickerDialog by remember { mutableStateOf(false) }
    var proposedDate by remember { mutableStateOf<Date?>(null) }

    var actionToConfirm by remember { mutableStateOf("") }
    var incidentDescription by remember { mutableStateOf("") }
    var endServiceComments by remember { mutableStateOf("") }

    var drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(serviceId) {
        try {
            val serviceSnapshot = db.collection("services").document(serviceId).get().await()
            val fetchedService = serviceSnapshot.toObject(Service::class.java)
            if (fetchedService != null) {
                service = fetchedService

                fetchedService.refCustomer!!.get().addOnSuccessListener { customerSnapshot ->
                    customer = customerSnapshot.toObject(Customer::class.java)
                    isLoading = false
                }.addOnFailureListener {
                    isLoading = false
                }
            } else {
                isLoading = false
            }
        } catch (e: Exception) {
            isLoading = false
            Log.e("ServiceDetail", "Error al cargar el servicio", e)
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
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /*showMenu = true*/ scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                    }
                    if (service != null) {
                        DropdownMenu(
                            tonalElevation = 8.dp,
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }) {
                            when (service!!.status) {
                                "Confirmado" -> {
                                    DropdownMenuItem(
                                        text = { Text("Finalizar Servicio") },
                                        onClick = {
                                            actionToConfirm = "finalizado"
                                            showEndServiceDialog = true
                                            showMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Abrir Incidencia") },
                                        onClick = {
                                            actionToConfirm = "incidencia"
                                            showIncidentDialog = true
                                            showMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Añadir imágenes") },
                                        onClick = {
                                            showUploadDialog = true
                                            showMenu = false
                                        }
                                    )
                                }

                                "Pendiente" -> {
                                    DropdownMenuItem(
                                        text = { Text("Confirmar Servicio") },
                                        onClick = {
                                            actionToConfirm = "confirmar"
                                            showConfirmDialog = true
                                            showMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Proponer Nueva Fecha") },
                                        onClick = {
                                            showMenu = false
                                            showDateTimePickerDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
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
                        RowData("Teléfono: ", customer!!.phone)
                        RowData("Dirección: ", customer!!.address)
                        RowData("Adicional: ", customer!!.addressAdditional)
                        Spacer(modifier = Modifier.height(15.dp))

                        Row(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            if (service!!.status == "Confirmado") {
                                CustomButton(
                                    "Call", isDarkTheme,
                                    onClick = { openDialer(context, customer!!.phone) })
                            }
                            CustomButton(
                                "Location", isDarkTheme,
                                onClick = { openGoogleMaps(context, customer!!.address) })
                        }
                        Spacer(modifier = Modifier.height(15.dp))

                        Card(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable { showServiceCommentsDialog = true }
                        ) { RowData("Ver comentarios ", "") }
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clickable { showViewFilesDialog = true }
                        ) { RowData("Archivos adjuntos", "") }
                    }
                } else {
                    Text("No se pudo cargar el servicio o cliente.", color = Color.Red)
                }

            }
        }
    }

    if (showDateTimePickerDialog) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }

                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        selectedDate.set(Calendar.HOUR_OF_DAY, hour)
                        selectedDate.set(Calendar.MINUTE, minute)
                        proposedDate = selectedDate.time

                        actionToConfirm = "propuesta"
                        showConfirmDialog = true

                        showDateTimePickerDialog = false
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()

        showDateTimePickerDialog = false
    }

    if (showEndServiceDialog) {
        AlertDialog(
            onDismissRequest = { showEndServiceDialog = false },
            title = { Text("Finalizar servicio") },
            text = {
                Column {
                    Text("Puedes escribir algún comentario:")
                    TextField(
                        value = endServiceComments,
                        onValueChange = { endServiceComments = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEndServiceDialog = false
                        showConfirmDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.White
                    )
                ) {
                    Text("Continuar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEndServiceDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar acción") },
            text = {
                Text(
                    when (actionToConfirm) {
                        "finalizado" -> "¿Está seguro de marcar el servicio como finalizado?"
                        "confirmar" -> "¿Está seguro de confirmar el servicio?"
                        "propuesta" -> "¿Está seguro de proponer nueva fecha?"
                        else -> ""
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (actionToConfirm) {
                            "finalizado" -> onUpdateStatus("Finalizado", null, endServiceComments)
                            "confirmar" -> onUpdateStatus("Confirmado", null, null)
                            "propuesta" -> {
                                onUpdateStatus("Propuesta nueva fecha", proposedDate, null)
                            }
                        }
                        showConfirmDialog = false
                        onBack()
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.White
                    )
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showIncidentDialog) {
        AlertDialog(
            onDismissRequest = { showIncidentDialog = false },
            title = { Text("Abrir Incidencia") },
            text = {
                Column {
                    Text("Escribe los detalles:")
                    TextField(
                        value = incidentDescription,
                        onValueChange = { incidentDescription = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReportIncident(incidentDescription)
                        showIncidentDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.White
                    )
                ) {
                    Text("Registrar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showIncidentDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showServiceCommentsDialog) {
        AlertDialog(
            onDismissRequest = { showServiceCommentsDialog = false },
            title = { Text("Comentarios") },
            text = {
                Column {
                    TextField(
                        value = service!!.comments ?: "No hay comentarios",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showServiceCommentsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = Color.White
                    )
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    if (showUploadDialog) {
        UploadImageDialog(serviceId, onDismiss = { showUploadDialog = false })
    }
    if (showViewFilesDialog) {
        ViewFilesDialog(serviceId, onDismiss = { showViewFilesDialog = false }, context)
    }
}

@Composable
fun RowData(title: String, text: String) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}
