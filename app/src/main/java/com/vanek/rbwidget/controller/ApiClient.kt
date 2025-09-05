import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class ApiClient(private val baseUrl: String) {

    private val client = OkHttpClient()

    // Non-suspending GET
    fun get(uri: String, parameters: Map<String, String>? = null, headers: Map<String, String>? = null): Response {
        val urlBuilder = (baseUrl + uri).toHttpUrlOrNull()?.newBuilder()
        parameters?.forEach { (key, value) -> urlBuilder?.addQueryParameter(key, value) }
        val url = urlBuilder?.build().toString()

        val requestBuilder = Request.Builder().url(url)
        headers?.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        return client.newCall(requestBuilder.build()).execute()
    }

    // Non-suspending POST
    fun post(uri: String, body: String, contentType: String = "application/json", headers: Map<String, String>? = null): Response {
        val mediaType = contentType.toMediaTypeOrNull()
        val requestBody = body.toRequestBody(mediaType)

        val requestBuilder = Request.Builder()
            .url(baseUrl + uri)
            .post(requestBody)

        headers?.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

        return client.newCall(requestBuilder.build()).execute()
    }
}