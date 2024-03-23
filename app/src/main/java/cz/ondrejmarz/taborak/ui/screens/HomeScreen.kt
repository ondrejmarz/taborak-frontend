package cz.ondrejmarz.taborak.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import cz.ondrejmarz.taborak.ui.components.TourList
import cz.ondrejmarz.taborak.model.tourList
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.screens.destinations.*

@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
) {
    Section(title = "Turnusy", modifier = Modifier.fillMaxWidth()) {
        TourList(
            tourList,
            onTourSelected = { id: Long ->
                navigator.navigate(
                    TourScreenDestination(
                        id
                    )
                )
            },
            Modifier.fillMaxWidth())
    }
}