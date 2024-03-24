package cz.ondrejmarz.taborak.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.data.utility.formatLocalDateTime
import cz.ondrejmarz.taborak.data.viewmodel.factory.TourViewModelFactory
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.Section

@RequiresApi(Build.VERSION_CODES.O)
@Destination
@Composable
fun TourScreen(
    tourId: Long,
    navigator: DestinationsNavigator
) {
    val tourModelView = TourViewModelFactory.getTourViewModel()
    val currentTour = tourModelView.tours.value?.find { it.tourId == tourId }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                tourId = tourId,
                allScreens = appTabRowScreens,
                onItemSelected = { dir ->
                    navigator.navigate( dir )
                },
                currentScreen = "Turnus"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

            Section(title = "Aktivní ankety", modifier = Modifier.padding(innerPadding)) {
                DesignedCard(
                    title = "Turnus momentálně nemá žádné aktivní ankety",
                    description = "Anketu může vytvořit hlavní vedoucí, nebo jeho zástupci."
                )
            }

            if (currentTour != null) {

                Section(title = "Doba trvání", modifier = Modifier.padding(innerPadding)) {
                    DesignedCard(
                        title = "od " + formatLocalDateTime(currentTour.startDate)
                                + "2024 do " + formatLocalDateTime(currentTour.endDate)
                    )
                }
            }
        }
    }
}
