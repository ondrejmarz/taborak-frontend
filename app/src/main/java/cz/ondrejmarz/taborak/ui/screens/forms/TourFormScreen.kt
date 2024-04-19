package cz.ondrejmarz.taborak.ui.screens.forms

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.util.formatMillisToIsoDateTime
import cz.ondrejmarz.taborak.data.util.toMillis
import cz.ondrejmarz.taborak.data.viewmodel.TourViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TourFormScreen(
    navController: NavHostController,
    tourViewModel: TourViewModel = viewModel(),
    userData: UserData? = null,
) {
    val dateTime = LocalDateTime.now()

    val dateRangePickerState = remember {
        DateRangePickerState(
            locale = Locale.getDefault(),
            initialSelectedStartDateMillis = dateTime.toMillis(),
            initialDisplayedMonthMillis = null,
            initialSelectedEndDateMillis = dateTime.plusDays(11).toMillis(),
            initialDisplayMode = DisplayMode.Input
        )
    }
    var title by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tvorba turnusu") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět")
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Název") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Téma") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Popis") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            DateRangePicker(state = dateRangePickerState)

            Button(
                onClick = {
                    if (title != "" && userData != null) {
                        tourViewModel.createNewTour(
                            Tour(
                                title = title,
                                description = description,
                                topic = topic,
                                endDate = formatMillisToIsoDateTime(dateRangePickerState.selectedEndDateMillis?: 0),
                                startDate = formatMillisToIsoDateTime(dateRangePickerState.selectedStartDateMillis?: 0),
                                members = listOf(userData.userId),
                                applications = null,
                                groups = null,
                                dailyPrograms = null
                            )
                        )
                    }
                    navController.popBackStack() },
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(alignment = Alignment.End),
                shape = MaterialTheme.shapes.small
            ) {
                Text(text = "Vytvořit")
            }
        }
    }
}

