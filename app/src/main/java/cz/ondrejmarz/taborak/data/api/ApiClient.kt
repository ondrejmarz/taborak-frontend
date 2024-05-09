package cz.ondrejmarz.taborak.data.api

import android.os.Build
import androidx.annotation.RequiresApi
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.data.models.DayPlan
import cz.ondrejmarz.taborak.data.models.Tour
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.io.IOException

object ApiClient {

    private const val urlPath = "https://taborak.onrender.com"

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchTours(onSuccess: (String) -> Unit) {
        val url = "$urlPath/tours"
        RequestManagerOkHttp.makeGetRequest(url, onSuccess) { error -> println(error) }
    }

    suspend fun fetchTour(tourId: String, onSuccess: (String) -> Unit) {
        val url = "$urlPath/tours/$tourId"
        RequestManagerOkHttp.makeGetRequest(url, onSuccess) { error -> println(error) }
    }

    suspend fun createTour(newTour: Tour, onSuccess: (String) -> Unit, onFailure: (IOException) -> Unit) {
        val url = "$urlPath/tours"
        val jsonBody = Json.encodeToString(newTour)
        RequestManagerOkHttp.makePostRequest(url, jsonBody, onSuccess, onFailure)
    }

    suspend fun saveUser(user: UserData, onSuccess: (String) -> Unit) {
        val url = "$urlPath/users"
        val jsonBody = Json.encodeToString(user)
        RequestManagerOkHttp.makePostRequest(
            url,
            jsonBody,
            onSuccess
        ) { error -> println(error) }
    }



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *   SETTINGS
     *
     */

    suspend fun getRole(tourId: String, userId: String, onSuccess: (String) -> Unit) {
        val url = "$urlPath/tours/$tourId/members/$userId/role"
        RequestManagerOkHttp.makeGetRequest(url, onSuccess) { error -> println(error) }
    }

    suspend fun deleteTour(tourId: String, onSuccess: () -> Unit, onFailure: (IOException) -> Unit) {
        val url = "$urlPath/tours/$tourId"
        RequestManagerOkHttp.makeDeleteRequest(url, onSuccess, onFailure)
    }

    suspend fun fetchAllMembers(tourId: String, onSuccess: (String) -> Unit) {
        val url = "$urlPath/tours/$tourId/members"
        RequestManagerOkHttp.makeGetRequest(url, onSuccess) { error -> println(error) }

    }

    suspend fun fetchAllApplications(tourId: String, onSuccess: (String) -> Unit) {
        val url = "$urlPath/tours/$tourId/applications"
        RequestManagerOkHttp.makeGetRequest(url, onSuccess) { error -> println(error) }
    }

    suspend fun addTourMember(tourId: String, userId: String, onSuccess: () -> Unit) {
        val url = "$urlPath/tours/$tourId/members/$userId"
        RequestManagerOkHttp.makePutRequest(url, "", onSuccess) { error -> println(error) }
    }

    suspend fun deleteTourMember(tourId: String, userId: String, onSuccess: () -> Unit) {
        val url = "$urlPath/tours/$tourId/members/$userId"
        RequestManagerOkHttp.makeDeleteRequest(url, onSuccess) { error -> println(error) }
    }

    suspend fun addTourApplication(tourId: String, userId: String, onSuccess: () -> Unit) {
        val url = "$urlPath/tours/$tourId/applications/$userId"
        RequestManagerOkHttp.makePutRequest(url, "", onSuccess) { error -> println(error) }
    }

    suspend fun deleteTourApplication(tourId: String, userId: String, onSuccess: () -> Unit) {
        val url = "$urlPath/tours/$tourId/applications/$userId"
        RequestManagerOkHttp.makeDeleteRequest(url, onSuccess) { error -> println(error) }
    }

    suspend fun setTourRole(tourId: String, userId: String, role: String, onSuccess: () -> Unit) {
        val url = "$urlPath/tours/$tourId/members/$userId/role"
        RequestManagerOkHttp.makePutRequest(url, role, onSuccess) { error -> println(error) }
    }



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *   CALENDAR
     *
     */

    suspend fun fetchCalendar(
        tourId: String,
        day: String,
        onSuccess: (String) -> Unit,
        onFailure: (IOException) -> Unit
    ) {
        val url = "$urlPath/tours/$tourId/calendar/$day"
        RequestManagerOkHttp.makeGetRequest(url, onSuccess, onFailure)
    }

    suspend fun createActivities(
        tourId: String,
        day: String,
        dayPlan: DayPlan,
        onSuccess: (String) -> Unit,
        onFailure: (IOException) -> Unit
    ) {
        val url = "$urlPath/tours/$tourId/calendar/$day"
        val jsonBody = Json.encodeToString(dayPlan)
        RequestManagerOkHttp.makePostRequest(url, jsonBody, onSuccess, onFailure)
    }

    suspend fun updateDayPlan(
        tourId: String,
        day: String,
        dayPlan: DayPlan?,
        onSuccess: () -> Unit,
        onFailure: (IOException) -> Unit
    ) {
        val url = "$urlPath/tours/$tourId/calendar/$day"
        val jsonBody = Json.encodeToString(dayPlan)
        RequestManagerOkHttp.makePutRequest(url, jsonBody, onSuccess, onFailure)
    }



    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *   PARTICIPANTS
     *
     */

    suspend fun fetchParticipants(
        tourId: String,
        onSuccess: (String) -> Unit,
        onFailure: (IOException) -> Unit
    ) {
        val url = "$urlPath/tours/$tourId/groups"
        RequestManagerOkHttp.makeGetRequest(url, onSuccess, onFailure)
    }

    suspend fun uploadParticipantXlsx(
        tourId: String,
        byteArray: ByteArray,
        onSuccess: (String) -> Unit,
        onFailure: (IOException) -> Unit
    ) {
        val url = "$urlPath/tours/$tourId/groups/createAllInXlsx"
        RequestManagerOkHttp.makePostRequest(url, byteArray, onSuccess, onFailure)
    }
}