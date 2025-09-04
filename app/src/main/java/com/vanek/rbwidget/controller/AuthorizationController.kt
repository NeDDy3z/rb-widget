package com.vanek.rbwidget.controller

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.vanek.rbwidget.model.Authorization

class AuthorizationController private constructor(context: Context) {

    companion object {
        @Volatile
        private var instance: AuthorizationController? = null

        fun init(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = AuthorizationController(context.applicationContext)
                    }
                }
            }
        }

        fun get(): AuthorizationController {
            return instance ?: throw IllegalStateException("AuthorizationController is not initialized. Call AuthorizationController.init(context) first.")
        }
    }

    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val apiClient = ApiClient(baseUrl = "https://api.rbinternational.com")


    fun getAuthorization(): Authorization {
        val authorization = Authorization.fromPrefs(prefs)

        return if (!authorization.isAuthorized()) {
            val (clientId, clientSecret) = getCredentials()
            obtainNewToken(
                clientId = clientId,
                clientSecret = clientSecret,
            )
        } else {
            authorization
        }
    }

    fun saveCredentials(clientId: String, clientSecret: String) {
        prefs.edit {
            putString("client_id", clientId)
            putString("client_secret", clientSecret)
        }
    }

    private fun obtainNewToken(
        clientId: String,
        clientSecret: String
    ): Authorization {
        val jsonBody = Gson().toJson(
            mapOf(
                "clientid" to clientId,
                "clientsecret" to clientSecret
            )
        )

        val response = apiClient.post(
            uri = "/aisp/oauth2/token",
            body = jsonBody
        )

        if (response === null || response.code != 200) {
            //throw Exception("Failed to obtain token: ${response?.code}")
            throw Exception(response?.body.toString())
        } else {
            val responseBody = response.body?.string() ?: throw Exception("Empty response body")

            val responseMap: Map<String, Any> = Gson().fromJson(responseBody, Map::class.java) as Map<String, Any>
            val token = responseMap["access_token"] as? String?
            val refreshToken = responseMap["refresh_token"] as? String?
            val expiresIn = (responseMap["expires_in"] as? Int?)

            if (token == null || refreshToken == null || expiresIn == null) {
                throw Exception("Invalid response: $responseBody")
            }

            prefs.edit {
                putString("token", token)
                putString("refreshToken", refreshToken)
                putString("expiresAt", expiresIn.toString())
            }
        }

        return Authorization.fromPrefs(prefs)
    }

    private fun getCredentials(): Pair<String, String> {
        val clientId = prefs.getString("client_id", null) ?: throw Exception("Client ID not found")
        val clientSecret = prefs.getString("client_secret", null) ?: throw Exception("Client Secret not found")

        return Pair(clientId, clientSecret)
    }
}