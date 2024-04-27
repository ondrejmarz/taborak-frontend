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
class LoadParticipantsViewModel(private val tourId: String) : ViewModel() {

    fun uploadParticipantXlsx(participantData: ByteArray) {
        viewModelScope.launch {
            ApiClient.uploadParticipantXlsx(tourId, participantData, { }, {
                println(it)
            })
        }
    }
}