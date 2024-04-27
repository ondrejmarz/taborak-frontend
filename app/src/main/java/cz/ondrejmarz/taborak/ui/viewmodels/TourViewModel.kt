package cz.ondrejmarz.taborak.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.models.Tour
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class TourViewModel(private val tourId: String) : ViewModel() {

    private val _tour = MutableStateFlow(Tour())
    val tour: StateFlow<Tour> = _tour.asStateFlow()

    init {
        fetchTour()
    }

    private fun fetchTour() {
        viewModelScope.launch {
            ApiClient.fetchTour(tourId) { responseBody: String ->
                val newTour = createTour(responseBody)
                _tour.update {
                    newTour
                }
            }
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
}