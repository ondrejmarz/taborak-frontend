package cz.ondrejmarz.taborak.data.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@RequiresApi(Build.VERSION_CODES.O)
fun formatDateStringToGivenFormatString(inputDate: String?, format: String): Int? {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    val parsedDate = LocalDateTime.parse(inputDate, formatter)

    return when (format) {
        "yyyy" -> parsedDate.year
        "MM" -> parsedDate.month.value
        "dd" -> parsedDate.dayOfMonth
        "HH" -> parsedDate.hour
        "mm" -> parsedDate.minute
        "ss" -> parsedDate.second
        else -> throw IllegalArgumentException("Invalid format: $format")
    }
}

fun formatDateStringToOutputDayString(inputDate: String?): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd.MM", Locale.getDefault())

    val date = inputDate?.let { inputFormat.parse(it) }
    return date?.let { outputFormat.format(it) }
}

fun formatDateStringToOutputTimeString(inputDate: String?): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val date = inputDate?.let { inputFormat.parse(it) }
    return date?.let { outputFormat.format(it) }
}

fun formatMillisToIsoDateTime(millis: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("Europe/Prague")
    return dateFormat.format(Date(millis))
}

fun formatMillisToIsoDay(millis: Long?): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("Europe/Prague")
    if (millis == null)
        return getCurrentDate()
    return dateFormat.format(Date(millis))
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toMillis(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.fromDayToMillis(): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormat.parse(this)
    return date?.time ?: 0L
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.fromDayToReadableDay(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormat.parse(this)
    val outputFormat = SimpleDateFormat("dd.MM.", Locale.getDefault())
    if (date == null) return "01.01."
    return outputFormat.format(date)
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
fun convertToTimestamp(timePickerState: TimePickerState): Long {
    val localDateTime = LocalDateTime.now()
        .withHour(if (timePickerState.is24hour) timePickerState.hour else 0) // Pokud je to 12h režim, 12h se konvertují na 0
        .withMinute(timePickerState.minute)
        .withSecond(0)
        .withNano(0)

    return localDateTime.toEpochSecond(ZoneOffset.UTC) * 1000
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
fun convertTimePickerStateToStringDate(timePickerState: TimePickerState, datetimeString: String?): String {
    datetimeString.run {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val dateTime = formatter.parse(datetimeString)
        val calendar = Calendar.getInstance()
        calendar.time = dateTime
        calendar.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
        calendar.set(Calendar.MINUTE, timePickerState.minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return formatter.format(calendar.time)
    }
}

fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDate = Date()
    return dateFormat.format(currentDate)
}