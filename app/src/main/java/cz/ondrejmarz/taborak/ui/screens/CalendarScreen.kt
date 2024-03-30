package cz.ondrejmarz.taborak.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.Section

@Composable
fun CalendarScreen(
    tourId: String,
    navController: NavHostController
) {
    //val currentTour = tourList.find { it.id == tourId } ?: Tour(42L,"Rodiče a děti 2024", "Dětský letní tábor s rodiči", "Hlavní vedoucí Iveta", Date(), Date(), true)

    Scaffold(
        bottomBar = {
            BottomNavBar(
                tourId = tourId,
                allScreens = appTabRowScreens,
                onItemSelected = { /*dir ->
                    navigator.navigate( dir )*/
                },
                currentScreen = "Kalendář"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

            Section(title = "Denní plán") {
                DesignedCard(
                    title = "Turnus momentálně nemá vytvořený denní plán",
                    description = "Denní plán může vytvořit hlavní vedoucí, nebo zástupci."
                )
            }

            Section(title = "Jídelníček") {
                DesignedCard(
                    title = "Turnus momentálně nemá vytvořený jídelníček",
                    description = "Jídelníček může vytvořit hlavní vedoucí, nebo zástupci."
                )
            }

            Section(title = "Služba") {
                DesignedCard(
                    title = "Turnus momentálně nemá přiřazenou službu",
                    description = "Službu může přiřadit hlavní vedoucí, nebo zástupci."
                )
            }
        }
    }
}
