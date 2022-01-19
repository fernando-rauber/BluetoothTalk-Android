package uk.fernando.bluetoothtalk.ext


import java.text.SimpleDateFormat
import java.util.*

fun Date.formatToTime(): String {
    val parser = SimpleDateFormat("HH:mm", Locale.getDefault())
    return parser.format(this)
}

fun Date.formatToDate(): String {
    val parser = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return parser.format(this)
}