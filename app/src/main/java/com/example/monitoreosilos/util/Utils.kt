package com.example.monitoreosilos.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    private fun dateFormat() =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply { isLenient = false }

    fun today(): String = dateFormat().format(Date())

    fun now(): String =
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

    fun time(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    fun parse(text: String): Date? =
        try { dateFormat().parse(text) } catch (e: Exception) { null }

    /** "29 de septiembre de 2026" a partir de "29/09/2026". */
    fun longDate(text: String): String {
        val d = parse(text) ?: return text
        return SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "CL")).format(d)
    }

    /** Convierte "dd/MM/yyyy" al valor que usa el calendario (medianoche UTC). */
    fun toPickerMillis(text: String): Long? {
        val d = parse(text) ?: return null
        val local = Calendar.getInstance().apply { time = d }
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis
    }

    /** Convierte lo que devuelve el calendario a "dd/MM/yyyy". */
    fun fromPickerMillis(millis: Long): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .format(Date(millis))
}

fun Double.formatTon(): String =
    if (this % 1.0 == 0.0) toInt().toString() else "%.1f".format(this)