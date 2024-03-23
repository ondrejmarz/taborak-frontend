package cz.ondrejmarz.taborak.model

import java.util.Date

data class Tour(
    val id: Long,
    val name: String,
    val type: String,
    val description: String,
    val startDate: Date,
    val endDate: Date,
    val enabled: Boolean
)

val tourList = listOf(
    Tour(13L, "První turnus 2024", "Dětský letní tábor", "Hlavní vedoucí Míra", Date(), Date(), true),
    Tour(72L,"Druhý turnus 2024", "Dětský letní tábor", "Hlavní vedoucí Petra", Date(), Date(), true),
    Tour(177L,"Třetí turnus 2024", "Dětský letní tábor","Hlavní vedoucí Filip", Date(), Date(), false)
)