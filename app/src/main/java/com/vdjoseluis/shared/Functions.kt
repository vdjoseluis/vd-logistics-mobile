package com.vdjoseluis.shared

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(date: Date): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return sdf.format(date)
}

fun formatTime(time: Date): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(time)
}