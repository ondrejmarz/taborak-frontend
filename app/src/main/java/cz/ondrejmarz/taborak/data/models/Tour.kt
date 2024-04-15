package cz.ondrejmarz.taborak.data.models

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

/**
 *
 * @param tourId Tour identification
 * @param title How is tour called
 * @param topic What kind of tour
 * @param description Details about tour
 * @param startDate When does it start
 * @param endDate When does it end
 */
@Serializable
data class Tour (

    val tourId: String? = null,
    val title: String? = null,
    val topic: String? = null,
    val description: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val members: List<String>? = null,
    val applications: List<String>? = null,
    val dailyPrograms: List<String>? = null
)