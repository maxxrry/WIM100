package cl.duoc.wim100.ui.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** 1761234567890 → "23-10-2026 14:05" */
fun formatearFecha(milisegundos: Long): String =
    SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date(milisegundos))
