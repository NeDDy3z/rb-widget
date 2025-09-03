import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vanek.rbwidget.ui.view.SetupPartOneView
import com.vanek.rbwidget.ui.view.SetupPartTwoView

sealed class Screen(val route: String) {
    object Setup : Screen("setup")
    object Home : Screen("home")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Setup.route) {
        composable(Screen.Setup.route) {
            SetupView(onSetupComplete = {
                navController.navigate(Screen.Home.route) {
                    // Pop Setup from back stack so user can't go back to it
                    popUpTo(Screen.Setup.route) {
                        inclusive = true
                    }
                }
            })
        }
        composable(Screen.Home.route) {
            HomeView()
        }
        composable(Screen.Settings.route) {
            // SettingsView()
        }
    }
}

@Composable
fun SetupView(onSetupComplete: () -> Unit) {
    var showPartTwo by remember { mutableStateOf(false) }

    if (!showPartTwo) {
        SetupPartOneView(
            onContinueClicked = {
                showPartTwo = true
            }
        )
    } else {
        SetupPartTwoView(
            onContinueClicked = {
                onSetupComplete()
            }
        )
    }
}

@Composable
fun HomeView() {
    // ... Your HomeView content
    Text("Welcome Home!")
}
