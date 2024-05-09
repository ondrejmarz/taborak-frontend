package cz.ondrejmarz.taborak.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.ui.viewmodels.states.TourListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import java.io.IOException


@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel : ViewModel() {

    private val _isCreatedSuccessfully = MutableStateFlow("")
    val isCreatedSuccessfully: StateFlow<String> = _isCreatedSuccessfully.asStateFlow()

    private val _tours = MutableStateFlow(TourListState())
    val tours: StateFlow<TourListState> = _tours.asStateFlow()

    fun fetchTours() {
        viewModelScope.launch {
            _tours.update { state -> state.copy( isLoading = true ) }
            ApiClient.fetchTours { responseBody: String ->
                _tours.update { tourListState ->
                    tourListState.copy(
                        listedTours = createTourList(responseBody)
                    )
                }
            }
            _tours.update { state -> state.copy( isLoading = false ) }
        }
    }

    fun createNewTour(newTour: Tour) {
        viewModelScope.launch {
            _isCreatedSuccessfully.update { "creating" }
            ApiClient.createTour(
                newTour,
                onSuccess = {
                    _isCreatedSuccessfully.update { "created" }
                    fetchTours()
                },
                onFailure = { e: IOException ->
                    println(e.message)
                    _isCreatedSuccessfully.update { "error" }
                }
            )
        }
    }

    fun sendApplication(tourId: String, userId: String?) {
        viewModelScope.launch {
            userId?.let {
                ApiClient.addTourApplication(tourId, it) { }
            }
            fetchTours()
        }
    }

    private fun createTourList(responseBody: String): List<Tour> {
        return try {
            Json.decodeFromString(responseBody)
        } catch (e: SerializationException) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun resetTourCreationState() {
        _isCreatedSuccessfully.update { "default" }
    }
}