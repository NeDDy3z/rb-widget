package com.vanek.rbwidget.controller

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.vanek.rbwidget.ui.view.*

object NavigationState {
    const val HOME = "main"
    const val SETTINGS = "settings"
    const val LOADING = "loading"
    const val SETUP_PART_ONE = "setup_part_one"
    const val SETUP_PART_TWO = "setup_part_two"
}

@Composable
fun Navigation(startDestination: String = NavigationState.HOME) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = startDestination) {
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
                    AuthorizationController.get().saveCredentials(
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