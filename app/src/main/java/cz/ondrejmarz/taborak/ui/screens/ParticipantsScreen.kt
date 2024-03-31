package cz.ondrejmarz.taborak.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.Section
import java.util.Date

@Composable
fun ParticipantsScreen(
    tourId: String,
    navController: NavHostController
) {
    //val currentTour = tourList.find { it.id == tourId } ?: Tour(42L,"Rodiče a děti 2024", "Dětský letní tábor s rodiči", "Hlavní vedoucí Iveta", Date(), Date(), true)

    Scaffold(
        bottomBar = {
            BottomNavBar(
                tourId = tourId,
                allScreens = appTabRowScreens,
                onItemSelected = { route ->
                    navController.popBackStack()
                    navController.navigate(route)
                },
                currentScreen = "Oddíl"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

            Section(title = "Účastníci") {
                DesignedCard(
                    title = "Turnus momentálně nemá žádné účastníky",
                    description = "Seznam účastníků můžete nahrát pomocí tlačítka níže."
                )
            }
        }
    }
}