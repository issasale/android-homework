package com.example.homework1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.homework1.ui.theme.Homework1Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Homework1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CoroutinesScreen()
                }
            }
        }
    }
}

@Composable
fun CoroutinesScreen() {
    var countText by rememberSaveable {
        mutableStateOf("3")
    }
    val scope = rememberCoroutineScope()
    var runningJob by remember { mutableStateOf<Job?>(null) }
    var completed by remember { mutableStateOf(0) }
    var total by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var countError by remember { mutableStateOf(false) }

    val progress = if (total > 0) {
        completed.toFloat() / total
    } else {
        0f
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.coroutines_title),
            style = MaterialTheme.typography.headlineSmall
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(
                R.string.coroutines_progress,
                completed,
                total
            )
        )
        OutlinedTextField(
            value = countText,
            onValueChange = { newText ->
                countText = newText
            },

            label = {
                Text(
                    text = stringResource(R.string.coroutines_count)
                )
            },

            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (countError) {
            Text(
                text = stringResource(R.string.invalid_coroutines_count),
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            enabled = !isRunning,
            onClick = {
                val count = countText.toIntOrNull()

                if (count == null || count !in 1..100) {
                    countError = true
                } else {
                    countError = false
                    completed = 0
                    total = count
                    isRunning = true

                    runningJob = scope.launch  {
                        try {
                            repeat(count) {
                                launch {
                                    delay(3000)
                                    completed++
                                }.join()
                            }
                        } finally {
                            isRunning = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.run_sequential))
        }

        Button(
            enabled = !isRunning,
            onClick = {
                val count = countText.toIntOrNull()

                if (count == null || count !in 1..100) {
                    countError = true
                } else {
                    countError = false
                    completed = 0
                    total = count
                    isRunning = true

                    runningJob = scope.launch {
                        try {
                            coroutineScope {
                                repeat(count) {
                                    launch {
                                        delay(3000)
                                        completed++
                                    }
                                }
                            }
                        } finally {
                            isRunning = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.run_parallel))
        }

        Button(
            enabled = isRunning,
            onClick = {
                runningJob?.cancel()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.cancel_coroutines))
        }
    }
}