package cz.ondrejmarz.taborak.ui.screens.menu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.material.datepicker.CalendarConstraints
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.data.models.Activity
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.util.formatMillisToIsoDay
import cz.ondrejmarz.taborak.data.util.fromDayToMillis
import cz.ondrejmarz.taborak.data.util.fromDayToReadableDay
import cz.ondrejmarz.taborak.data.util.getCurrentDate
import cz.ondrejmarz.taborak.data.util.toMillis
import cz.ondrejmarz.taborak.data.viewmodel.CalendarViewModel
import cz.ondrejmarz.taborak.data.viewmodel.TourViewModel
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.Section
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
    tourViewModel: TourViewModel = viewModel(),
    calendarViewModel: CalendarViewModel = viewModel(),
    navController: NavHostController
) {
    tourViewModel.fetchTours()
    val tours by tourViewModel.tours.collectAsState()
    val currentTour = tours.listedTours.find { it.tourId == tourId }

    var showSelectDayDialog by remember { mutableStateOf(false) }

    var selectedDay by rememberSaveable { mutableStateOf(getClosestTourDate(currentTour)) }

    val datePickerState = remember {
        var startDate = currentTour?.startDate?.let { LocalDateTime.parse(currentTour.startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")) }
        var endDate = currentTour?.endDate?.let { LocalDateTime.parse(currentTour.endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")) }

        startDate = startDate?.plusDays(1)
        endDate = endDate?.plusDays(1)

        DatePickerState(
            locale = Locale.getDefault(),
            initialSelectedDateMillis = selectedDay.fromDayToMillis() + 1000 * 60 * 60 * 12,
            initialDisplayMode = DisplayMode.Picker,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val selectedDate = LocalDate.ofEpochDay(utcTimeMillis / 86400000)
                    return !selectedDate.isBefore(startDate?.toLocalDate()) && !selectedDate.isAfter(endDate?.toLocalDate())
                }
            }
        )
    }

    calendarViewModel.fetchCalendar(tourId, selectedDay)
    val calendar by calendarViewModel.calendar.collectAsState()

    val activityCurrent = findClosestActivity(calendar.dayProgram?.getActivityList)
    //val activityNext = findNextActivity(calendar.dayProgram?.getActivityList)

    Scaffold(
        bottomBar = {
            BottomNavBar(
                tourId = tourId,
                allScreens = appTabRowScreens,
                onItemSelected = { route ->
                    navController.navigate(route)
                },
                currentScreen = "Kalendář"
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Section(
                title = "Den ${selectedDay.fromDayToReadableDay()}",
                onButtonClick = { showSelectDayDialog = true },
                buttonTitle = "Vybrat den"
            ) { }
            if (calendar.dayProgram?.dayId != null) {
                if (selectedDay == getCurrentDate() && activityCurrent != null) {
                    Section(
                        title = "Právě probíhá",
                        onButtonClick = { navController.navigate("daily_plan/$tourId/$selectedDay") },
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
                        onButtonClick = { navController.navigate("daily_plan/$tourId/$selectedDay") },
                        buttonTitle = "Zobrazit"
                    ) {
                        DesignedCard(
                            title = "Tento den má vytvořený plán"
                        )
                    }
                }

            }
            else {
                Section(
                    title = "Denní plán",
                    onButtonClick = { navController.navigate("daily_plan_create/$tourId/$selectedDay") },
                    buttonTitle = "Vytvořit"
                ) {
                    DesignedCard(
                        title = "Denní plán není vytvořený",
                        description = "Můžete požádat hlavní vedoucího o vytvoření denního plánu"
                    )
                }
            }

            val menuList = getMenuItems(activityList = calendar.dayProgram?.getActivityList)
            if (menuList != null) {
                Section(title = "Jídelníček") {
                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        items(menuList) { menuItem ->
                            DesignedCard(
                                title = menuItem.desc ?: "Neznámo",
                                description = menuItem.name
                            )
                        }
                    }
                }
            }

            Section(title = "Služba") {
                DesignedCard(
                    title = "Turnus momentálně nemá přiřazenou službu",
                    description = "Službu může přiřadit hlavní vedoucí, nebo zástupci."
                )
            }

            if (showSelectDayDialog) {
                DatePickerDialog(
                    modifier = Modifier
                        .padding(20.dp),
                    onDismissRequest = { showSelectDayDialog = false },
                    confirmButton = {
                        selectedDay = formatMillisToIsoDay(datePickerState.selectedDateMillis)
                        println("Zvolený den $selectedDay")
                        calendarViewModel.fetchCalendar(tourId, selectedDay)
                        //showSelectDayDialog = false
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
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

fun getClosestTourDate(currentTour: Tour?): String {

    if (currentTour == null) return getCurrentDate()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    val dateOutputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDate = Date()

    if (currentTour.startDate == null || currentTour.endDate == null) return getCurrentDate()

    val startDate = dateFormat.parse(currentTour.startDate)
    val endDate = dateFormat.parse(currentTour.endDate)

    if (startDate == null || endDate == null) return getCurrentDate()

    return when {
        currentDate.after(endDate) -> {
            dateOutputFormat.format(endDate)
        }
        currentDate.before(startDate) -> {
            dateOutputFormat.format(startDate)
        }
        else -> {
            dateOutputFormat.format(currentDate)
        }
    }
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