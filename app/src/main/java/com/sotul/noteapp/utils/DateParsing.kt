package com.sotul.noteapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.toDate(): Date {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return try {
        dateFormat.parse(this) ?: Date()
    } catch (e: Exception) {
        Date()
    }
}
