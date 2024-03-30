package cz.ondrejmarz.taborak.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.ui.components.TourList
import cz.ondrejmarz.taborak.data.viewmodel.factory.TourViewModelFactory
import cz.ondrejmarz.taborak.ui.components.Section

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavHostController,
    onLogoutClick: () -> Unit,
    onTourClick: (String) -> Unit,
    onCreateTourClick: () -> Unit,
) {
    val tourModelView = TourViewModelFactory.getTourViewModel()

    tourModelView.fetchTours()

    val tourListState: State<List<Tour>?> = tourModelView.tours.observeAsState()
    val tourList: List<Tour>? = tourListState.value

    Section(
        title = "Turnusy",
        onButtonClick = onCreateTourClick,
        buttonTitle = "Přidat"
    ) {
        TourList(
            tourList,
            onTourSelected = { id: String ->
                onTourClick(id) },
            Modifier.fillMaxWidth()
        )
    }
}