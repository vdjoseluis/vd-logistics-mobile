package com.vdjoseluis.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vdjoseluis.data.models.Customer
import com.vdjoseluis.data.repositories.ClienteRepository

@Composable
fun ClienteScreen() {
    val repository = ClienteRepository()
    var customers by remember { mutableStateOf(listOf<Customer>()) }

    LaunchedEffect(Unit) {
        repository.getClientes { customers = it }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        customers.forEach { cliente ->
            Text("Nombre: ${cliente.firstName}")
            Button(onClick = { repository.deleteCliente(cliente.id) { /* refresh */ } }) {
                Text("Eliminar")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
