package com.vanek.rbwidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vanek.rbwidget.controller.AuthorizationController
import com.vanek.rbwidget.controller.Navigation
import com.vanek.rbwidget.ui.theme.RBWidgetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init singletons
        AuthorizationController.init(this)

        enableEdgeToEdge()
        setContent {
            RBWidgetTheme {
                Navigation()
            }
        }
    }
}