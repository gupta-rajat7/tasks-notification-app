package com.guptarajat.screenactivetaskreminder.ui.app

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.guptarajat.screenactivetaskreminder.R
import com.guptarajat.screenactivetaskreminder.AppSectionCopy
import com.guptarajat.screenactivetaskreminder.SETTINGS_ROUTE
import com.guptarajat.screenactivetaskreminder.TASKS_ROUTE
import com.guptarajat.screenactivetaskreminder.TODAY_ROUTE
import com.guptarajat.screenactivetaskreminder.appName
import com.guptarajat.screenactivetaskreminder.appSectionForRoute
import com.guptarajat.screenactivetaskreminder.auth.AuthSession
import com.guptarajat.screenactivetaskreminder.auth.AuthStore
import com.guptarajat.screenactivetaskreminder.auth.GoogleSignInClient
import com.guptarajat.screenactivetaskreminder.auth.GoogleSignInConfig
import com.guptarajat.screenactivetaskreminder.auth.GoogleSignInResult
import com.guptarajat.screenactivetaskreminder.auth.userMessage
import com.guptarajat.screenactivetaskreminder.data.local.CachedTask
import com.guptarajat.screenactivetaskreminder.data.local.TaskReminderDatabase
import com.guptarajat.screenactivetaskreminder.data.repository.TaskCacheRepository
import com.guptarajat.screenactivetaskreminder.data.repository.TaskCacheSnapshot
import com.guptarajat.screenactivetaskreminder.settings.MAX_REMINDER_INTERVAL_MINUTES
import com.guptarajat.screenactivetaskreminder.settings.MAX_SNOOZE_MINUTES
import com.guptarajat.screenactivetaskreminder.settings.MIN_REMINDER_INTERVAL_MINUTES
import com.guptarajat.screenactivetaskreminder.settings.MIN_SNOOZE_MINUTES
import com.guptarajat.screenactivetaskreminder.settings.SettingsStore
import com.guptarajat.screenactivetaskreminder.settings.TaskReminderSettings
import com.guptarajat.screenactivetaskreminder.settings.ThemeMode
import com.guptarajat.screenactivetaskreminder.ui.theme.TaskReminderTheme
import kotlinx.coroutines.launch

private data class AppDestination(
    val section: AppSectionCopy,
    val icon: ImageVector,
)

private val AppDestinations = listOf(
    AppDestination(appSectionForRoute(TODAY_ROUTE), Icons.Outlined.CheckCircle),
    AppDestination(appSectionForRoute(TASKS_ROUTE), Icons.AutoMirrored.Outlined.List),
    AppDestination(appSectionForRoute(SETTINGS_ROUTE), Icons.Outlined.Settings),
)

