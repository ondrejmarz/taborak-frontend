package cz.ondrejmarz.taborak.data.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.lifecycle.ViewModel
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.models.Activity
import cz.ondrejmarz.taborak.data.models.DayPlan
import cz.ondrejmarz.taborak.data.viewmodel.states.CalendarState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerializationException
import java.io.IOException

class CalendarViewModel : ViewModel() {

    private val _calendar = MutableStateFlow(CalendarState())
    val calendar: StateFlow<CalendarState> = _calendar.asStateFlow()

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

    private fun createDayPlan(responseBody: String): DayPlan {
        return try {
            Json.decodeFromString(responseBody)
        } catch (e: SerializationException) {
            e.printStackTrace()
            DayPlan()
        }
    }
}