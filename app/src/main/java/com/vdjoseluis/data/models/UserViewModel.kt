package com.vdjoseluis.data.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = ""
) {
    val name: String
        get() = "$firstName $lastName"
}

class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData

    private val _confirmedServices = MutableStateFlow<List<Service>>(emptyList())
    val confirmedServices: StateFlow<List<Service>> = _confirmedServices

    private val _pendingServices = MutableStateFlow<List<Service>>(emptyList())
    val pendingServices: StateFlow<List<Service>> = _pendingServices

    private var servicesListener: ListenerRegistration? = null

    init {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            loadUserData()
            startListeningForServices()
        }
    }

    fun loadUserData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                try {
                    val snapshot = db.collection("users").document(uid).get().await()
                    if (snapshot.exists()) {
                        val user = snapshot.toObject(User::class.java)
                        _userData.value = user
                        Log.d("UserViewModel", "Datos cargados: $user")
                    } else {
                        Log.e("UserViewModel", "No existe documento para el usuario con uid: $uid")
                    }
                } catch (e: Exception) {
                    Log.e("UserViewModel", "Error al cargar datos del usuario: ${e.message}", e)
                }
            }
        } else {
            Log.e("UserViewModel", "Usuario no autenticado")
        }
    }

    fun startListeningForServices() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Log.e("UserViewModel", "Usuario no autenticado, no se pueden cargar servicios")
            return
        }

        val userRef = db.collection("users").document(uid)

        servicesListener?.remove()

        servicesListener = db.collection("services")
            .whereEqualTo("refOperator", userRef)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("UserViewModel", "Error al escuchar servicios: ${error.message}", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val allServices = snapshot.documents.mapNotNull { doc ->
                        val service = doc.toObject(Service::class.java)
                        Log.d("UserViewModel", "Servicio actualizado: $service")
                        service
                    }

                    _confirmedServices.value = allServices.filter { it.status == "Confirmado" }
                    _pendingServices.value = allServices.filter { it.status == "Pendiente" }

                    Log.d(
                        "UserViewModel",
                        "Servicios actualizados. Confirmados: ${_confirmedServices.value.size}, Pendientes: ${_pendingServices.value.size}"
                    )
                }
            }
    }

    fun clearUserData() {
        _userData.value = null
        _confirmedServices.value = emptyList()
        _pendingServices.value = emptyList()
        stopListeningForServices()
    }

    fun stopListeningForServices() {
        servicesListener?.remove()
        servicesListener = null
    }

}