@Composable
fun TaskReminderRoot(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val settingsStore = remember(context) { SettingsStore(context) }
    val authStore = remember(context) { AuthStore(context) }
    val googleSignInClient = remember(context) {
        GoogleSignInClient(
            context = context,
            config = GoogleSignInConfig(
                webClientId = context.getString(R.string.google_web_client_id).trim(),
            ),
        )
    }
    val taskCacheRepository = remember(context) {
        TaskCacheRepository(TaskReminderDatabase.getInstance(context))
    }
    val scope = rememberCoroutineScope()
    val settings by settingsStore.settings.collectAsState(initial = TaskReminderSettings())
    val authSession by authStore.session.collectAsState(initial = AuthSession())
    val taskCacheSnapshot by taskCacheRepository.cacheSnapshot.collectAsState(
        initial = TaskCacheSnapshot(),
    )
    val systemDarkTheme = isSystemInDarkTheme()
    var authStatusMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isAuthActionInProgress by rememberSaveable { mutableStateOf(false) }

    TaskReminderTheme(
        darkTheme = when (settings.themeMode) {
            ThemeMode.SYSTEM -> systemDarkTheme
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        },
    ) {
        TaskReminderApp(
            settings = settings,
            authSession = authSession,
            authStatusMessage = authStatusMessage,
            isAuthActionInProgress = isAuthActionInProgress,
            taskCacheSnapshot = taskCacheSnapshot,
            onGoogleSignInClick = {
                val activity = context.findActivity()
                if (activity == null) {
                    authStatusMessage = "Google sign-in needs an active Android screen."
                } else {
                    scope.launch {
                        isAuthActionInProgress = true
                        when (val result = googleSignInClient.signIn(activity)) {
                            is GoogleSignInResult.Success -> {
                                authStore.saveSession(result.session)
                                authStatusMessage = "Signed in as ${result.session.displayLabel}."
                            }
                            else -> {
                                authStatusMessage = result.userMessage()
                            }
                        }
                        isAuthActionInProgress = false
                    }
                }
            },
            onGoogleSignOutClick = {
                scope.launch {
                    isAuthActionInProgress = true
                    runCatching { googleSignInClient.signOut() }
                    authStore.clearSession()
                    authStatusMessage = "Signed out."
                    isAuthActionInProgress = false
                }
            },
            onDismissAuthStatus = {
                authStatusMessage = null
            },
            onReminderIntervalChange = { value ->
                scope.launch { settingsStore.setReminderIntervalMinutes(value) }
            },
            onSnoozeMinutesChange = { value ->
                scope.launch { settingsStore.setSnoozeMinutes(value) }
            },
            onThemeModeChange = { value ->
                scope.launch { settingsStore.setThemeMode(value) }
            },
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskReminderApp(
    settings: TaskReminderSettings,
    authSession: AuthSession,
    authStatusMessage: String?,
    isAuthActionInProgress: Boolean,
    taskCacheSnapshot: TaskCacheSnapshot,
    onGoogleSignInClick: () -> Unit,
    onGoogleSignOutClick: () -> Unit,
    onDismissAuthStatus: () -> Unit,
    onReminderIntervalChange: (Int) -> Unit,
    onSnoozeMinutesChange: (Int) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedRoute by rememberSaveable { mutableStateOf(TODAY_ROUTE) }
    val selectedSection = appSectionForRoute(selectedRoute)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = appName(),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = selectedSection.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                AppDestinations.forEach { destination ->
                    NavigationBarItem(
                        selected = selectedRoute == destination.section.route,
                        onClick = { selectedRoute = destination.section.route },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = null,
                            )
                        },
                        label = { Text(destination.section.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background,
        ) {
            when (selectedRoute) {
                TODAY_ROUTE -> TodayScreen(
                    settings = settings,
                    taskCacheSnapshot = taskCacheSnapshot,
                )
                TASKS_ROUTE -> TasksScreen(taskCacheSnapshot = taskCacheSnapshot)
                SETTINGS_ROUTE -> SettingsScreen(
                    settings = settings,
                    authSession = authSession,
                    authStatusMessage = authStatusMessage,
                    isAuthActionInProgress = isAuthActionInProgress,
                    onGoogleSignInClick = onGoogleSignInClick,
                    onGoogleSignOutClick = onGoogleSignOutClick,
                    onDismissAuthStatus = onDismissAuthStatus,
                    onReminderIntervalChange = onReminderIntervalChange,
                    onSnoozeMinutesChange = onSnoozeMinutesChange,
                    onThemeModeChange = onThemeModeChange,
                )

                else -> TodayScreen(
                    settings = settings,
                    taskCacheSnapshot = taskCacheSnapshot,
                )
            }
        }
    }
}

@Composable
private fun TodayScreen(
    settings: TaskReminderSettings,
    taskCacheSnapshot: TaskCacheSnapshot,
) {
    AppScreenScaffold(section = appSectionForRoute(TODAY_ROUTE)) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "${taskCacheSnapshot.pendingTaskCount} pending tasks",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (taskCacheSnapshot.hasSyncedData) {
                        "Reading from the local Room cache."
                    } else {
                        "Local task cache is ready. Google Tasks sync comes next."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = {},
                label = { Text("Review every ${settings.reminderIntervalMinutes} min") },
            )
            AssistChip(
                onClick = {},
                label = { Text("Snooze ${settings.snoozeMinutes} min") },
            )
        }
    }
}

@Composable
private fun TasksScreen(taskCacheSnapshot: TaskCacheSnapshot) {
    AppScreenScaffold(section = appSectionForRoute(TASKS_ROUTE)) {
        if (taskCacheSnapshot.pendingTasks.isEmpty()) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "No cached pending tasks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "The Room cache is connected. Future Google Tasks sync will write tasks here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            taskCacheSnapshot.pendingTasks.forEach { task ->
                CachedTaskCard(task = task)
            }
        }
    }
}

