package cz.ondrejmarz.taborak.ui.screens.details

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.data.viewmodel.CalendarViewModel
import cz.ondrejmarz.taborak.ui.components.DesignedCard

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDayScreen(
    tourId: String,
    day: String?,
    calendarViewModel: CalendarViewModel = viewModel(),
    navController: NavHostController
) {
    day?.let { calendarViewModel.fetchCalendar(tourId, it) }

    val calendar by calendarViewModel.calendar.collectAsState()

    println("Pro den $day načteno ${calendar.dayProgram?.getActivityList?.size} aktivit.")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Denní plán") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack()  }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět")
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            val activityList = calendar.dayProgram?.getActivityList?.sortedBy { it.startTime }
            if (activityList != null) {
                itemsIndexed(activityList) { index, activity ->
                    if (activity.visible == true) {
                        when (activity.type) {
                            "Jídlo" -> DesignedCard(
                                title = activity.name?: "Nepojmenovaná aktivita",
                                startTime = activity.startTime,
                                endTime = activity.endTime,
                                timeInDayFormat = false
                            )
                            "Milník" -> DesignedCard(
                                title = activity.name?: "Nepojmenovaná aktivita",
                                startTime = activity.startTime,
                                endTime = activity.endTime,
                                timeInDayFormat = false
                            )
                            else -> DesignedCard(
                                title = activity.name?: "Nepojmenovaná aktivita",
                                topic = activity.type,
                                description = activity.desc,
                                startTime = activity.startTime,
                                endTime = activity.endTime,
                                timeInDayFormat = false
                            )
                        }
                    }
                }
            }
        }
    }
}
