package com.vanek.rbwidget.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun SetupPartOneView(modifier: Modifier = Modifier, onContinueClicked: () -> Unit) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Use background color for the Surface
    ) {
        // Top padding
        Column(modifier = modifier.fillMaxHeight(0.33f)){}

        // Contents
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.33f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Setup your RB API Connection",
                style = MaterialTheme.typography.titleLarge,
                modifier = modifier.padding(16.dp),
            )
            Button(
                onClick = { onContinueClicked },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // Bottom padding
        Column(modifier = modifier.fillMaxHeight(0.33f)){}
    }
}

@Composable
fun SetupPartTwoView(modifier: Modifier = Modifier, onContinueClicked: () -> Unit) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter your Client ID and Client Secret obtained at the RB Developer Portal",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )


            var clientId by remember { mutableStateOf("") }
            var clientSecret by remember { mutableStateOf("") }

            TextField(
                value = clientId,
                onValueChange = { clientId = it },
                label = { Text("Client ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            TextField(
                value = clientSecret,
                onValueChange = { clientSecret = it },
                label = { Text("Client Secret") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Button(
                onClick = { onContinueClicked },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Clickable text link
            val annotatedText = buildAnnotatedString {
                append("Don't know where to get the credentials? Click here.")
                pushStringAnnotation(tag = "LINK", annotation = "https://developer.rb.com")
                pop()
                addStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    ),
                    start = 10,
                    end = length
                )
            }

            ClickableText(
                text = annotatedText,
                modifier = Modifier.padding(top = 16.dp),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(tag = "LINK", start = offset, end = offset)
                        .firstOrNull()?.let { annotation ->
                            // TODO: Handle link click, e.g., open a browser
                            println("Clicked on link: ${annotation.item}")
                        }
                }
            )
        }
    }
}
