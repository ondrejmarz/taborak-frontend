package cz.ondrejmarz.taborak.data.api

import cz.ondrejmarz.taborak.auth.AuthTokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonNull.content
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * Class for sending HTTP requests using OkHttp3.
 */
object RequestManagerOkHttp {

    private val client = OkHttpClient()

    /**
     * Function for sending a GET request.
     * @param url The URL address for the request.
     * @param onSuccess Callback function invoked upon successful execution of the request with the response as parameter.
     * @param onFailure Callback function invoked in case of error during the request execution with the exception as parameter.
     */
    suspend fun makeGetRequest(url: String, onSuccess: (String) -> Unit, onFailure: (IOException) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "${AuthTokenManager.getAuthToken()}")
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseBody: String = response.body?.string() ?: ""
                onSuccess(responseBody)

            } catch (e: IOException) {
                onFailure(e)
            }
        }
    }

    /**
     * Function for sending a POST request.
     * @param url The URL address for the request.
     * @param body The body of the request as a string.
     * @param onSuccess Callback function invoked upon successful execution of the request.
     * @param onFailure Callback function invoked in case of error during the request execution with the exception as parameter.
     */
    suspend fun makePostRequest(url: String, body: String, onSuccess: (String) -> Unit, onFailure: (IOException) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val requestBody = body.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "${AuthTokenManager.getAuthToken()}")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseBody: String = response.body?.string() ?: ""
                onSuccess(responseBody)

            } catch (e: IOException) {
                onFailure(e)
            }
        }
    }

    suspend fun makePostRequest(url: String, byteArray: ByteArray, onSuccess: (String) -> Unit, onFailure: (IOException) -> Unit) {
        withContext(Dispatchers.IO) {
            try {

                val requestBody = byteArray.toRequestBody(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull(),
                    0,
                    byteArray.size
                )

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "${AuthTokenManager.getAuthToken()}")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseBody: String = response.body?.string() ?: ""
                onSuccess(responseBody)

            } catch (e: IOException) {
                onFailure(e)
            }
        }
    }


    /**
     * Function for sending a PUT request.
     * @param url The URL address for the request.
     * @param body The body of the request as a string.
     * @param onSuccess Callback function invoked upon successful execution of the request.
     * @param onFailure Callback function invoked in case of error during the request execution with the exception as parameter.
     */
    suspend fun makePutRequest(url: String, body: String, onSuccess: () -> Unit, onFailure: (IOException) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val requestBody = body.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "${AuthTokenManager.getAuthToken()}")
                    .put(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                onSuccess()

            } catch (e: IOException) {
                onFailure(e)
            }
        }
    }

    /**
     * Function for sending a DELETE request.
     * @param url The URL address for the request.
     * @param onSuccess Callback function invoked upon successful execution of the request.
     * @param onFailure Callback function invoked in case of error during the request execution with the exception as parameter.
     */
    suspend fun makeDeleteRequest(url: String, onSuccess: () -> Unit, onFailure: (IOException) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "${AuthTokenManager.getAuthToken()}")
                    .delete()
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                onSuccess()

            } catch (e: IOException) {
                onFailure(e)
            }
        }
    }
}