package com.vdjoseluis.data.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        get() = firstName
}


class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData

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
}
