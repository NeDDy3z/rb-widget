package com.vanek.rbwidget.model.network.response

data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val expiresAt: Long
)