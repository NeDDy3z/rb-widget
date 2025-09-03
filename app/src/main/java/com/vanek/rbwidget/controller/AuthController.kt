package com.vanek.rbwidget.controller

import android.content.Context
import com.vanek.rbwidget.model.AuthState

class AuthorizationController(private val context: Context) {

    //private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun authorize(authState: AuthState) {

    }

    fun getAuthState(): AuthState {
        val token = "Temp"
        return AuthState(token = token)
    }
}