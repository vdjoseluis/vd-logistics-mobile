package com.vdjoseluis.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.vdjoseluis.data.models.Cliente

class ClienteRepository {
    private val db = FirebaseFirestore.getInstance()

    fun addCliente(cliente: Cliente, onResult: (Boolean) -> Unit) {
        db.collection("clientes").document(cliente.id).set(cliente).addOnCompleteListener { onResult(it.isSuccessful) }
    }

    fun getClientes(onResult: (List<Cliente>) -> Unit) {
        db.collection("clientes").get().addOnSuccessListener { result ->
            onResult(result.mapNotNull { it.toObject(Cliente::class.java) })
        }
    }

    fun deleteCliente(id: String, onResult: (Boolean) -> Unit) {
        db.collection("clientes").document(id).delete().addOnCompleteListener { onResult(it.isSuccessful) }
    }
}
