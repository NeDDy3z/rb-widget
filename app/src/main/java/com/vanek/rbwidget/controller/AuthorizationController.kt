package com.vanek.rbwidget.controller

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.vanek.rbwidget.model.Authorization
import com.vanek.rbwidget.model.network.RBClient

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
            return instance
                ?: throw IllegalStateException("AuthorizationController is not initialized. Call AuthorizationController.init(context) first.")
        }
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val rbClient = RBClient()
    private var authorization: Authorization? = Authorization.fromPrefs(prefs)

    fun setAuthorization(auth: Authorization) {
        authorization = auth
    }

    fun getAuthorization(): Authorization? {
        return authorization
    }


    fun authorize(onComplete: (result: Authorization?, error: String?) -> Unit) {
        val auth = getAuthorization()

        if (auth !== null && auth.isAuthorized()) {
            onComplete(auth, null)
        } else if (getClientCredentials() !== null) {
            val credentials = getClientCredentials()!!
            obtainNewToken(
                credentials.first,
                credentials.second
            ) { success, error ->
                if (success) {
                    val newAuth = Authorization(
                        token = prefs.getString("token", null) ?: "",
                        refreshToken = prefs.getString("refreshToken", null) ?: "",
                        expiresAt = prefs.getLong("expiresAt", 0).toInt()
                    )
                    onComplete(newAuth, null)
                } else {
                    onComplete(null, error)
                }
            }
        } else {
            onComplete(null, "Could not authorize - missing client credentials")
        }
    }

    private fun obtainNewToken(
        clientId: String,
        clientSecret: String,
        onComplete: (success: Boolean, error: String?) -> Unit
    ) {
        rbClient.authorize(
            clientId = clientId,
            clientSecret = clientSecret
        ) { result ->
            if (result.isSuccess && result.data != null) {
                val authResponse = result.data

                val token = authResponse.token
                val refreshToken = authResponse.refreshToken
                val expiresAt = System.currentTimeMillis() / 1000 + authResponse.expiresAt

                prefs.edit {
                    putString("token", token)
                    putString("refreshToken", refreshToken)
                    putLong("expiresAt", System.currentTimeMillis() / 1000 + expiresAt)
                }

                setAuthorization(
                    auth = Authorization(
                        token = token,
                        refreshToken = refreshToken,
                        expiresAt = (System.currentTimeMillis() / 1000 + expiresAt).toInt()
                    )
                )
                onComplete(true, null)
            } else {
                onComplete(false, "Authentication failed - ${result.statusCode}")
            }
        }
    }

    private fun getClientCredentials(): Pair<String, String>? {
        val clientId = prefs.getString("clientId", null)
        val clientSecret = prefs.getString("clientSecret", null)

        if (clientId != null && clientSecret != null) {
            return Pair(clientId, clientSecret)
        }

        return null
    }

    fun saveClientCredentials(clientId: String, clientSecret: String) {
        prefs.edit {
            putString("clientId", clientId)
            putString("clientSecret", clientSecret)
        }
    }
}
