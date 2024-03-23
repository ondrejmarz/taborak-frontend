package cz.ondrejmarz.taborak.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.ondrejmarz.taborak.model.TourList
import cz.ondrejmarz.taborak.model.tourList
import cz.ondrejmarz.taborak.ui.components.Section

@Composable
fun TourScreen() {
    Section(title = "Tour") {
        TourList(tourList, Modifier.fillMaxWidth())
    }
}