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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 *
 * @param tourId Tour identification
 * @param name How is tour called
 * @param topic What kind of tour
 * @param description Details about tour
 * @param startDate When does it start
 * @param endDate When does it end
 */
@Serializable
data class Tour (

    val tourId: Long? = null,
    val name: String? = null,
    val topic: String? = null,
    val description: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startDate: LocalDateTime? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endDate: LocalDateTime? = null
    //enabled from currentUser
)

@RequiresApi(Build.VERSION_CODES.O)
val tourList = listOf(
    Tour(13L, "První turnus 2024", "Dětský letní tábor", "Hlavní vedoucí Míra", LocalDateTime.of(2024, 8, 2, 14, 0), LocalDateTime.of(2024, 8, 13, 16, 0)),
    Tour(72L,"Druhý turnus 2024", "Dětský letní tábor", "Hlavní vedoucí Petra", LocalDateTime.of(2024, 8, 13, 14, 0), LocalDateTime.of(2024, 8, 24, 16, 0)),
    Tour(177L,"Třetí turnus 2024", "Dětský letní tábor","Hlavní vedoucí Filip", LocalDateTime.of(2024, 8, 24, 14, 0), LocalDateTime.of(2024, 9, 4, 16, 0))
)

@Serializer(forClass = LocalDateTime::class)
object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}