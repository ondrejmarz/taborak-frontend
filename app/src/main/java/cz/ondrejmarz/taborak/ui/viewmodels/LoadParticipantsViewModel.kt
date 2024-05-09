package cz.ondrejmarz.taborak.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import cz.ondrejmarz.taborak.data.api.ApiClient
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.O)
class LoadParticipantsViewModel(private val tourId: String) : ViewModel() {

    fun uploadParticipantXlsx(participantData: ByteArray) {
        runBlocking {
            ApiClient.uploadParticipantXlsx(tourId, participantData, { }, {
                println(it)
            })
        }
    }
}