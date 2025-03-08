package com.vdjoseluis.shared

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
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
    val extension = "png"
    val storageRef = FirebaseStorage.getInstance().reference
        .child("services/$serviceId/${UUID.randomUUID()}.$extension")

    storageRef.putFile(fileUri)
        .addOnSuccessListener {
            Toast.makeText(context, "Imagen subida correctamente", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error al subir imagen", Toast.LENGTH_SHORT).show()
        }
}
fun fetchFilesFromFirebaseStorage(serviceId: String, onResult: (List<Pair<String, String>>) -> Unit) {
    val storageRef = Firebase.storage.reference.child("services/$serviceId")

    storageRef.listAll()
        .addOnSuccessListener { listResult ->
            val fileList = mutableListOf<Pair<String, String>>()
            val totalFiles = listResult.items.size
            if (totalFiles == 0) {
                onResult(emptyList())
                return@addOnSuccessListener
            }

            listResult.items.forEach { item ->
                item.downloadUrl.addOnSuccessListener { uri ->
                    fileList.add(item.name to uri.toString())

                    if (fileList.size == totalFiles) {
                        onResult(fileList)
                    }
                }
            }
        }
        .addOnFailureListener {
            onResult(emptyList())
        }
}

fun openImage(context: Context, imageUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(Uri.parse(imageUrl), "image/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se puede abrir la imagen", Toast.LENGTH_SHORT).show()
    }
}

fun openPdf(context: Context, pdfUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(Uri.parse(pdfUrl), "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se puede abrir el PDF", Toast.LENGTH_SHORT).show()
    }
}




