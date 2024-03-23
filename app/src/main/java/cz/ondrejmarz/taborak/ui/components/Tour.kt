package cz.ondrejmarz.taborak.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cz.ondrejmarz.taborak.model.Tour
import cz.ondrejmarz.taborak.model.tourList
import cz.ondrejmarz.taborak.model.utility.fromDateToStringFormattedTime
import cz.ondrejmarz.taborak.ui.theme.AppTheme

@Composable
fun TourList(tourList: List<Tour>, onTourSelected: (Long) -> Unit, modifier: Modifier) {
    LazyColumn(
        modifier = modifier
    ) {
        items(tourList) { tour -> DesignedCard(
            title = tour.name,
            topic = tour.type,
            description = tour.description,
            startTime = fromDateToStringFormattedTime(tour.startDate),
            endTime = fromDateToStringFormattedTime(tour.endDate),
            enabled = tour.enabled,
            button = if (tour.enabled == true) "Otevřít" else "Požádat o přijetí",
            onClickAction = { onTourSelected(tour.id) }
            )
        }
    }
}

@Preview
@Composable
fun PreviewTurnusList() {
    AppTheme {
        MaterialTheme {
            TourList(tourList, {}, Modifier.fillMaxWidth())
        }
    }
}