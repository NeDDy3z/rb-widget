package com.vanek.rbwidget.model

data class Authorization(
    val token: String?,
    val refreshToken: String?,
    val expiresAt: Int?,
) {
    fun isAuthorized(): Boolean {
        if (token == null || refreshToken == null || expiresAt == null) {
            return false
        }

        return expiresAt > System.currentTimeMillis()
    }

    fun isNull(): Boolean {
        return token == null && refreshToken == null && expiresAt == null
    }

    companion object {
        fun fromPrefs(prefs: android.content.SharedPreferences): Authorization? {
            val token = prefs.getString("token", null)
            val refreshToken = prefs.getString("refreshToken", null)
            val expiresAt = prefs.getString("expiresAt", null)?.toIntOrNull()

            if (token == null || refreshToken == null || expiresAt == null) {
                return null
            } else {
                return Authorization(token, refreshToken, expiresAt)
            }
        }
    }
}