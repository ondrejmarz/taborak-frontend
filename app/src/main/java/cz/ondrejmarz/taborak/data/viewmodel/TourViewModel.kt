package cz.ondrejmarz.taborak.data.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.lifecycle.ViewModel
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.models.Tour
import kotlinx.serialization.SerializationException

//import cz.ondrejmarz.taborak.data.models.tourList

class TourViewModel : ViewModel() {

    private val _tours = MutableLiveData<List<Tour>>()
    val tours: LiveData<List<Tour>> = _tours

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchTours() {
        ApiClient.fetchTours({ responseBody: String ->
            val tourList = createTourList(responseBody)
            _tours.postValue(tourList)
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNewTour(newTour: Tour) {
        ApiClient.createTour(newTour, { fetchTours() })
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