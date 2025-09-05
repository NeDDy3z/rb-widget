package com.vanek.rbwidget.model.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.vanek.rbwidget.model.network.response.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Basic async API client for GET and POST requests
 */
class ApiClient {
    val gson = Gson()

    // Make client public to avoid inline function access issues
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Performs an async GET request
     * @param url The endpoint URL
     * @param headers Optional headers map
     * @return ApiResponse with the result
     */
    suspend inline fun <reified T> get(
        url: String,
        headers: Map<String, String> = emptyMap()
    ): ApiResponse<T> = withContext(Dispatchers.IO) {
        try {
            val requestBuilder = Request.Builder().url(url)

            // Add headers
            headers.forEach { (key, value) ->
                requestBuilder.addHeader(key, value)
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()

            handleResponse<T>(response)
        } catch (e: IOException) {
            ApiResponse.Companion.error("Network error: ${e.message}")
        } catch (e: Exception) {
            ApiResponse.Companion.error("Unexpected error: ${e.message}")
        }
    }

    /**
     * Performs an async POST request
     * @param url The endpoint URL
     * @param body The request body object (will be serialized to JSON)
     * @param headers Optional headers map
     * @return ApiResponse with the result
     */
    suspend inline fun <reified T> post(
        url: String,
        body: Any? = null,
        headers: Map<String, String> = emptyMap()
    ): ApiResponse<T> = withContext(Dispatchers.IO) {
        try {
            val requestBuilder = Request.Builder().url(url)

            // Set default content type for POST
            val defaultHeaders = mutableMapOf("Content-Type" to "application/json")
            defaultHeaders.putAll(headers)

            // Add headers
            defaultHeaders.forEach { (key, value) ->
                requestBuilder.addHeader(key, value)
            }

            // Create request body
            val requestBody = if (body != null) {
                val json = gson.toJson(body)
                json.toRequestBody("application/json".toMediaType())
            } else {
                "".toRequestBody("application/json".toMediaType())
            }

            val request = requestBuilder.post(requestBody).build()
            val response = client.newCall(request).execute()

            handleResponse<T>(response)
        } catch (e: IOException) {
            ApiResponse.Companion.error("Network error: ${e.message}")
        } catch (e: Exception) {
            ApiResponse.Companion.error("Unexpected error: ${e.message}")
        }
    }

    /**
     * Handles the HTTP response and converts it to ApiResponse
     */
    inline fun <reified T> handleResponse(response: Response): ApiResponse<T> {
        val statusCode = response.code
        val responseBody = response.body?.string()

        return if (response.isSuccessful) {
            try {
                if (responseBody.isNullOrEmpty()) {
                    // Handle empty response for Unit type
                    if (T::class == Unit::class) {
                        @Suppress("UNCHECKED_CAST")
                        ApiResponse.Companion.success(Unit as T, statusCode)
                    } else {
                        ApiResponse.Companion.error("Empty response body", statusCode)
                    }
                } else {
                    // Try to parse JSON response
                    val data = gson.fromJson(responseBody, T::class.java)
                    ApiResponse.Companion.success(data, statusCode)
                }
            } catch (e: JsonSyntaxException) {
                ApiResponse.Companion.error("Invalid JSON response: ${e.message}", statusCode)
            }
        } else {
            val errorMessage = responseBody ?: "HTTP $statusCode"
            ApiResponse.Companion.error(errorMessage, statusCode)
        }
    }

    /**
     * Performs an async GET request returning raw string response
     */
    suspend fun getRaw(
        url: String,
        headers: Map<String, String> = emptyMap()
    ): ApiResponse<String> = withContext(Dispatchers.IO) {
        try {
            val requestBuilder = Request.Builder().url(url)

            headers.forEach { (key, value) ->
                requestBuilder.addHeader(key, value)
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()

            val statusCode = response.code
            val responseBody = response.body?.string()

            if (response.isSuccessful) {
                ApiResponse.Companion.success(responseBody ?: "", statusCode)
            } else {
                ApiResponse.Companion.error(responseBody ?: "HTTP $statusCode", statusCode)
            }
        } catch (e: IOException) {
            ApiResponse.Companion.error("Network error: ${e.message}")
        } catch (e: Exception) {
            ApiResponse.Companion.error("Unexpected error: ${e.message}")
        }
    }

    /**
     * Performs an async POST request returning raw string response
     */
    suspend fun postRaw(
        url: String,
        body: String = "",
        headers: Map<String, String> = emptyMap()
    ): ApiResponse<String> = withContext(Dispatchers.IO) {
        try {
            val requestBuilder = Request.Builder().url(url)

            val defaultHeaders = mutableMapOf("Content-Type" to "application/json")
            defaultHeaders.putAll(headers)

            defaultHeaders.forEach { (key, value) ->
                requestBuilder.addHeader(key, value)
            }

            val requestBody = body.toRequestBody("application/json".toMediaType())
            val request = requestBuilder.post(requestBody).build()
            val response = client.newCall(request).execute()

            val statusCode = response.code
            val responseBody = response.body?.string()

            if (response.isSuccessful) {
                ApiResponse.Companion.success(responseBody ?: "", statusCode)
            } else {
                ApiResponse.Companion.error(responseBody ?: "HTTP $statusCode", statusCode)
            }
        } catch (e: IOException) {
            ApiResponse.Companion.error("Network error: ${e.message}")
        } catch (e: Exception) {
            ApiResponse.Companion.error("Unexpected error: ${e.message}")
        }
    }
}