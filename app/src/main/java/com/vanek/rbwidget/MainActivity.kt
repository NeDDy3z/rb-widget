package com.vanek.rbwidget

import ApiClient
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.vanek.rbwidget.controller.AuthorizationController
import com.vanek.rbwidget.controller.Navigation
import com.vanek.rbwidget.controller.NavigationState
import com.vanek.rbwidget.ui.theme.RBWidgetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AuthorizationController.init(this)
        val authController = AuthorizationController.get()
        var isAuthorized = false

        // This works!!!
//        Thread {
//            val apiClient = ApiClient(baseUrl = "https://jsonplaceholder.typicode.com/posts")
//            val response = apiClient.get("")
//            println("GET: "+ response.body?.string())
//        }.start()

        val startDestination = when (false) {
            isAuthorized -> NavigationState.SETUP_PART_ONE
            else -> NavigationState.HOME
        }

        enableEdgeToEdge()
        setContent {
            RBWidgetTheme {
                Navigation(
                    startDestination = startDestination,
                )
            }
        }
    }
}