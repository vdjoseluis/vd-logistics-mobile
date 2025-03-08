package com.vdjoseluis.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun openGoogleMaps(context: Context, address: String) {
    val uri = Uri.encode(address)
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$uri"))
    intent.setPackage("com.google.android.apps.maps") // Para abrir solo con Google Maps
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Google Maps no está instalado", Toast.LENGTH_SHORT).show()
    }
}

fun openDialer(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No se pudo abrir el marcador", Toast.LENGTH_SHORT).show()
    }
}

