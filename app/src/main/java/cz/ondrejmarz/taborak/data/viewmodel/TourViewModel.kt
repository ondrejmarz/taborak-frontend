package cz.ondrejmarz.taborak.data.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.lifecycle.ViewModel
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.util.getCurrentDate
import cz.ondrejmarz.taborak.data.viewmodel.states.TourListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class TourViewModel : ViewModel() {

    private val _tours = MutableStateFlow(TourListState())
    val tours: StateFlow<TourListState> = _tours.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchTours() {
        ApiClient.fetchTours { responseBody: String ->
            _tours.update { tourListState ->
                tourListState.copy(
                    listedTours = createTourList(responseBody)
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNewTour(newTour: Tour) {
        ApiClient.createTour(
            newTour,
            onSuccess = { fetchTours() },
            onFailure = { e: IOException -> println(e.message) }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteTour(tourId: String) {
        ApiClient.deleteTour(
            tourId,
            onSuccess = { fetchTours() },
            onFailure = { e: IOException -> println(e.message) }
        )
    }

    private fun createTourList(responseBody: String): List<Tour> {
        return try {
            Json.decodeFromString(responseBody)
        } catch (e: SerializationException) {
            e.printStackTrace()
            emptyList()
        }
    }
}