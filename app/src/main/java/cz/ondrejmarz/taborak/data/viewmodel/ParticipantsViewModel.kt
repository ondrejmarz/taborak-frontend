package cz.ondrejmarz.taborak.data.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.lifecycle.ViewModel
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.models.Group
import cz.ondrejmarz.taborak.data.viewmodel.states.ParticipantsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerializationException


class ParticipantsViewModel : ViewModel() {

    private val _participants = MutableStateFlow(ParticipantsState())
    val participants: StateFlow<ParticipantsState> = _participants.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchParticipants(tourId: String) {
        ApiClient.fetchParticipants(tourId, { responseBody: String ->
            _participants.update { participantsListState ->
                participantsListState.copy(
                    groupList = createGroupList(responseBody)
                )
            }
        }, {
            println(it)
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadParticipantXlsx(tourId: String, participantData: ByteArray) {
        ApiClient.uploadParticipantXlsx(tourId, participantData, { responseBody: String ->
            _participants.update { participantsState ->
                participantsState.copy(
                    groupList = createGroupList(responseBody)
                )
            }
        }, {
            println(it)
        })
    }

    /*
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
    */

    private fun createGroupList(responseBody: String): List<Group> {
        return try {
            Json.decodeFromString(responseBody)
        } catch (e: SerializationException) {
            e.printStackTrace()
            emptyList()
        }
    }
}