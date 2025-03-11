package com.vdjoseluis.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    if (uid!=null) {
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                            val token = tokenTask.result
                            saveTokenToFirestore(uid, token)
                        }
                    }
                    Log.d("AuthViewModel", "Login exitoso")
                    onResult(true)
                } else {
                    Log.e("AuthViewModel", "Error de login", task.exception)
                    onResult(false)
                }
            }
    }

    private fun saveTokenToFirestore(uid: String, token: String) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(uid)
        userRef.update("fcmToken", token)
            .addOnSuccessListener {
                Log.d("FCM", "Token registrado en Firestore")
            }
            .addOnFailureListener {
                Log.e("FCM", "Error al registrar token", it)
            }
    }
}
