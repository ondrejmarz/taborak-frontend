package cz.ondrejmarz.taborak.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.models.Activity
import cz.ondrejmarz.taborak.data.models.DayPlan
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.util.getCurrentDate
import cz.ondrejmarz.taborak.ui.viewmodels.states.CalendarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class CalendarViewModel(private val tourId: String) : ViewModel() {

    private val _tour = MutableStateFlow(Tour())
    val tour: StateFlow<Tour> = _tour.asStateFlow()

    private val _day = MutableStateFlow(getCurrentDate())
    val day: StateFlow<String> = _day.asStateFlow()

    private val _calendar = MutableStateFlow(CalendarState())
    val calendar: StateFlow<CalendarState> = _calendar.asStateFlow()

    init {
        fetchTour()
        fetchCalendar(day.value)
    }

    private fun fetchTour() {
        viewModelScope.launch {
            ApiClient.fetchTour(tourId) { responseBody: String ->
                val newTour = createTour(responseBody)
                _tour.update {
                    newTour
                }
                _day.update {
                    getClosestTourDate(newTour)
                }
            }
        }
    }

    fun daySelected(day: String) {
        viewModelScope.launch {
            _day.update { day }
            fetchCalendar(day)
        }
    }

    fun fetchCalendar(day: String) {
        viewModelScope.launch {
            ApiClient.fetchCalendar(tourId, day, { responseBody: String ->
                _calendar.update { activityState ->
                    activityState.copy(
                        dayProgram = createDayPlan(responseBody)
                    )
                }
            }, {
                _calendar.update { activityState ->
                    activityState.copy(
                        dayProgram = DayPlan()
                    )
                }
            })
        }
    }

    fun createNewDayProgram(day: String, dayPlan: DayPlan) {
        viewModelScope.launch {
            ApiClient.createActivities(
                tourId,
                day,
                dayPlan,
                onSuccess = { fetchCalendar(day) },
                onFailure = { e: IOException -> println(e.message) }
            )
        }
    }

    fun saveActivity(day: String, activity: Activity) {
        viewModelScope.launch {
            val dayPlan: DayPlan = _calendar.value.dayProgram ?: return@launch

            if (activity.type == "Jídlo" || activity.type == "Milník") {
                when (activity.name) {
                    "Budíček" -> dayPlan.wakeUp = activity
                    "Rozcvička" -> dayPlan.warmUp = activity
                    "Snídaně" -> dayPlan.dishBreakfast = activity
                    "Dopolední svačina" -> dayPlan.dishMorningSnack = activity
                    "Oběd" -> dayPlan.dishLunch = activity
                    "Odpolední svačina" -> dayPlan.dishAfternoonSnack = activity
                    "Nástup" -> dayPlan.summon = activity
                    "Večeře" -> dayPlan.dishDinner = activity
                    "Druhá večeře" -> dayPlan.dishEveningSnack = activity
                    "Příprava na večerku" -> dayPlan.prepForNight = activity
                    "Večerka" -> dayPlan.lightsOut = activity
                }
            }
            else {
                when (activity.type) {
                    "Dopolední činnost" -> dayPlan.programMorning = activity
                    "Odpolední činnost" -> dayPlan.programAfternoon = activity
                    "Podvečení činnost" -> dayPlan.programEvening = activity
                    "Večerní činnost" -> dayPlan.programNight = activity
                }
            }

            ApiClient.updateDayPlan(
                tourId,
                day,
                dayPlan,
                onSuccess = { fetchCalendar(day) },
                onFailure = { e: IOException -> println(e.message) }
            )
        }
    }

    private fun createTour(responseBody: String): Tour {
        return try {
            Json.decodeFromString(responseBody)
        } catch (e: SerializationException) {
            e.printStackTrace()
            Tour()
        }
    }

    private fun createDayPlan(responseBody: String): DayPlan {
        return try {
            Json.decodeFromString(responseBody)
        } catch (e: SerializationException) {
            e.printStackTrace()
            DayPlan()
        }
    }

    private fun getClosestTourDate(currentTour: Tour?): String {

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
}