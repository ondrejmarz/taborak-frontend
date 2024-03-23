package cz.ondrejmarz.taborak.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import cz.ondrejmarz.taborak.model.TourList
import cz.ondrejmarz.taborak.model.tourList
import cz.ondrejmarz.taborak.ui.components.Section

@RootNavGraph(start = true)
@Destination
@Composable
fun OverviewScreen() {
    Section(title = "Overview") {
        TourList(tourList, Modifier.fillMaxWidth())
    }
}