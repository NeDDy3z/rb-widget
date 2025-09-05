package com.vanek.rbwidget.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vanek.rbwidget.controller.AuthorizationController
import com.vanek.rbwidget.controller.NavigationState
import kotlinx.coroutines.withContext


@Composable
fun SetupView(
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    onContinue: (clientId: String, clientSecret: String) -> Unit,
) {
    var clientId by remember { mutableStateOf("") }
    var clientSecret by remember { mutableStateOf("") }
    var localErrorMessage by remember { mutableStateOf(errorMessage) }
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "Enter your API credentials",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "How to get them?",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable { uriHandler.openUri("https://api.rbinternational.com/en/pages/getting-started/how-to-use-api-marketplace") }
            )

            OutlinedTextField(
                value = clientId,
                onValueChange = { clientId = it },
                label = { Text("Client ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = clientSecret,
                onValueChange = { clientSecret = it },
                label = { Text("Client Secret") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = {
                    if (clientId.isNotBlank() && clientSecret.isNotBlank()) {
                        onContinue(clientId, clientSecret)
                    } else {
                        localErrorMessage = "Please fill in both fields."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
                    .height(48.dp)
                ,
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Text(
                    text = localErrorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetupViewPreview() {
    SetupView(
        errorMessage = "Sample error message",
        onContinue = { _, _ -> }
    )
}

@Composable
fun SetupFlowController(navController: NavController) {
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
                navController.navigate(NavigationState.SETUP_PART_ONE + if (error != null) "?error=${error}" else "") {
                    popUpTo(NavigationState.STARTUP) { inclusive = true }
                }
            }
        }
    }
}
