package com.vdjoseluis.shared

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

fun formatDate(date: Date): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return sdf.format(date)
}

fun formatTime(time: Date): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(time)
}

fun uploadImageToFirebaseStorage(context: Context, serviceId: String, fileUri: Uri) {
    val storageRef = FirebaseStorage.getInstance().reference
        .child("services/$serviceId/${UUID.randomUUID()}")

    storageRef.putFile(fileUri)
        .addOnSuccessListener {
            Toast.makeText(context, "Imagen subida correctamente", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error al subir imagen", Toast.LENGTH_SHORT).show()
        }
}

fun fetchImagesFromFirebaseStorage(serviceId: String, onResult: (List<String>) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference.child("services/$serviceId/")

    storageRef.listAll()
        .addOnSuccessListener { listResult ->
            val urls = mutableListOf<String>()
            listResult.items.forEach { item ->
                item.downloadUrl.addOnSuccessListener { uri ->
                    urls.add(uri.toString())
                    if (urls.size == listResult.items.size) {
                        onResult(urls)
                    }
                }
            }
        }
        .addOnFailureListener {
            onResult(emptyList())
        }
}

