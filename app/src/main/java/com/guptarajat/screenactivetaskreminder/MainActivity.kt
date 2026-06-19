package com.guptarajat.screenactivetaskreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.guptarajat.screenactivetaskreminder.auth.AuthSession
import com.guptarajat.screenactivetaskreminder.data.repository.TaskCacheSnapshot
import com.guptarajat.screenactivetaskreminder.settings.TaskReminderSettings
import com.guptarajat.screenactivetaskreminder.ui.app.TaskReminderRoot
import com.guptarajat.screenactivetaskreminder.ui.app.TaskReminderApp
import com.guptarajat.screenactivetaskreminder.ui.theme.TaskReminderTheme

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
        TaskReminderApp(
            settings = TaskReminderSettings(),
            authSession = AuthSession(),
            authStatusMessage = null,
            isAuthActionInProgress = false,
            taskCacheSnapshot = TaskCacheSnapshot(),
            onGoogleSignInClick = {},
            onGoogleSignOutClick = {},
            onDismissAuthStatus = {},
            onReminderIntervalChange = {},
            onSnoozeMinutesChange = {},
            onThemeModeChange = {},
        )
    }
}
