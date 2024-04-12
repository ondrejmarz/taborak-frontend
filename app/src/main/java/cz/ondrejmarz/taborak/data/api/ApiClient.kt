package cz.ondrejmarz.taborak.data.api

import android.os.Build
import androidx.annotation.RequiresApi
import cz.ondrejmarz.taborak.auth.AuthTokenManager
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.viewmodel.TourViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

object ApiClient {

    private val client = OkHttpClient()
    private val urlPath = "http://10.0.2.2:8080"

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchTours(onSuccess: (String) -> Unit) {
        runBlocking {
            val url = "$urlPath/tours"
            RequestManagerOkHttp.makeGetRequest(url, onSuccess, { error -> println(error) })
        }
    }

    fun createTour(newTour: Tour, onSuccess: (String) -> Unit, onFailure: (IOException) -> Unit) {
        runBlocking {
            val url = "$urlPath/tours"
            val jsonBody = Json.encodeToString(newTour)
            RequestManagerOkHttp.makePostRequest(url, jsonBody, onSuccess, onFailure)
        }
    }

    fun saveUser(user: UserData, onSuccess: (String) -> Unit) {
        runBlocking {
            val url = "$urlPath/users"
            val jsonBody = Json.encodeToString(user)
            RequestManagerOkHttp.makePostRequest(url, jsonBody, onSuccess, { error -> println(error) })
        }
    }

    fun getRole(tourId: String, userId: String, onSuccess: (String) -> Unit) {
        runBlocking {
            val url = "$urlPath/tours/$tourId/members/$userId/role"
            RequestManagerOkHttp.makeGetRequest(url, onSuccess, { error -> println(error) })
        }
    }

    fun deleteTour(tourId: String, onSuccess: () -> Unit, onFailure: (IOException) -> Unit) {
        runBlocking {
            val url = "$urlPath/tours/$tourId"
            RequestManagerOkHttp.makeDeleteRequest(url, onSuccess, onFailure)
        }
    }

    fun fetchAllMembers(tourId: String, onSuccess: (String) -> Unit) {
        runBlocking {
            val url = "$urlPath/tours/$tourId/members"
            RequestManagerOkHttp.makeGetRequest(url, onSuccess, { error -> println(error) })
        }
    }

    fun fetchAllApplications(tourId: String, onSuccess: (String) -> Unit) {
        runBlocking {
            val url = "$urlPath/tours/$tourId/applications"
            RequestManagerOkHttp.makeGetRequest(url, onSuccess, { error -> println(error) })
        }
    }

    fun addTourMember(tourId: String, userId: String, onSuccess: () -> Unit) {
        runBlocking {
            val url = "$urlPath/tours/$tourId/members/$userId"
            RequestManagerOkHttp.makePutRequest(url, "", onSuccess, { error -> println(error) })
        }
    }

    fun deleteTourMember(tourId: String, userId: String, onSuccess: () -> Unit) {
        runBlocking {
            val url = "$urlPath/tours/$tourId/members/$userId"
            RequestManagerOkHttp.makeDeleteRequest(url, onSuccess, { error -> println(error) })
        }
    }

    fun addTourApplication(tourId: String, userId: String, onSuccess: () -> Unit) {
        runBlocking {
            val url = "$urlPath/tours/$tourId/applications/$userId"
            RequestManagerOkHttp.makePutRequest(url, "", onSuccess, { error -> println(error) })
        }
    }

    fun deleteTourApplication(tourId: String, userId: String, onSuccess: () -> Unit) {
        runBlocking {
            val url = "$urlPath/tours/$tourId/applications/$userId"
            RequestManagerOkHttp.makeDeleteRequest(url, onSuccess, { error -> println(error) })
        }
    }

    fun setTourRole(tourId: String, userId: String, role: String, onSuccess: () -> Unit) {
        runBlocking {
            val url = "$urlPath/tours/$tourId/members/$userId/role"
            RequestManagerOkHttp.makePutRequest(url, role, onSuccess, { error -> println(error) })
        }
    }
}