package com.vdjoseluis.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vdjoseluis.data.models.Cliente
import com.vdjoseluis.data.repositories.ClienteRepository

@Composable
fun ClienteScreen() {
    val repository = ClienteRepository()
    var clientes by remember { mutableStateOf(listOf<Cliente>()) }

    LaunchedEffect(Unit) {
        repository.getClientes { clientes = it }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        clientes.forEach { cliente ->
            Text("Nombre: ${cliente.nombre}")
            Button(onClick = { repository.deleteCliente(cliente.id) { /* refresh */ } }) {
                Text("Eliminar")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
