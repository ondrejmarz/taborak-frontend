package cz.ondrejmarz.taborak.data.api

import android.os.Build
import androidx.annotation.RequiresApi
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.viewmodel.TourViewModel
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
    private val urlPath = "example.com"
    var responseBodyOld = """
                                    [
                                      {
                                        "tourId": 1,
                                        "name": "Tour 1",
                                        "topic": "Topic 1",
                                        "description": "Description 1",
                                        "startDate": "2024-03-23T08:00:00",
                                        "endDate": "2024-03-24T18:00:00"
                                      },
                                      {
                                        "tourId": 2,
                                        "name": "Tour 2",
                                        "topic": "Topic 2",
                                        "description": "Description 2",
                                        "startDate": "2024-03-25T10:00:00",
                                        "endDate": "2024-03-26T20:00:00"
                                      }
                                    ]
                                   """
    val responseBodyNew = """
                                    [
                                      {
                                        "tourId": 1,
                                        "name": "Tour 1",
                                        "topic": "Topic 1",
                                        "description": "Description 1",
                                        "startDate": "2024-03-23T08:00:00",
                                        "endDate": "2024-03-24T18:00:00"
                                      },
                                      {
                                        "tourId": 2,
                                        "name": "Tour 2",
                                        "topic": "Topic 2",
                                        "description": "Description 2",
                                        "startDate": "2024-03-25T10:00:00",
                                        "endDate": "2024-03-26T20:00:00"
                                      },
                                      {
                                        "tourId": 3,
                                        "name": "Tour 3",
                                        "topic": "Topic 3",
                                        "description": "Description 3",
                                        "startDate": "2024-03-27T12:00:00",
                                        "endDate": "2024-03-28T22:00:00"
                                      }
                                    ]
                                   """

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchTours(onSuccess: (String) -> Unit) {

        onSuccess(responseBodyOld)

        /*
        val request = Request.Builder()
            .url(urlPath + "/tours")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body.toString()
                viewModel.fetchedTours(responseBody)
            }
        })*/
    }

    fun createTour(newTour: Tour, onSuccess: () -> Unit) {

        responseBodyOld = responseBodyNew
        onSuccess()

        /*
        val jsonBody = Json.encodeToString(newTour)

        val request = Request.Builder()
            .url(urlPath + "/tours")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    println("Request failed with code ${response.code}")
                }
            }
        })*/
    }
}