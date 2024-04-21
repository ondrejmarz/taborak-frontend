package cz.ondrejmarz.taborak.ui.screens.forms

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.data.models.Activity
import cz.ondrejmarz.taborak.data.models.DayPlan
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.util.convertToTimestamp
import cz.ondrejmarz.taborak.data.util.formatMillisToIsoDateTime
import cz.ondrejmarz.taborak.data.viewmodel.CalendarViewModel
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.Section

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayPlanFormScreen(
    tourId: String?,
    day: String?,
    calendarViewModel: CalendarViewModel = viewModel(),
    navController: NavHostController,
) {
    var currentFieldIndex   by remember { mutableStateOf(0) }

    val fields = listOf("Budíček", "Rozcvička", "Dopolední činnost", "Odpolední činnost",
        "Podvečerní činnost", "Večerní činnost", "Nástup", "Večerka")

    var programMorning      by remember { mutableStateOf("") }
    var programAfternoon    by remember { mutableStateOf("") }
    var programEvening      by remember { mutableStateOf("") }
    var programNight        by remember { mutableStateOf("") }

    val switchStateWarmUp    = remember { mutableStateOf(true) }
    val switchStateSummon    = remember { mutableStateOf(true) }

    val wakeUpTime           = rememberTimePickerState(8, 0, true)
    val lightsOut            = rememberTimePickerState(22, 30, true)

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tvorba denního plánu") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět")
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPaddding ->
        Column(
            modifier = Modifier
                .padding(innerPaddding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Section(title = fields[currentFieldIndex]) {
                    when (currentFieldIndex) {
                        1, 6 -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Bude se konat:")
                                if (currentFieldIndex == 1) {
                                    Switch(
                                        checked = switchStateWarmUp.value,
                                        onCheckedChange = { isChecked ->
                                            switchStateWarmUp.value = isChecked
                                        }
                                    )
                                }
                                if (currentFieldIndex == 6) {
                                    Switch(
                                        checked = switchStateSummon.value,
                                        onCheckedChange = { isChecked ->
                                            switchStateSummon.value = isChecked
                                        }
                                    )
                                }
                            }
                        }
                        0, 7 -> {
                            if (currentFieldIndex == 0) { TimePicker(state = wakeUpTime) }
                            if (currentFieldIndex == 7) { TimePicker(state = lightsOut)  }
                        }
                        else -> {
                            OutlinedTextField(
                                value = when(currentFieldIndex-2) { 0->programMorning 1->programAfternoon 2->programEvening 3->programNight else -> "" },
                                onValueChange = { text -> when(currentFieldIndex-2) { 0->programMorning=text 1->programAfternoon=text 2->programEvening=text else->programNight=text } },
                                label = { Text(text = fields[currentFieldIndex]) },
                                readOnly = false,
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = {
                                    currentFieldIndex =
                                        (currentFieldIndex + 1).coerceAtMost(fields.size - 1)
                                    if (currentFieldIndex == 1 || currentFieldIndex == 6) {
                                        keyboardController?.hide()
                                    }
                                })
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        currentFieldIndex = (currentFieldIndex - 1).coerceAtLeast(0)
                    },
                    enabled = currentFieldIndex > 0,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Text("Předchozí")
                }

                Spacer(modifier = Modifier.width(10.dp))

                if (currentFieldIndex != fields.size - 1) {
                    Button(
                        onClick = {
                            currentFieldIndex = (currentFieldIndex + 1).coerceAtMost(fields.size - 1)
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Text("Další")
                    }
                }
                else {
                    Button(
                        onClick = {
                            if (day != null && tourId != null) {
                                calendarViewModel.createNewDayProgram(
                                    tourId,
                                    day,
                                    DayPlan(
                                        wakeUp = Activity(startTime = convertToTimestamp(wakeUpTime).toString()),

                                        warmUp = Activity(visible = switchStateWarmUp.value),
                                        summon = Activity(visible = switchStateSummon.value),

                                        programMorning = Activity(name = programMorning),
                                        programAfternoon = Activity(name = programAfternoon),
                                        programEvening = Activity(name = programEvening),
                                        programNight = Activity(name = programNight),

                                        lightsOut = Activity(startTime = convertToTimestamp(lightsOut).toString()),
                                    )
                                )
                            }
                            navController.popBackStack()
                        },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Text("Dokončit")
                    }
                }
            }
        }
    }
}