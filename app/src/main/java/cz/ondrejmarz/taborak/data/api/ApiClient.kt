package cz.ondrejmarz.taborak.data.api

import android.os.Build
import androidx.annotation.RequiresApi
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
            launch(Dispatchers.IO) {
                try {
                    val request = Request.Builder()
                        .url(urlPath + "/tours")
                        .build()

                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseBody: String = response.body?.string() ?: ""
                    onSuccess(responseBody)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun createTour(newTour: Tour, onSuccess: () -> Unit) {

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
        })
    }
}