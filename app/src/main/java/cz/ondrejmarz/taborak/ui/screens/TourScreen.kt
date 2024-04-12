package cz.ondrejmarz.taborak.ui.screens

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
import cz.ondrejmarz.taborak.data.viewmodel.TourViewModel
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.Section

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TourScreen(
    tourId: String,
    tourViewModel: TourViewModel = viewModel(),
    navController: NavHostController
) {
    val tours by tourViewModel.tours.collectAsState()
    val currentTour = tours.listedTours.find { it.tourId == tourId }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                tourId = tourId,
                allScreens = appTabRowScreens,
                onItemSelected = { route ->
                    navController.popBackStack()
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

            if (currentTour != null) {

                Section(title = "Doba trvání") {
                    DesignedCard(
                        title = "od " + formatDateStringToOutputDayString(currentTour.startDate) + " do " + formatDateStringToOutputDayString(currentTour.endDate)
                    )
                }
            }
        }
    }
}
