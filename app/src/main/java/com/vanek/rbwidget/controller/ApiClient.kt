package com.vanek.rbwidget.controller

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class ApiClient constructor(
    private val client: OkHttpClient = OkHttpClient(),
    private val baseUrl: String
) {
    /**
     * Generic HTTP GET request.
     * @param uri The endpoint URL.
     * @param headers Optional headers.
     * @return Response body as String, or null on failure.
     */
    fun get(
        uri: String,
        headers: Map<String, String>? = null
    ): Response {
        val requestBuilder = Request.Builder()
        val headers = headers?.toMutableMap() ?: mutableMapOf()

        requestBuilder.url(baseUrl + uri)
        requestBuilder.get()
        headers.forEach { (key, value) ->
            requestBuilder.addHeader(key, value)
        }

        return client.newCall(
            requestBuilder.build()
        ).execute()
    }

    /**
     * Generic HTTP POST request.
     * @param url The endpoint URL.
     * @param body The request body as String.
     * @param contentType The content type of the request body.
     * @param headers Optional headers.
     * @return Response body as String, or null on failure.
     */
    fun post(
        uri: String,
        body: String,
        contentType: String = "application/json",
        headers: Map<String, String>? = null,
    ): Response? {
        val requestBody = body.toRequestBody("$contentType; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(baseUrl + uri)
            .post(requestBody)
            .build()

        return client.newCall(request).execute()
    }
}