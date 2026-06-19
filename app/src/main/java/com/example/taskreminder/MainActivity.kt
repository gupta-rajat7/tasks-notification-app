package com.example.taskreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.taskreminder.ui.app.TaskReminderRoot
import com.example.taskreminder.ui.theme.TaskReminderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TaskReminderRoot()
        }
    }
}

@Composable
fun TaskReminderHomeScreen(modifier: Modifier = Modifier) {
    TaskReminderRoot(modifier = modifier)
}

@Preview(showBackground = true)
@Composable
private fun TaskReminderHomeScreenPreview() {
    TaskReminderTheme {
        TaskReminderHomeScreen()
    }
}
