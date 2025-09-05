package com.vanek.rbwidget.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.vanek.rbwidget.ui.view.*
import java.lang.Exception

object NavigationState {
    const val STARTUP = "startup"
    const val HOME = "main"
    const val SETTINGS = "settings"
    const val SETUP_PART_ONE = "setup_part_one"
    const val SETUP_PART_TWO = "setup_part_two"
}

@Composable
fun Navigation(startDestination: String = NavigationState.STARTUP) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = startDestination) {
        // Startup
        composable(NavigationState.STARTUP) {
            StartupFlowController(navController)
        }

        // Home
        composable(NavigationState.HOME) {
            HomeView(
                onSettingsClicked = { navController.navigate(NavigationState.SETTINGS) }
            )
        }

        // Settings
        composable(NavigationState.SETTINGS) {
            SettingsView(
                onBack = { navController.popBackStack() }
            )
        }

        // Setup
        composable(
            route = NavigationState.SETUP_PART_ONE + "?error={error}",
            arguments = listOf(
                navArgument("error") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val error = backStackEntry.arguments?.getString("error")
            SetupView(
                errorMessage = error,
                onContinue = { clientId, clientSecret ->
                    AuthorizationController.get().saveClientCredentials(
                        clientId = clientId,
                        clientSecret = clientSecret
                    )

                    navController.navigate(NavigationState.SETUP_PART_TWO)
                },
            )
        }
        composable(NavigationState.SETUP_PART_TWO) {
            SetupFlowController(navController)
        }
    }
}

@Composable
fun StartupFlowController(navController: NavController) {
    LoadingView()

    LaunchedEffect(Unit) {
        val authController = AuthorizationController.get()

        authController.authorize {
                result, error ->
            if (result !== null) {
                navController.navigate(NavigationState.HOME) {
                    popUpTo(NavigationState.STARTUP) { inclusive = true }
                }
            } else {
                navController.navigate(NavigationState.SETUP_PART_ONE) {
                    popUpTo(NavigationState.STARTUP) { inclusive = true }
                }
            }
        }
    }
}