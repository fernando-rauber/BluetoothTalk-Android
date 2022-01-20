package uk.fernando.bluetoothtalk.ext


import android.content.res.Resources
import org.koin.androidx.compose.get
import uk.fernando.bluetoothtalk.R
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

fun Date.formatToChatDate(): String {
    return if (this.after(getCurrentDay()))
        this.formatToTime()
    else if (this.before(getCurrentDay()) && this.after(getYesterdayDate()))
        "Yesterday"
    else
        this.formatToDate()
}

private fun getCurrentDay(): Date {
    val calendar = Calendar.getInstance()
    calendar[Calendar.HOUR] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.HOUR_OF_DAY] = 0
    return calendar.time
}

private fun getYesterdayDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = getCurrentDay()
    calendar[Calendar.DAY_OF_MONTH] = calendar[Calendar.DAY_OF_MONTH] - 1
    return calendar.time
}

fun Date.isSameDay(date: Date): Boolean {
    val firstDay = Calendar.getInstance()
    firstDay.time = this

    val secondDay = Calendar.getInstance()
    secondDay.time = date

    return (firstDay.get(Calendar.ERA) == secondDay.get(Calendar.ERA) &&
            firstDay.get(Calendar.YEAR) == secondDay.get(Calendar.YEAR) &&
            firstDay.get(Calendar.DAY_OF_YEAR) == secondDay.get(Calendar.DAY_OF_YEAR))
}