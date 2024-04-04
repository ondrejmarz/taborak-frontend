package cz.ondrejmarz.taborak.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.ui.theme.AppTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TourList(
    tourList: List<Tour>?,
    userId: String?,
    onTourSelected: (String) -> Unit,
    modifier: Modifier = Modifier) {
    if (tourList != null) {
        LazyColumn(modifier = modifier) {
            itemsIndexed(tourList) { index, tour ->
                val isMember = tour.members?.contains(userId)
                if (tour.tourId != null) {
                    DesignedCard(
                        title = if (tour.title != null) tour.title else "Nepojmenovaný turnus",
                        topic = tour.topic,
                        description = tour.description,
                        startTime = tour.startDate,
                        endTime = tour.endDate,
                        enabled = isMember,
                        button = "Otevřít",
                        onClickAction = { if (isMember == true) { onTourSelected(tour.tourId) } }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewTurnusList() {
    AppTheme {
        MaterialTheme {
            //TourList(tourList, {}, Modifier.fillMaxWidth())
        }
    }
}