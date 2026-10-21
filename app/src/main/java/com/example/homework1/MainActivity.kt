package com.example.homework1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.homework1.ui.theme.Homework1Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Homework1Theme {
                val name = stringResource(R.string.student_name)
                val group = stringResource(R.string.student_group)

                val studentInfo = stringResource(
                    R.string.student_info_format,
                    name,
                    group
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = name)

                    Text(text = group)

                    Button(
                        onClick = {
                            val intent = Intent(
                                this@MainActivity,
                                SecondActivity::class.java
                            )

                            intent.putExtra("student_info", studentInfo)

                            startActivity(intent)
                        }
                    ) {
                        Text(
                            text = stringResource(
                                R.string.open_second_screen
                            )
                        )
                    }
                }
            }
        }
    }
}