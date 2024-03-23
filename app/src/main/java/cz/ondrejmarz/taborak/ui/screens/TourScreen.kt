package cz.ondrejmarz.taborak.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import cz.ondrejmarz.taborak.Overview
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.model.Tour
import cz.ondrejmarz.taborak.model.tourList
import cz.ondrejmarz.taborak.model.utility.fromDateToStringFormattedTime
import cz.ondrejmarz.taborak.ui.components.AppTabRow
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.Section
import java.util.Date

@Destination
@Composable
fun TourScreen(
    tourId: Long,
    navigator: DestinationsNavigator
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen =
        appTabRowScreens.find { it.route == currentDestination?.route } ?: Overview

    val currentTour = tourList.find { it.id == tourId } ?: Tour(42L,"Rodiče a děti 2024", "Dětský letní tábor s rodiči", "Hlavní vedoucí Iveta", Date(), Date(), true)

    Scaffold(
        bottomBar = {
            AppTabRow(
                allScreens = appTabRowScreens,
                onTabSelected = { newScreen ->
                    navigator.navigate(newScreen.route)
                },
                currentScreen = currentScreen
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

            Section(title = "Aktivní ankety", modifier = Modifier.padding(innerPadding)) {
                DesignedCard(
                    title = "Tunus momentálně nemá žádné aktivní ankety",
                    description = "Anketu může vytvořit hlavní vedoucí, nebo jeho zástupci."
                )
            }

            Section(title = "Doba trvání", modifier = Modifier.padding(innerPadding)) {
                DesignedCard(
                    title = "od " +  fromDateToStringFormattedTime(currentTour.startDate) + "2024 do " + fromDateToStringFormattedTime(currentTour.endDate) + "2024"
                )
            }
        }
    }
}