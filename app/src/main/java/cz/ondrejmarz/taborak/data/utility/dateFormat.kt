package cz.ondrejmarz.taborak.data.utility

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
fun formatLocalDateTime(localDateTime: LocalDateTime?): String {
    return localDateTime?.format(DateTimeFormatter.ofPattern("dd.MM.")) ?: "0.0."
}
