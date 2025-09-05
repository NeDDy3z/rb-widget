package com.vanek.rbwidget.model.network

import com.vanek.rbwidget.model.network.request.AuthRequest
import com.vanek.rbwidget.model.network.response.AccountBalanceResponse
import com.vanek.rbwidget.model.network.response.ApiResponse
import com.vanek.rbwidget.model.network.response.AuthResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RBClient {
    private val apiClient = ApiClient()
    private val baseUrl = "https://api.example.com/"

    /**
     * Authenticate with client credentials
     */
    fun authorize(
        clientId: String,
        clientSecret: String,
        onResult: (ApiResponse<AuthResponse>) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val authRequest = AuthRequest(clientId, clientSecret)

            val response = apiClient.post<AuthResponse>(
                url = baseUrl + "auth/token",
                body = authRequest,
                headers = mapOf(
                    "Content-Type" to "application/json",
                    "Accept" to "application/json"
                )
            )

            onResult(response)
        }
    }

    /**
     * Get account balance
     * @param token Authentication token
     * @param accountId Account identifier (optional, if not provided gets default account)
     * @param onResult Callback with the account balance response
     */
    fun getAccountBalance(
        token: String,
        accountId: String? = null,
        onResult: (ApiResponse<AccountBalanceResponse>) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val url = if (accountId != null) {
                baseUrl + "accounts/$accountId/balance"
            } else {
                baseUrl + "accounts/balance"
            }

            val response = apiClient.get<AccountBalanceResponse>(
                url = url,
                headers = mapOf(
                    "Authorization" to "Bearer $token",
                    "Accept" to "application/json"
                )
            )

            onResult(response)
        }
    }

    // Example usage:
//    val rbClient = RBClient()
//    rbClient.authenticate("your-client-id", "your-client-secret") { response ->
//        if (response.isSuccess) {
//            val authData = response.data
//            // Use authData.token, authData.refreshToken, authData.expiresAt
//        } else {
//            // Handle authentication error
//        }
//    }

}