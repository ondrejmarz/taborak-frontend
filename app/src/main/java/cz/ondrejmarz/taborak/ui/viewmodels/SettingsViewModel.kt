package cz.ondrejmarz.taborak.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.ui.viewmodels.states.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.O)
class SettingsViewModel(
    private val tourId: String,
    private val userId: String
) : ViewModel() {

    private val _tour = MutableStateFlow(Tour())
    val tour: StateFlow<Tour> = _tour.asStateFlow()

    private val _role = MutableStateFlow(UserRole.ERROR)
    val role: StateFlow<UserRole> = _role.asStateFlow()

    init {
        fetchTour()
        loadTourUserRole()
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

    private fun loadTourUserRole() {
        viewModelScope.launch {
            ApiClient.getRole(tourId, userId) { responseBody: String ->
                _role.update {
                    UserRole.fromString(responseBody)
                }
            }
        }
    }

    fun deleteTour() {
        runBlocking {
            ApiClient.deleteTour(
                tourId,
                onSuccess = {  },
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
}