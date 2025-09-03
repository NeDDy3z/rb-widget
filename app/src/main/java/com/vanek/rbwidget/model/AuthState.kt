package com.vanek.rbwidget.model

data class AuthState(
    val token: String? = null,
    val expiresAt: Long? = null,
) {
    fun isAuthorized(): Boolean {
        return token != null && expiresAt != null && expiresAt > System.currentTimeMillis()
    }
}