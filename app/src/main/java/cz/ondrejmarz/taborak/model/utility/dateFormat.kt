package cz.ondrejmarz.taborak.model.utility

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun fromDateToStringFormattedTime(date: Date): String {
    val dateFormat = SimpleDateFormat("dd.MM.", Locale.getDefault())
    return dateFormat.format(date)
}