package cz.ondrejmarz.taborak.ui.screens.menu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.R
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.data.models.Activity
import cz.ondrejmarz.taborak.data.models.DayPlan
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.util.convertToTimestamp
import cz.ondrejmarz.taborak.data.util.formatMillisToIsoDay
import cz.ondrejmarz.taborak.data.util.fromDayToMillis
import cz.ondrejmarz.taborak.data.util.fromDayToReadableDay
import cz.ondrejmarz.taborak.data.util.getCurrentDate
import cz.ondrejmarz.taborak.ui.viewmodels.CalendarViewModel
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.DesignedBottomSheet
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.LoadingIcon
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.components.TwoOptionButtons
import cz.ondrejmarz.taborak.ui.screens.TourForm
import cz.ondrejmarz.taborak.ui.viewmodels.factory.CalendarViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    tourId: String,
    calendarViewModel: CalendarViewModel = viewModel(factory = CalendarViewModelFactory(tourId)),
    navController: NavHostController
) {
    val tour by calendarViewModel.tour.collectAsState()
    val day by calendarViewModel.day.collectAsState()

    var showSelectDayDialog by remember { mutableStateOf(false) }
    var showBottomSheetDayPlanForm by remember { mutableStateOf(false) }
    val sheetStateDayPlanForm = rememberModalBottomSheetState(true)

    val datePickerState by remember(tour) {
        val startDate = tour.startDate?.let { LocalDateTime.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")) }
        val endDate = tour.endDate?.let { LocalDateTime.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")) }

        val defaultStartDate = LocalDateTime.now().plusDays(0) // Default values before tour initialization
        val defaultEndDate = LocalDateTime.now().plusDays(2) // Default values before tour initialization

        derivedStateOf {
            val finalStartDate = startDate?.plusHours(12) ?: defaultStartDate
            val finalEndDate = endDate?.plusHours(12) ?: defaultEndDate

            DatePickerState(
                locale = Locale.getDefault(),
                initialSelectedDateMillis = day.fromDayToMillis() + 1000 * 60 * 60 * 12,
                initialDisplayMode = DisplayMode.Picker,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val selectedDate = LocalDate.ofEpochDay(utcTimeMillis / 86400000)
                        return !selectedDate.isBefore(finalStartDate.toLocalDate()) && !selectedDate.isAfter(finalEndDate.toLocalDate())
                    }
                }
            )
        }
    }

    val calendar by calendarViewModel.calendar.collectAsState()

    val scope = rememberCoroutineScope()

    val activityCurrent = findClosestActivity(calendar.dayProgram?.getActivityList)
    //val activityNext = findNextActivity(calendar.dayProgram?.getActivityList)



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                tourId = tourId,
                allScreens = appTabRowScreens,
                onItemSelected = { route ->
                    navController.navigate(route)
                },
                currentScreen = "Kalendář"
            )
        }
    ) { innerPadding ->

        /*
         * Calendar has three different views:
         *      1) day plan is created for selected day and it is current day
         *      2) day plan is created for selected day and it is not current day
         *      3) day plan is not created for selected day
         *
         * */

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Button(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                onClick = { showSelectDayDialog = true }
            ) {
                Text(text = "Vybraný den: " + day.fromDayToReadableDay())
            }

            when {
                calendar.dayProgram != null -> {
                    if (day == getCurrentDate() && activityCurrent != null) {
                        Section(
                            title = "Právě probíhá",
                            onButtonClick = { navController.navigate("daily_plan/$tourId/$day") },
                            buttonTitle = "Zobrazit vše"
                        ) {
                            DesignedCard(
                                title = activityCurrent.name?: "Nepojmenovaná aktivita",
                                topic = if (activityCurrent.type != "Jídlo" && activityCurrent.type != "Milník") activityCurrent.type else null,
                                description = activityCurrent.desc,
                                startTime = activityCurrent.startTime,
                                endTime = activityCurrent.endTime,
                                timeInDayFormat = false
                            )
                        }
                    }
                    else {
                        Section(
                            title = "Denní plán",
                            onButtonClick = { navController.navigate("daily_plan/$tourId/$day") },
                            buttonTitle = "Zobrazit vše"
                        ) {
                            ActivityList(
                                listOf(
                                    calendar.dayProgram?.programMorning,
                                    calendar.dayProgram?.programAfternoon,
                                    calendar.dayProgram?.programEvening,
                                    calendar.dayProgram?.programNight
                                )
                            )
                        }
                    }
                }

                calendar.isLoading -> {
                    LoadingIcon()
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Section(
                            title = "Denní plán",
                            onButtonClick = { showBottomSheetDayPlanForm = true
                                //navController.navigate("daily_plan_create/$tourId/$day")
                            },
                            buttonTitle = "Vytvořit"
                        ) {
                            DesignedCard(
                                title = "Denní plán není vytvořený",
                                description = "Můžete požádat hlavní vedoucího o vytvoření denního plánu"
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.calendar_pana),
                            contentDescription = null
                        )
                    }
                }
            }

            val menuList = getMenuItems(activityList = calendar.dayProgram?.getActivityList)
            if (menuList != null) {
                Section(title = "Jídelníček") {
                    menuList.forEach { activity ->
                        DesignedCard(
                            title = activity.desc ?: "Neznámo",
                            description = activity.name
                        )
                    }
                }
            }
        }

        if (showBottomSheetDayPlanForm) {
            DesignedBottomSheet(
                state = sheetStateDayPlanForm,
                onDismiss = {
                    scope.launch {
                        sheetStateDayPlanForm.hide()
                    }.invokeOnCompletion {
                        if (!sheetStateDayPlanForm.isVisible) {
                            showBottomSheetDayPlanForm = false
                        }
                    }
                }
            ) {
                DayPlanForm(
                    onDismiss = {
                        scope.launch {
                            sheetStateDayPlanForm.hide()
                        }.invokeOnCompletion {
                            if (!sheetStateDayPlanForm.isVisible) {
                                showBottomSheetDayPlanForm = false
                            }
                        }
                    },
                    onCreate = { dayPlan: DayPlan ->
                        calendarViewModel.createNewDayProgram(
                            dayPlan
                        )
                    }
                )
            }
        }

        if (showSelectDayDialog) {

            DatePickerDialog(
                shape = MaterialTheme.shapes.small,
                tonalElevation = 0.dp,
                //modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small),
                onDismissRequest = { showSelectDayDialog = false },
                dismissButton = {
                    OutlinedButton(
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.small,
                        onClick = {
                            showSelectDayDialog = false
                        }
                    ) {
                        Text(text = "Zrušit")
                    }
                },
                confirmButton = {
                    Button(
                        //modifier = Modifier.padding(end = 20.dp),
                        shape = MaterialTheme.shapes.small,
                        onClick = {
                            calendarViewModel.daySelected(formatMillisToIsoDay(datePickerState.selectedDateMillis))
                            calendarViewModel.fetchCalendar()
                            showSelectDayDialog = false
                        }
                    ) {
                        Text(text = "Potvrdit")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    title = {
                        Box(modifier = Modifier.padding(start = 20.dp, top = 20.dp)) {
                            Text(text = "Zvolte datum")
                        }
                    },
                    headline = {
                        Box(modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)) {
                            Text(text = formatMillisToIsoDay(datePickerState.selectedDateMillis).fromDayToReadableDay(), style = MaterialTheme.typography.titleLarge)
                        }
                    },
                    showModeToggle = false
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayPlanForm(onDismiss: () -> Unit, onCreate: (DayPlan) -> Unit) {

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

    Column(
        modifier = Modifier
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
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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
                        onCreate(
                            DayPlan(
                                wakeUp = Activity(startTime = convertToTimestamp(wakeUpTime).toString()),

                                warmUp = Activity(visible = switchStateWarmUp.value),
                                summon = Activity(visible = switchStateSummon.value),

                                programMorning = Activity(name = programMorning),
                                programAfternoon = Activity(name = programAfternoon),
                                programEvening = Activity(name = programEvening),
                                programNight = Activity(name = programNight),

                                lightsOut = Activity(startTime = convertToTimestamp(lightsOut).toString()
                                )
                            )
                        )
                        onDismiss()
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

@Composable
fun ActivityList(activityList: List<Activity?>) {
    Column {
        activityList.forEach { activity ->
            if (activity != null && activity.visible == true) {
                DesignedCard(
                    title = activity.name ?: "Nepojmenovaná aktivita",
                    topic = activity.type,
                    startTime = activity.startTime,
                    endTime = activity.endTime,
                    timeInDayFormat = false
                )
            }
        }
    }
}

fun getMenuItems(activityList: List<Activity>?): List<Activity>? {
    if (activityList == null) return null

    val menuItems: MutableList<Activity> = mutableListOf()

    for (activity in activityList) {
        if (activity.type == "Jídlo" && activity.desc != null)
            menuItems += activity
    }

    return if (menuItems.isEmpty()) null else menuItems
}



@RequiresApi(Build.VERSION_CODES.O)
fun findClosestActivity(activityList: List<Activity>?): Activity? {
    if (activityList == null) return null

    val currentDateTime = Date(Date().time)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    var closestValidActivity: Activity? = null
    var closestStartTime: Date? = null

    for (activity in activityList) {
        val startTime = activity.startTime?.let { dateFormat.parse(it) }

        if (startTime != null
            && startTime.before(currentDateTime)
            && (closestStartTime == null || startTime.after(closestStartTime)))
        {
            closestStartTime = startTime
            closestValidActivity = activity
        }
    }

    return closestValidActivity
}

@RequiresApi(Build.VERSION_CODES.O)
fun findNextActivity(activityList: List<Activity>?): Activity? {
    if (activityList == null) return null

    val currentDateTime = Date(Date().time)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")

    var nearestActivity: Activity? = null
    var nearestStartTime: Date? = null

    for (activity in activityList) {
        val startTime = activity.startTime?.let { dateFormat.parse(it) }

        if (startTime != null
            && startTime.after(currentDateTime)
            && (nearestStartTime == null || startTime.before(nearestStartTime)))
        {
            nearestStartTime = startTime
            nearestActivity = activity
        }
    }

    return nearestActivity
}