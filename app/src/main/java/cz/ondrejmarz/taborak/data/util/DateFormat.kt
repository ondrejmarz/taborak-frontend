package cz.ondrejmarz.taborak.data.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDateStringToOutputDayString(inputDate: String?): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd.MM", Locale.getDefault())

    val date = inputDate?.let { inputFormat.parse(it) }
    return date?.let { outputFormat.format(it) }
}