package cz.ondrejmarz.taborak.data.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.lifecycle.ViewModel
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.models.Activity
import cz.ondrejmarz.taborak.data.models.DayPlan
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.viewmodel.states.CalendarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerializationException
import java.io.IOException

class CalendarViewModel : ViewModel() {

    private val _tour = MutableStateFlow(Tour())
    val tour: StateFlow<Tour> = _tour.asStateFlow()

    private val _calendar = MutableStateFlow(CalendarState())
    val calendar: StateFlow<CalendarState> = _calendar.asStateFlow()

    fun fetchTour(tourId: String) {
        ApiClient.fetchTour(tourId) { responseBody: String ->
            val newTour = createTour(responseBody)
            _tour.update {
                newTour
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchCalendar(tourId: String, day: String) {
        ApiClient.fetchCalendarData(tourId, day, { responseBody: String ->
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNewDayProgram(tourId: String, day: String, dayPlan: DayPlan) {
        ApiClient.createActivities(
            tourId,
            day,
            dayPlan,
            onSuccess = { fetchCalendar(tourId, day) },
            onFailure = { e: IOException -> println(e.message) }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveActivity(tourId: String, day: String, activity: Activity) {

        var dayPlan: DayPlan? = _calendar.value.dayProgram ?: return

        if (activity.type == "Jídlo" || activity.type == "Milník") {
            when (activity.name) {
                "Budíček" -> dayPlan?.wakeUp = activity
                "Rozcvička" -> dayPlan?.warmUp = activity
                "Snídaně" -> dayPlan?.dishBreakfast = activity
                "Dopolední svačina" -> dayPlan?.dishMorningSnack = activity
                "Oběd" -> dayPlan?.dishLunch = activity
                "Odpolední svačina" -> dayPlan?.dishAfternoonSnack = activity
                "Nástup" -> dayPlan?.summon = activity
                "Večeře" -> dayPlan?.dishDinner = activity
                "Druhá večeře" -> dayPlan?.dishEveningSnack = activity
                "Příprava na večerku" -> dayPlan?.prepForNight = activity
                "Večerka" -> dayPlan?.lightsOut = activity
            }
        }
        else {
            when (activity.type) {
                "Dopolední činnost" -> dayPlan?.programMorning = activity
                "Odpolední činnost" -> dayPlan?.programAfternoon = activity
                "Podvečení činnost" -> dayPlan?.programEvening = activity
                "Večerní činnost" -> dayPlan?.programNight = activity
            }
        }

        ApiClient.updateDayPlan(
            tourId,
            day,
            dayPlan,
            onSuccess = { fetchCalendar(tourId, day) },
            onFailure = { e: IOException -> println(e.message) }
        )
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
}