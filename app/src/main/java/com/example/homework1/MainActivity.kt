package com.example.homework1

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.homework1.ui.theme.Homework1Theme

fun generatePassword(): String {
    val letters =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val digits = "0123456789"
    val allCharacters = letters + digits

    val characters = mutableListOf<Char>()

    characters.add(letters.random())
    characters.add(digits.random())

    repeat(8) {
        characters.add(allCharacters.random())
    }

    return characters.shuffled().joinToString("")
}

object PasswordStore {
    val generatedPassword: String = generatePassword()
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val generatedPassword = PasswordStore.generatedPassword

        setContent {
            Homework1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthorizationApp(
                        generatedPassword = generatedPassword
                    )
                }
            }
        }
    }
}

@Composable
fun AuthorizationApp(generatedPassword: String) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var submittedEmail by rememberSaveable { mutableStateOf("") }
    var submittedPassword by rememberSaveable { mutableStateOf("") }

    var showSecondScreen by rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler(enabled = showSecondScreen) {
        showSecondScreen = false
    }

    if (showSecondScreen) {
        ResultScreen(
            email = submittedEmail,
            enteredPassword = submittedPassword,
            generatedPassword = generatedPassword,
            onBack = {
                showSecondScreen = false
            }
        )
    } else {
        LoginScreen(
            email = email,
            password = password,
            onEmailChange = { email = it },
            onPasswordChange = { password = it },
            onLogin = { validEmail, validPassword ->
                submittedEmail = validEmail
                submittedPassword = validPassword
                showSecondScreen = true
            }
        )
    }
}

@Composable
fun LoginScreen(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: (String, String) -> Unit
) {
    var validationRequested by rememberSaveable {
        mutableStateOf(false)
    }

    val emailError: Int? =
        if (!validationRequested) {
            null
        } else if (email.isBlank()) {
            R.string.email_required
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            R.string.email_invalid
        } else {
            null
        }

    val passwordError: Int? =
        if (!validationRequested) {
            null
        } else if (password.isBlank()) {
            R.string.password_required
        } else if (!isPasswordValid(password)) {
            R.string.password_invalid
        } else {
            null
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(
            16.dp,
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = {
                Text(stringResource(R.string.email_label))
            },
            singleLine = true,
            isError = emailError != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (emailError != null) {
            Text(
                text = stringResource(emailError),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = {
                Text(stringResource(R.string.password_label))
            },
            singleLine = true,
            isError = passwordError != null,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (passwordError != null) {
            Text(
                text = stringResource(passwordError),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                validationRequested = true

                val validEmail =
                    email.isNotBlank() &&
                            Patterns.EMAIL_ADDRESS
                                .matcher(email.trim())
                                .matches()

                if (validEmail && isPasswordValid(password)) {
                    onLogin(email.trim(), password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.login_button))
        }
    }
}

fun isPasswordValid(password: String): Boolean {
    val hasLetter = password.any {
        it in 'a'..'z' || it in 'A'..'Z'
    }

    val hasDigit = password.any {
        it in '0'..'9'
    }

    return password.length >= 8 && hasLetter && hasDigit
}

@Composable
fun ResultScreen(
    email: String,
    enteredPassword: String,
    generatedPassword: String,
    onBack: () -> Unit
) {
    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    val passwordsMatch = enteredPassword == generatedPassword

    val imageUrl = if (passwordsMatch) {
        "https://http.cat/200.jpg"
    } else {
        "https://http.cat/401.jpg"
    }

    val resultText = stringResource(
        if (passwordsMatch) {
            R.string.password_match
        } else {
            R.string.password_mismatch
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = email)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = resultText,
                style = MaterialTheme.typography.titleMedium
            )
        }

        AsyncImage(
            model = imageUrl,
            contentDescription = resultText,
            modifier = Modifier
                .align(Alignment.Center)
                .size(220.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = generatedPassword,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = {
                        Text(
                            stringResource(
                                R.string.generated_password_label
                            )
                        )
                    },
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation(mask = '*')
                    },
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {
                    Text(
                        stringResource(
                            if (passwordVisible) {
                                R.string.hide_password
                            } else {
                                R.string.show_password
                            }
                        )
                    )
                }
            }

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.back_button))
            }
        }
    }
}