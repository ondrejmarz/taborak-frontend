package cz.ondrejmarz.taborak.ui.screens.details

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.data.models.Activity
import cz.ondrejmarz.taborak.data.util.formatDateStringToOutputDayString
import cz.ondrejmarz.taborak.data.util.formatDateStringToOutputTimeString
import cz.ondrejmarz.taborak.data.viewmodel.CalendarViewModel
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.Section
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayPlanScreen(
    tourId: String,
    day: String?,
    calendarViewModel: CalendarViewModel = viewModel(),
    navController: NavHostController
) {
    day?.let { calendarViewModel.fetchCalendar(tourId, it) }

    val calendar by calendarViewModel.calendar.collectAsState()

    var editMode by remember { mutableStateOf(false) }
    var showEdit by remember { mutableStateOf(false) }
    var showDetail by remember { mutableStateOf(false) }

    val sheetStateEdit = rememberModalBottomSheetState()
    val sheetStateDetail = rememberModalBottomSheetState()

    val scopeEdit = rememberCoroutineScope()
    val scopeDetail = rememberCoroutineScope()

    var selectedActivity by remember { mutableStateOf(Activity()) }

    val onClickAction = { activity: Activity ->
        selectedActivity = activity
        if (editMode) { showEdit = true } else { showDetail = true }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Denní plán") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack()  }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět")
                    }
                },
                actions = {
                    IconButton(onClick = { editMode = !editMode }) {
                        Icon(Icons.Default.Edit, contentDescription = "Upravit")
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
                    if (activity.visible == true || editMode == true) {
                        when (activity.type) {
                            "Jídlo" -> DesignedCard(
                                title = activity.name?: "Nepojmenovaná aktivita",
                                description = activity.desc,
                                startTime = activity.startTime,
                                endTime = activity.endTime,
                                timeInDayFormat = false,
                                enabled = true,
                                button = if (editMode) "Upravit aktivitu" else null,
                                onClickAction = { onClickAction(activity) }
                            )
                            "Milník" -> DesignedCard(
                                title = activity.name?: "Nepojmenovaná aktivita",
                                startTime = activity.startTime,
                                endTime = activity.endTime,
                                timeInDayFormat = false,
                                enabled = true,
                                button = if (editMode) "Upravit aktivitu" else null,
                                onClickAction = { onClickAction(activity) }
                            )
                            else -> DesignedCard(
                                title = activity.name?: "Nepojmenovaná aktivita",
                                topic = activity.type,
                                //description = activity.desc,
                                startTime = activity.startTime,
                                endTime = activity.endTime,
                                timeInDayFormat = false,
                                enabled = true,
                                button = if (editMode) "Upravit aktivitu" else null,
                                onClickAction = { onClickAction(activity) }
                            )
                        }
                    }
                }
            }
        }

        if (showDetail) {
            ModalBottomSheet(
                onDismissRequest = {
                    scopeDetail.launch {
                        sheetStateDetail.hide()
                    }.invokeOnCompletion {
                        if (!sheetStateDetail.isVisible) {
                            showDetail = false
                        }
                    }
                },
                sheetState = sheetStateDetail
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    when (selectedActivity.type) {
                        "Jídlo" -> Section(title = selectedActivity.name?: "Jídlo") {
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 20.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(text = selectedActivity.desc?: "Neznámo")
                                selectedActivity.startTime?.let { Text(text = ("Od: " + formatDateStringToOutputTimeString(it))) }
                                selectedActivity.endTime?.let { Text(text = ("Do: " + formatDateStringToOutputTimeString(it))) }
                            }
                        }

                        "Milník" -> Section(title = selectedActivity.name?: "Aktivita") {
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .fillMaxWidth()
                            ) {
                                selectedActivity.desc?.let { Text(text = it) }
                                selectedActivity.startTime?.let { Text(text = ("Od: " + formatDateStringToOutputTimeString(it))) }
                                selectedActivity.endTime?.let { Text(text = ("Do: " + formatDateStringToOutputTimeString(it))) }
                            }
                        }

                        else -> Section(title = selectedActivity.type?: "Aktivita") {
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(text = selectedActivity.name?: "Neznámo")
                                Text(text = selectedActivity.desc?: "Bez popisku")
                                selectedActivity.startTime?.let { Text(text = ("Od: " + formatDateStringToOutputTimeString(it))) }
                                selectedActivity.endTime?.let { Text(text = ("Do: " + formatDateStringToOutputTimeString(it))) }
                            }
                        }
                    }
                }
            }
        }

        if (showEdit) {
            ModalBottomSheet(
                onDismissRequest = {
                    showDetail = false
                },
                sheetState = sheetStateEdit
            ) {
                var activityName    by remember { mutableStateOf(selectedActivity.name) }
                var activityDesc    by remember { mutableStateOf(selectedActivity.desc) }
                var activityStart   by remember { mutableStateOf(selectedActivity.startTime) }
                var activityEnd     by remember { mutableStateOf(selectedActivity.endTime) }
                var activityVisible by remember { mutableStateOf(selectedActivity.visible) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    when (selectedActivity.type) {
                        "Jídlo" -> Section(title = selectedActivity.name?: "Jídlo") {
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 20.dp)
                                    .fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = activityDesc?: "",
                                    onValueChange = { text -> activityDesc = text },
                                    label = { selectedActivity.name },
                                    readOnly = false,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(text = ("Od: " + formatDateStringToOutputTimeString(selectedActivity.startTime)))
                                if (selectedActivity.endTime == null) Text(text = "Do: Nespecifikováno")
                                else Text(text = "Do: " + formatDateStringToOutputTimeString(selectedActivity.endTime))
                                Text(text = ("Viditelné? " + if (selectedActivity.visible == true) "Ano" else "Ne"))
                            }
                        }
                        "Milník" -> Section(title = selectedActivity.name?: "Aktivita") {
                            Column(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth()
                            ) {
                                selectedActivity.desc?.let { Text(text = it) }
                                Text(text = ("Od: " + formatDateStringToOutputTimeString(selectedActivity.startTime)))
                                Text(text = ("Do: " + formatDateStringToOutputTimeString(selectedActivity.endTime)))
                                Text(text = ("Viditelné? " + if (selectedActivity.visible == true) "Ano" else "Ne"))
                            }
                        }

                        else -> Section(title = selectedActivity.type?: "Aktivita") {
                            Column(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(text = selectedActivity.name?: "Neznámo")
                                Text(text = selectedActivity.desc?: "Bez popisku")
                                Text(text = ("Od: " + formatDateStringToOutputTimeString(selectedActivity.startTime)))
                                Text(text = ("Do: " + formatDateStringToOutputTimeString(selectedActivity.endTime)))
                                Text(text = ("Viditelné? " + if (selectedActivity.visible == true) "Ano" else "Ne"))
                            }
                        }
                    }

                    //Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            shape = MaterialTheme.shapes.small,
                            onClick = {
                                day?.run {
                                    selectedActivity = selectedActivity.copy( desc = activityDesc )
                                    calendarViewModel.saveActivity(tourId, day, selectedActivity)
                                }
                                scopeEdit.launch {
                                    sheetStateEdit.hide()
                                }.invokeOnCompletion {
                                    if (!sheetStateEdit.isVisible) {
                                        showEdit = false
                                    }
                                }
                            }
                        ) {
                            Text(text = "Uložit")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            shape = MaterialTheme.shapes.small,
                            onClick = {
                                scopeEdit.launch {
                                    sheetStateEdit.hide()
                                }.invokeOnCompletion {
                                    if (!sheetStateEdit.isVisible) {
                                        showEdit = false
                                    }
                                }
                            }
                        ) {
                            Text(text = "Zrušit")
                        }
                    }
                }
            }
        }
    }
}
