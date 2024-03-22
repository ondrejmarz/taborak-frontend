package cz.ondrejmarz.taborak.model

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.ondrejmarz.taborak.model.utility.fromDateToStringFormattedTime
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import java.util.Date

data class Tour(
    val name: String,
    val type: String,
    val description: String,
    val startDate: Date,
    val endDate: Date,
    val enabled: Boolean
)

val tourList = listOf(
    Tour("První turnus 2024", "Dětský letní tábor", "Hlavní vedoucí Míra", Date(), Date(), true),
    Tour("Druhý turnus 2024", "Dětský letní tábor", "Hlavní vedoucí Petra", Date(), Date(), true),
    Tour("Třetí turnus 2024", "Dětský letní tábor","Hlavní vedoucí Filip", Date(), Date(), false)
)

@Composable
fun TourList(tourList: List<Tour>, modifier: Modifier) {
    LazyColumn(
        modifier = modifier
    ) {
        items(tourList) { tour -> DesignedCard(
            title = tour.name,
            topic = tour.type,
            description = tour.description,
            startTime = fromDateToStringFormattedTime(tour.startDate),
            endTime = fromDateToStringFormattedTime(tour.endDate),
            enabled = tour.enabled
        )
        }
    }
}