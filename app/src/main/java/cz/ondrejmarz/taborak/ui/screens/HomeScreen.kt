package cz.ondrejmarz.taborak.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.ui.components.TourList
import cz.ondrejmarz.taborak.data.viewmodel.factory.TourViewModelFactory
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.screens.destinations.*

@RequiresApi(Build.VERSION_CODES.O)
@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
) {
    val tourModelView = TourViewModelFactory.getTourViewModel()

    tourModelView.fetchTours()

    val tourListState: State<List<Tour>?> = tourModelView.tours.observeAsState()
    val tourList: List<Tour>? = tourListState.value

    Column {
        Section(title = "Turnusy", modifier = Modifier.fillMaxWidth()) {
            if (tourList != null) {
                TourList(
                    tourList,
                    onTourSelected = { id: Long ->
                        navigator.navigate(
                            TourScreenDestination(
                                tourId = id
                            )
                        )
                    },
                    Modifier.fillMaxWidth())
            }
        }
        Button(
            onClick = { tourModelView.createNewTour(Tour(tourId = 17L, name = "Nový turnus")) },
            modifier = Modifier
                .padding(20.dp)
                .align(alignment = Alignment.End)
        ) {
            Text(text = "Přidat nový")
        }
    }
}