@Composable
private fun CachedTaskCard(task: CachedTask) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = task.taskListTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            if (!task.notes.isNullOrBlank()) {
                Text(
                    text = task.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    settings: TaskReminderSettings,
    authSession: AuthSession,
    authStatusMessage: String?,
    isAuthActionInProgress: Boolean,
    onGoogleSignInClick: () -> Unit,
    onGoogleSignOutClick: () -> Unit,
    onDismissAuthStatus: () -> Unit,
    onReminderIntervalChange: (Int) -> Unit,
    onSnoozeMinutesChange: (Int) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    AppScreenScaffold(section = appSectionForRoute(SETTINGS_ROUTE)) {
        GoogleAccountCard(
            authSession = authSession,
            authStatusMessage = authStatusMessage,
            isAuthActionInProgress = isAuthActionInProgress,
            onGoogleSignInClick = onGoogleSignInClick,
            onGoogleSignOutClick = onGoogleSignOutClick,
            onDismissAuthStatus = onDismissAuthStatus,
        )

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                StepperSetting(
                    title = "Reminder interval",
                    valueText = "${settings.reminderIntervalMinutes} minutes",
                    canDecrease = settings.reminderIntervalMinutes > MIN_REMINDER_INTERVAL_MINUTES,
                    canIncrease = settings.reminderIntervalMinutes < MAX_REMINDER_INTERVAL_MINUTES,
                    onDecrease = {
                        onReminderIntervalChange(settings.reminderIntervalMinutes - 5)
                    },
                    onIncrease = {
                        onReminderIntervalChange(settings.reminderIntervalMinutes + 5)
                    },
                )
                StepperSetting(
                    title = "Snooze duration",
                    valueText = "${settings.snoozeMinutes} minutes",
                    canDecrease = settings.snoozeMinutes > MIN_SNOOZE_MINUTES,
                    canIncrease = settings.snoozeMinutes < MAX_SNOOZE_MINUTES,
                    onDecrease = {
                        onSnoozeMinutesChange(settings.snoozeMinutes - 5)
                    },
                    onIncrease = {
                        onSnoozeMinutesChange(settings.snoozeMinutes + 5)
                    },
                )
                ThemeSetting(
                    selectedThemeMode = settings.themeMode,
                    onThemeModeChange = onThemeModeChange,
                )
            }
        }
    }
}

@Composable
private fun GoogleAccountCard(
    authSession: AuthSession,
    authStatusMessage: String?,
    isAuthActionInProgress: Boolean,
    onGoogleSignInClick: () -> Unit,
    onGoogleSignOutClick: () -> Unit,
    onDismissAuthStatus: () -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Google Tasks account",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = if (authSession.isSignedIn) {
                    "Signed in as ${authSession.displayLabel}"
                } else {
                    "Connect a Google account before task sync is enabled."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (!authStatusMessage.isNullOrBlank()) {
                ListItem(
                    headlineContent = { Text(authStatusMessage) },
                    trailingContent = {
                        OutlinedButton(onClick = onDismissAuthStatus) {
                            Text("Dismiss")
                        }
                    },
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (authSession.isSignedIn) {
                    OutlinedButton(
                        onClick = onGoogleSignOutClick,
                        enabled = !isAuthActionInProgress,
                    ) {
                        Text("Sign out")
                    }
                } else {
                    Button(
                        onClick = onGoogleSignInClick,
                        enabled = !isAuthActionInProgress,
                    ) {
                        Text("Sign in with Google")
                    }
                }

                if (isAuthActionInProgress) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun StepperSetting(
    title: String,
    valueText: String,
    canDecrease: Boolean,
    canIncrease: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(valueText) },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                IconButton(
                    onClick = onDecrease,
                    enabled = canDecrease,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Remove,
                        contentDescription = "Decrease $title",
                    )
                }
                IconButton(
                    onClick = onIncrease,
                    enabled = canIncrease,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Increase $title",
                    )
                }
            }
        },
    )
}

@Composable
private fun ThemeSetting(
    selectedThemeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    ListItem(
        headlineContent = { Text("Theme") },
        supportingContent = {
            Row(
                modifier = Modifier.selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ThemeMode.entries.forEach { themeMode ->
                    FilterChip(
                        selected = selectedThemeMode == themeMode,
                        onClick = { onThemeModeChange(themeMode) },
                        label = { Text(themeMode.label) },
                    )
                }
            }
        },
    )
}

@Composable
private fun AppScreenScaffold(
    section: AppSectionCopy,
    content: @Composable ColumnScope.() -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column {
                Text(
                    text = section.headline,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = section.body,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content,
            )
        }
        item {
            Box(modifier = Modifier.height(1.dp))
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
