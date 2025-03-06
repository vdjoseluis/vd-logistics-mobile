package com.vdjoseluis.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.vdjoseluis.data.models.Customer

class ClienteRepository {
    private val db = FirebaseFirestore.getInstance()

    fun addCliente(customer: Customer, onResult: (Boolean) -> Unit) {
        db.collection("clientes").document(customer.id).set(customer).addOnCompleteListener { onResult(it.isSuccessful) }
    }

    fun getClientes(onResult: (List<Customer>) -> Unit) {
        db.collection("clientes").get().addOnSuccessListener { result ->
            onResult(result.mapNotNull { it.toObject(Customer::class.java) })
        }
    }

    fun deleteCliente(id: String, onResult: (Boolean) -> Unit) {
        db.collection("clientes").document(id).delete().addOnCompleteListener { onResult(it.isSuccessful) }
    }
}
