package cz.ondrejmarz.taborak.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.models.Group
import cz.ondrejmarz.taborak.data.models.Participant
import cz.ondrejmarz.taborak.ui.viewmodels.states.ParticipantsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException

@RequiresApi(Build.VERSION_CODES.O)
class ParticipantsViewModel(private val tourId: String) : ViewModel() {

    private val _participants = MutableStateFlow(ParticipantsState())
    val participants: StateFlow<ParticipantsState> = _participants.asStateFlow()

    fun fetchParticipants() {
        viewModelScope.launch {
            _participants.update { state -> state.copy( isLoading = true ) }
            ApiClient.fetchParticipants(tourId, { responseBody: String ->
                _participants.update { participantsListState ->
                    participantsListState.copy(
                        groupList = createGroupList(responseBody)
                    )
                }
            }, {
                println(it)
            })
            _participants.update { state -> state.copy( isLoading = false ) }
        }
    }

    fun assignNewParticipants(groupId: Int, selected: List<Participant>) {
        val allGroups = participants.value.groupList

        if (allGroups == null || groupId < 0 || groupId >= allGroups.size || selected.isEmpty()) {
            return
        }

        val participantsToAdd = selected.toMutableList()

        var groups: MutableList<Group> = mutableListOf()

        // Projdeme všechny skupiny kromě cílové skupiny
        for (i in allGroups.indices) {
            if (i != groupId) {
                // Odstranění vybraných účastníků ze všech skupin kromě cílové skupiny
                val group = allGroups[i]
                participantsToAdd.removeAll { it in group.participants }
                groups.add(group)
            }
        }

        // Přidání vybraných účastníků do cílové skupiny
        val targetGroup = allGroups[groupId]
        participantsToAdd.forEach { participant ->
            if (!targetGroup.participants.contains(participant)) {
                targetGroup.participants.toMutableList().add(participant)
            }
        }
        groups.add(targetGroup)

        // Aktualizace dat view modelem
        _participants.update { participantsState ->
            participantsState.copy(
                groupList = groups
            )
        }
    }


    private fun createGroupList(responseBody: String): List<Group> {
        return try {
            Json.decodeFromString(responseBody)
        } catch (e: SerializationException) {
            e.printStackTrace()
            emptyList()
        }
    }
}