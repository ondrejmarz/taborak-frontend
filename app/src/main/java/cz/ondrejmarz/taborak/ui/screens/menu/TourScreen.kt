package cz.ondrejmarz.taborak.ui.screens.menu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.data.util.formatDateStringToOutputDayString
import cz.ondrejmarz.taborak.ui.viewmodels.TourViewModel
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.viewmodels.factory.TourViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TourScreen(
    tourId: String,
    tourViewModel: TourViewModel = viewModel(factory = TourViewModelFactory(tourId)),
    navController: NavHostController
) {
    val tour by tourViewModel.tour.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                tourId = tourId,
                allScreens = appTabRowScreens,
                onItemSelected = { route ->
                    navController.navigate(route)
                },
                currentScreen = "Turnus"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

            Section(title = "Aktivní ankety") {
                DesignedCard(
                    title = "Turnus momentálně nemá žádné aktivní ankety",
                    description = "Anketu může vytvořit hlavní vedoucí, nebo jeho zástupci."
                )
            }

            if (tour.startDate != null && tour.endDate != null) {
                Section(title = "Doba trvání") {
                    DesignedCard(
                        title = "od " + formatDateStringToOutputDayString(tour.startDate) + " do " + formatDateStringToOutputDayString(tour.endDate)
                    )
                }
            }
        }
    }
}
