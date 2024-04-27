package cz.ondrejmarz.taborak.ui.screens.menu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.R
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.data.models.Activity
import cz.ondrejmarz.taborak.data.util.formatMillisToIsoDay
import cz.ondrejmarz.taborak.data.util.fromDayToMillis
import cz.ondrejmarz.taborak.data.util.fromDayToReadableDay
import cz.ondrejmarz.taborak.data.util.getCurrentDate
import cz.ondrejmarz.taborak.ui.viewmodels.CalendarViewModel
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.components.TwoOptionButtons
import cz.ondrejmarz.taborak.ui.viewmodels.factory.CalendarViewModelFactory
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

            if (calendar.dayProgram?.dayId != null) {
                // Calendar view 1)
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
                // Calendar view 2)
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
            // Calendar view 3)
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Section(
                        title = "Denní plán",
                        onButtonClick = { navController.navigate("daily_plan_create/$tourId/$day") },
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

@Composable
fun ActivityList(activityList: List<Activity?>) {
    Column {
        activityList.forEach {
            if (it != null) {
                DesignedCard(
                    title = it.name ?: "Nepojmenovaná aktivita",
                    topic = it.type,
                    startTime = it.startTime,
                    endTime = it.endTime,
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