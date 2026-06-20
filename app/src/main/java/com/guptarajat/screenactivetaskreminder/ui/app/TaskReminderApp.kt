package com.guptarajat.screenactivetaskreminder.ui.app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings as AndroidSettings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Sync
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.core.content.ContextCompat
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
import com.guptarajat.screenactivetaskreminder.data.repository.CachedTaskList
import com.guptarajat.screenactivetaskreminder.data.local.TaskReminderDatabase
import com.guptarajat.screenactivetaskreminder.data.remote.GoogleTasksApiClient
import com.guptarajat.screenactivetaskreminder.data.remote.GoogleTasksFetchResult
import com.guptarajat.screenactivetaskreminder.data.repository.TaskCacheRepository
import com.guptarajat.screenactivetaskreminder.data.repository.TaskCacheSnapshot
import com.guptarajat.screenactivetaskreminder.reminders.ReminderNotificationCoordinator
import com.guptarajat.screenactivetaskreminder.reminders.ReminderScheduler
import com.guptarajat.screenactivetaskreminder.reminders.reminderNotificationStatusMessage
import com.guptarajat.screenactivetaskreminder.reminders.snoozeUntilMillis
import com.guptarajat.screenactivetaskreminder.screenactivity.UsageAccessDiagnosticSnapshot
import com.guptarajat.screenactivetaskreminder.screenactivity.UsageAccessDiagnostics
import com.guptarajat.screenactivetaskreminder.settings.MAX_REMINDER_INTERVAL_MINUTES
import com.guptarajat.screenactivetaskreminder.settings.MAX_SNOOZE_MINUTES
import com.guptarajat.screenactivetaskreminder.settings.MIN_REMINDER_INTERVAL_MINUTES
import com.guptarajat.screenactivetaskreminder.settings.MIN_SNOOZE_MINUTES
import com.guptarajat.screenactivetaskreminder.settings.QUIET_HOURS_STEP_MINUTES
import com.guptarajat.screenactivetaskreminder.settings.SettingsStore
import com.guptarajat.screenactivetaskreminder.settings.TaskReminderSettings
import com.guptarajat.screenactivetaskreminder.settings.ThemeMode
import com.guptarajat.screenactivetaskreminder.settings.formatMinuteOfDay
import com.guptarajat.screenactivetaskreminder.sync.GoogleTasksAuthorizationClient
import com.guptarajat.screenactivetaskreminder.sync.GoogleTasksAuthorizationResult
import com.guptarajat.screenactivetaskreminder.sync.userMessage as googleTasksAuthorizationUserMessage
import com.guptarajat.screenactivetaskreminder.ui.theme.TaskReminderTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Date

private data class AppDestination(
    val section: AppSectionCopy,
    val icon: ImageVector,
)

private val AppDestinations = listOf(
    AppDestination(appSectionForRoute(TODAY_ROUTE), Icons.Outlined.CheckCircle),
    AppDestination(appSectionForRoute(TASKS_ROUTE), Icons.AutoMirrored.Outlined.List),
    AppDestination(appSectionForRoute(SETTINGS_ROUTE), Icons.Outlined.Settings),
)

private fun lastSyncedLabel(lastSuccessfulSyncAtMillis: Long?): String =
    if (lastSuccessfulSyncAtMillis == null) {
        "Last synced: never"
    } else {
        "Last synced: ${
            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                .format(Date(lastSuccessfulSyncAtMillis))
        }"
    }

private fun taskListSelectionLabel(snapshot: TaskCacheSnapshot): String =
    when {
        snapshot.taskListCount == 0 -> "No task lists synced yet."
        snapshot.selectedTaskListCount == snapshot.taskListCount ->
            "Watching all ${snapshot.taskListCount} synced lists."
        snapshot.selectedTaskListCount == 1 ->
            "Watching 1 of ${snapshot.taskListCount} synced lists."
        else -> "Watching ${snapshot.selectedTaskListCount} of ${snapshot.taskListCount} synced lists."
    }

private enum class OnboardingStep {
    WELCOME,
    GOOGLE_TASKS,
    NOTIFICATIONS,
    DEFAULT_REMINDERS,
}

private data class OnboardingStepCopy(
    val icon: ImageVector,
    val title: String,
    val body: String,
)

private val OnboardingSteps = OnboardingStep.entries

private fun onboardingStepCopy(step: OnboardingStep): OnboardingStepCopy = when (step) {
    OnboardingStep.WELCOME -> OnboardingStepCopy(
        icon = Icons.Outlined.CheckCircle,
        title = "Review tasks before they drift",
        body = "Screen Active Task Reminder keeps the first version simple: Google Tasks, local reminders, and standard Android notifications.",
    )

    OnboardingStep.GOOGLE_TASKS -> OnboardingStepCopy(
        icon = Icons.Outlined.Sync,
        title = "Connect Google Tasks",
        body = "Sign in when you are ready. The app uses read-only Google Tasks access and stores the task cache on this device.",
    )

    OnboardingStep.NOTIFICATIONS -> OnboardingStepCopy(
        icon = Icons.Outlined.Notifications,
        title = "Allow reminder notifications",
        body = "Notifications are the standard V1 reminder surface. You can keep using the app even if you skip this for now.",
    )

    OnboardingStep.DEFAULT_REMINDERS -> OnboardingStepCopy(
        icon = Icons.Outlined.Settings,
        title = "Start with calm defaults",
        body = "The default reminder rhythm is ready. You can tune interval, snooze, quiet hours, and theme from Settings later.",
    )
}

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
    val googleTasksAuthorizationClient = remember(context) {
        GoogleTasksAuthorizationClient(context)
    }
    val googleTasksApiClient = remember { GoogleTasksApiClient() }
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
    var syncStatusMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isTaskSyncInProgress by rememberSaveable { mutableStateOf(false) }
    var pendingSyncAccountId by rememberSaveable { mutableStateOf<String?>(null) }
    var reminderStatusMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var usageAccessStatusMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var usageAccessSnapshot by remember {
        mutableStateOf(UsageAccessDiagnostics.accessSnapshot(context))
    }
    var areNotificationsAllowed by rememberSaveable {
        mutableStateOf(ReminderNotificationCoordinator.areNotificationsEnabled(context))
    }

    suspend fun scheduleNextReminderCheck() {
        runCatching {
            ReminderScheduler.scheduleNext(context)
        }
    }

    LaunchedEffect(context) {
        ReminderNotificationCoordinator.ensureNotificationChannel(context)
        areNotificationsAllowed = ReminderNotificationCoordinator.areNotificationsEnabled(context)
        usageAccessSnapshot = UsageAccessDiagnostics.accessSnapshot(context)
    }

    LaunchedEffect(context, settings) {
        scheduleNextReminderCheck()
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        areNotificationsAllowed = ReminderNotificationCoordinator.areNotificationsEnabled(context)
        reminderStatusMessage = if (isGranted || areNotificationsAllowed) {
            "Notifications are enabled."
        } else {
            "Notifications are still off. Open Android notification settings if the prompt does not appear again."
        }
        scope.launch {
            scheduleNextReminderCheck()
        }
    }

    val notificationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) {
        areNotificationsAllowed = ReminderNotificationCoordinator.areNotificationsEnabled(context)
        reminderStatusMessage = if (areNotificationsAllowed) {
            "Notifications are enabled."
        } else {
            "Notifications are still off. Turn on notifications in Android settings to receive reminders."
        }
        scope.launch {
            scheduleNextReminderCheck()
        }
    }

    fun syncGoogleTasks(accessToken: String, accountId: String?) {
        scope.launch {
            val resolvedAccountId = accountId?.takeIf { it.isNotBlank() } ?: "primary"
            when (val result = googleTasksApiClient.fetchTaskCache(accessToken)) {
                is GoogleTasksFetchResult.Success -> {
                    taskCacheRepository.replaceCache(
                        fetchedCache = result.cache,
                        accountId = resolvedAccountId,
                    )
                    syncStatusMessage =
                        "Synced ${result.cache.tasks.size} pending tasks from ${result.cache.taskLists.size} lists."
                }

                is GoogleTasksFetchResult.Failure -> {
                    taskCacheRepository.recordSyncError(
                        accountId = resolvedAccountId,
                        message = result.message,
                    )
                    syncStatusMessage = result.message
                }
            }
            scheduleNextReminderCheck()
            isTaskSyncInProgress = false
            pendingSyncAccountId = null
        }
    }

    val tasksAuthorizationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { activityResult ->
        if (activityResult.resultCode != Activity.RESULT_OK) {
            syncStatusMessage = "Google Tasks permission was cancelled."
            isTaskSyncInProgress = false
            pendingSyncAccountId = null
            return@rememberLauncherForActivityResult
        }

        when (
            val authorizationResult =
                googleTasksAuthorizationClient.readAuthorizationResult(activityResult.data)
        ) {
            is GoogleTasksAuthorizationResult.Authorized -> {
                syncGoogleTasks(
                    accessToken = authorizationResult.accessToken,
                    accountId = pendingSyncAccountId ?: authSession.accountId,
                )
            }

            else -> {
                syncStatusMessage =
                    authorizationResult.googleTasksAuthorizationUserMessage()
                        ?: "Google Tasks permission was not granted."
                isTaskSyncInProgress = false
                pendingSyncAccountId = null
            }
        }
    }

    fun startGoogleSignIn() {
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
    }

    fun enableReminderNotifications() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            areNotificationsAllowed =
                ReminderNotificationCoordinator.areNotificationsEnabled(context)
            reminderStatusMessage = if (areNotificationsAllowed) {
                "Notifications are enabled."
            } else {
                "Notifications are still off. Open Android notification settings to receive reminders."
            }
            scope.launch {
                scheduleNextReminderCheck()
            }
        }
    }

    fun checkReminderNotificationStatus() {
        areNotificationsAllowed = ReminderNotificationCoordinator.areNotificationsEnabled(context)
        reminderStatusMessage = if (areNotificationsAllowed) {
            "Notifications are enabled."
        } else {
            "Notifications are off. Use Enable or open Android notification settings."
        }
        scope.launch {
            scheduleNextReminderCheck()
        }
    }

    fun openReminderNotificationSettings() {
        val settingsIntent = Intent(AndroidSettings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(AndroidSettings.EXTRA_APP_PACKAGE, context.packageName)
        }
        runCatching {
            notificationSettingsLauncher.launch(settingsIntent)
        }.onFailure {
            val fallbackIntent = Intent(AndroidSettings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            runCatching {
                notificationSettingsLauncher.launch(fallbackIntent)
            }.onFailure {
                reminderStatusMessage = "Android notification settings could not be opened."
            }
        }
    }

    fun completeOnboarding() {
        scope.launch {
            settingsStore.setOnboardingCompleted(true)
            scheduleNextReminderCheck()
        }
    }

    fun setTaskListSelected(taskListId: String, isSelected: Boolean) {
        scope.launch {
            taskCacheRepository.setTaskListSelected(taskListId, isSelected)
            syncStatusMessage = "Task list filter updated."
            scheduleNextReminderCheck()
        }
    }

    TaskReminderTheme(
        darkTheme = when (settings.themeMode) {
            ThemeMode.SYSTEM -> systemDarkTheme
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        },
    ) {
        if (!settings.hasCompletedOnboarding) {
            OnboardingScreen(
                settings = settings,
                authSession = authSession,
                authStatusMessage = authStatusMessage,
                isAuthActionInProgress = isAuthActionInProgress,
                areNotificationsAllowed = areNotificationsAllowed,
                onGoogleSignInClick = { startGoogleSignIn() },
                onDismissAuthStatus = { authStatusMessage = null },
                onEnableNotificationsClick = { enableReminderNotifications() },
                onFinishClick = { completeOnboarding() },
                modifier = modifier,
            )
        } else {
            TaskReminderApp(
                settings = settings,
                authSession = authSession,
                authStatusMessage = authStatusMessage,
                isAuthActionInProgress = isAuthActionInProgress,
                taskCacheSnapshot = taskCacheSnapshot,
                syncStatusMessage = syncStatusMessage,
                isTaskSyncInProgress = isTaskSyncInProgress,
                onGoogleSignInClick = { startGoogleSignIn() },
                onGoogleSignOutClick = {
                    scope.launch {
                        isAuthActionInProgress = true
                        runCatching { googleSignInClient.signOut() }
                        authStore.clearSession()
                        authStatusMessage = "Signed out."
                        isAuthActionInProgress = false
                    }
                },
                onGoogleTasksSyncClick = {
                    if (!authSession.isSignedIn) {
                        syncStatusMessage = "Sign in with Google before syncing tasks."
                    } else {
                        val activity = context.findActivity()
                        if (activity == null) {
                            syncStatusMessage = "Task sync needs an active Android screen."
                        } else {
                            scope.launch {
                                isTaskSyncInProgress = true
                                syncStatusMessage = null
                                pendingSyncAccountId = authSession.accountId
                                when (
                                    val authorizationResult =
                                        googleTasksAuthorizationClient.requestAccess(activity)
                                ) {
                                    is GoogleTasksAuthorizationResult.Authorized -> {
                                        syncGoogleTasks(
                                            accessToken = authorizationResult.accessToken,
                                            accountId = authSession.accountId,
                                        )
                                    }

                                    is GoogleTasksAuthorizationResult.NeedsUserConsent -> {
                                        val request = IntentSenderRequest.Builder(
                                            authorizationResult.pendingIntent.intentSender,
                                        ).build()
                                        runCatching {
                                            tasksAuthorizationLauncher.launch(request)
                                        }.onFailure { error ->
                                            syncStatusMessage =
                                                error.localizedMessage
                                                    ?: "Google Tasks permission screen could not open."
                                            isTaskSyncInProgress = false
                                            pendingSyncAccountId = null
                                        }
                                    }

                                    else -> {
                                        syncStatusMessage =
                                            authorizationResult.googleTasksAuthorizationUserMessage()
                                                ?: "Google Tasks permission was not granted."
                                        isTaskSyncInProgress = false
                                        pendingSyncAccountId = null
                                    }
                                }
                            }
                        }
                    }
                },
                onDismissAuthStatus = {
                    authStatusMessage = null
                },
                onDismissSyncStatus = {
                    syncStatusMessage = null
                },
                onTaskListSelectionChange = { taskListId, isSelected ->
                    setTaskListSelected(taskListId, isSelected)
                },
                reminderStatusMessage = reminderStatusMessage,
                areNotificationsAllowed = areNotificationsAllowed,
                onDismissReminderStatus = {
                    reminderStatusMessage = null
                },
                onEnableNotificationsClick = { enableReminderNotifications() },
                onOpenNotificationSettingsClick = { openReminderNotificationSettings() },
                onCheckNotificationStatusClick = { checkReminderNotificationStatus() },
                onCheckReminderNowClick = {
                    scope.launch {
                        val result = ReminderNotificationCoordinator.evaluateAndNotify(context)
                        areNotificationsAllowed =
                            ReminderNotificationCoordinator.areNotificationsEnabled(context)
                        reminderStatusMessage = reminderNotificationStatusMessage(result)
                        scheduleNextReminderCheck()
                    }
                },
                onReviewNowClick = {
                    scope.launch {
                        settingsStore.recordReview(System.currentTimeMillis())
                        ReminderNotificationCoordinator.cancel(context)
                        reminderStatusMessage = "Review recorded."
                        scheduleNextReminderCheck()
                    }
                },
                onSnoozeReminderClick = {
                    scope.launch {
                        settingsStore.snoozeUntil(
                            snoozeUntilMillis(
                                nowMillis = System.currentTimeMillis(),
                                snoozeMinutes = settings.snoozeMinutes,
                            ),
                        )
                        ReminderNotificationCoordinator.cancel(context)
                        reminderStatusMessage =
                            "Reminders snoozed for ${settings.snoozeMinutes} minutes."
                        scheduleNextReminderCheck()
                    }
                },
                onReminderIntervalChange = { value ->
                    scope.launch { settingsStore.setReminderIntervalMinutes(value) }
                },
                onSnoozeMinutesChange = { value ->
                    scope.launch { settingsStore.setSnoozeMinutes(value) }
                },
                onQuietHoursEnabledChange = { value ->
                    scope.launch { settingsStore.setQuietHoursEnabled(value) }
                },
                onQuietHoursStartChange = { value ->
                    scope.launch { settingsStore.setQuietHoursStartMinuteOfDay(value) }
                },
                onQuietHoursEndChange = { value ->
                    scope.launch { settingsStore.setQuietHoursEndMinuteOfDay(value) }
                },
                onThemeModeChange = { value ->
                    scope.launch { settingsStore.setThemeMode(value) }
                },
                usageAccessSnapshot = usageAccessSnapshot,
                usageAccessStatusMessage = usageAccessStatusMessage,
                onCheckUsageAccessClick = {
                    usageAccessSnapshot = UsageAccessDiagnostics.accessSnapshot(context)
                    usageAccessStatusMessage = if (usageAccessSnapshot.hasUsageAccess) {
                        "Usage Access is enabled."
                    } else {
                        "Usage Access is off."
                    }
                },
                onOpenUsageAccessSettingsClick = {
                    val didOpenSettings = UsageAccessDiagnostics.openUsageAccessSettings(context)
                    usageAccessStatusMessage = if (didOpenSettings) {
                        "Opened Android Usage Access settings."
                    } else {
                        "Android Usage Access settings could not be opened."
                    }
                },
                onScanScreenActivityClick = {
                    scope.launch {
                        usageAccessSnapshot = withContext(Dispatchers.IO) {
                            UsageAccessDiagnostics.scanRecentActivity(context)
                        }
                        usageAccessStatusMessage = usageAccessScanStatusMessage(usageAccessSnapshot)
                    }
                },
                onDismissUsageAccessStatus = {
                    usageAccessStatusMessage = null
                },
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun OnboardingScreen(
    settings: TaskReminderSettings,
    authSession: AuthSession,
    authStatusMessage: String?,
    isAuthActionInProgress: Boolean,
    areNotificationsAllowed: Boolean,
    onGoogleSignInClick: () -> Unit,
    onDismissAuthStatus: () -> Unit,
    onEnableNotificationsClick: () -> Unit,
    onFinishClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var stepIndex by rememberSaveable { mutableStateOf(0) }
    val step = OnboardingSteps[stepIndex]
    val stepCopy = onboardingStepCopy(step)
    val isLastStep = stepIndex == OnboardingSteps.lastIndex

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column {
                    Text(
                        text = appName(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Step ${stepIndex + 1} of ${OnboardingSteps.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Icon(
                            imageVector = stepCopy.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp),
                        )
                        Text(
                            text = stepCopy.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = stepCopy.body,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        OnboardingStepDetail(
                            step = step,
                            settings = settings,
                            authSession = authSession,
                            authStatusMessage = authStatusMessage,
                            isAuthActionInProgress = isAuthActionInProgress,
                            areNotificationsAllowed = areNotificationsAllowed,
                            onGoogleSignInClick = onGoogleSignInClick,
                            onDismissAuthStatus = onDismissAuthStatus,
                            onEnableNotificationsClick = onEnableNotificationsClick,
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            if (isLastStep) {
                                onFinishClick()
                            } else {
                                stepIndex += 1
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(if (isLastStep) "Start using app" else "Continue")
                    }
                    OutlinedButton(
                        onClick = onFinishClick,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Set up later")
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingStepDetail(
    step: OnboardingStep,
    settings: TaskReminderSettings,
    authSession: AuthSession,
    authStatusMessage: String?,
    isAuthActionInProgress: Boolean,
    areNotificationsAllowed: Boolean,
    onGoogleSignInClick: () -> Unit,
    onDismissAuthStatus: () -> Unit,
    onEnableNotificationsClick: () -> Unit,
) {
    when (step) {
        OnboardingStep.WELCOME -> Text(
            text = "No ads, no overlays, and no advanced screen-activity permission in first-run setup.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        OnboardingStep.GOOGLE_TASKS -> GoogleTasksOnboardingDetail(
            authSession = authSession,
            authStatusMessage = authStatusMessage,
            isAuthActionInProgress = isAuthActionInProgress,
            onGoogleSignInClick = onGoogleSignInClick,
            onDismissAuthStatus = onDismissAuthStatus,
        )

        OnboardingStep.NOTIFICATIONS -> NotificationsOnboardingDetail(
            areNotificationsAllowed = areNotificationsAllowed,
            onEnableNotificationsClick = onEnableNotificationsClick,
        )

        OnboardingStep.DEFAULT_REMINDERS -> ReminderDefaultsOnboardingDetail(settings = settings)
    }
}

@Composable
private fun GoogleTasksOnboardingDetail(
    authSession: AuthSession,
    authStatusMessage: String?,
    isAuthActionInProgress: Boolean,
    onGoogleSignInClick: () -> Unit,
    onDismissAuthStatus: () -> Unit,
) {
    Text(
        text = if (authSession.isSignedIn) {
            "Signed in as ${authSession.displayLabel}."
        } else {
            "You can sign in now or finish onboarding and connect later from Settings."
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
    if (!authSession.isSignedIn) {
        Button(
            onClick = onGoogleSignInClick,
            enabled = !isAuthActionInProgress,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Sign in with Google")
        }
    }
    if (isAuthActionInProgress) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun NotificationsOnboardingDetail(
    areNotificationsAllowed: Boolean,
    onEnableNotificationsClick: () -> Unit,
) {
    Text(
        text = if (areNotificationsAllowed) {
            "Notifications are enabled."
        } else {
            "Notifications are not enabled yet."
        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    if (!areNotificationsAllowed) {
        Button(
            onClick = onEnableNotificationsClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Enable notifications")
        }
    }
}

@Composable
private fun ReminderDefaultsOnboardingDetail(settings: TaskReminderSettings) {
    ListItem(
        headlineContent = { Text("Reminder interval") },
        supportingContent = { Text("${settings.reminderIntervalMinutes} minutes") },
    )
    ListItem(
        headlineContent = { Text("Snooze duration") },
        supportingContent = { Text("${settings.snoozeMinutes} minutes") },
    )
    ListItem(
        headlineContent = { Text("Quiet hours") },
        supportingContent = {
            Text(
                if (settings.quietHoursEnabled) {
                    "${formatMinuteOfDay(settings.quietHoursStartMinuteOfDay)} to " +
                        formatMinuteOfDay(settings.quietHoursEndMinuteOfDay)
                } else {
                    "Off"
                },
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskReminderApp(
    settings: TaskReminderSettings,
    authSession: AuthSession,
    authStatusMessage: String?,
    isAuthActionInProgress: Boolean,
    taskCacheSnapshot: TaskCacheSnapshot,
    syncStatusMessage: String?,
    isTaskSyncInProgress: Boolean,
    onGoogleSignInClick: () -> Unit,
    onGoogleSignOutClick: () -> Unit,
    onGoogleTasksSyncClick: () -> Unit,
    onTaskListSelectionChange: (String, Boolean) -> Unit,
    onDismissAuthStatus: () -> Unit,
    onDismissSyncStatus: () -> Unit,
    reminderStatusMessage: String?,
    areNotificationsAllowed: Boolean,
    onDismissReminderStatus: () -> Unit,
    onEnableNotificationsClick: () -> Unit,
    onOpenNotificationSettingsClick: () -> Unit,
    onCheckNotificationStatusClick: () -> Unit,
    onCheckReminderNowClick: () -> Unit,
    onReviewNowClick: () -> Unit,
    onSnoozeReminderClick: () -> Unit,
    onReminderIntervalChange: (Int) -> Unit,
    onSnoozeMinutesChange: (Int) -> Unit,
    onQuietHoursEnabledChange: (Boolean) -> Unit,
    onQuietHoursStartChange: (Int) -> Unit,
    onQuietHoursEndChange: (Int) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    usageAccessSnapshot: UsageAccessDiagnosticSnapshot,
    usageAccessStatusMessage: String?,
    onCheckUsageAccessClick: () -> Unit,
    onOpenUsageAccessSettingsClick: () -> Unit,
    onScanScreenActivityClick: () -> Unit,
    onDismissUsageAccessStatus: () -> Unit,
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
                    authSession = authSession,
                    taskCacheSnapshot = taskCacheSnapshot,
                    reminderStatusMessage = reminderStatusMessage,
                    areNotificationsAllowed = areNotificationsAllowed,
                    onDismissReminderStatus = onDismissReminderStatus,
                    onEnableNotificationsClick = onEnableNotificationsClick,
                    onOpenNotificationSettingsClick = onOpenNotificationSettingsClick,
                    onCheckReminderNowClick = onCheckReminderNowClick,
                    onReviewNowClick = onReviewNowClick,
                    onSnoozeReminderClick = onSnoozeReminderClick,
                    onOpenTasksClick = { selectedRoute = TASKS_ROUTE },
                    onOpenSettingsClick = { selectedRoute = SETTINGS_ROUTE },
                )
                TASKS_ROUTE -> TasksScreen(
                    taskCacheSnapshot = taskCacheSnapshot,
                    authSession = authSession,
                    syncStatusMessage = syncStatusMessage,
                    isTaskSyncInProgress = isTaskSyncInProgress,
                    onGoogleTasksSyncClick = onGoogleTasksSyncClick,
                    onTaskListSelectionChange = onTaskListSelectionChange,
                    onDismissSyncStatus = onDismissSyncStatus,
                    onOpenSettingsClick = { selectedRoute = SETTINGS_ROUTE },
                )
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
                    onQuietHoursEnabledChange = onQuietHoursEnabledChange,
                    onQuietHoursStartChange = onQuietHoursStartChange,
                    onQuietHoursEndChange = onQuietHoursEndChange,
                    onThemeModeChange = onThemeModeChange,
                    reminderStatusMessage = reminderStatusMessage,
                    areNotificationsAllowed = areNotificationsAllowed,
                    onDismissReminderStatus = onDismissReminderStatus,
                    onEnableNotificationsClick = onEnableNotificationsClick,
                    onOpenNotificationSettingsClick = onOpenNotificationSettingsClick,
                    onCheckNotificationStatusClick = onCheckNotificationStatusClick,
                    usageAccessSnapshot = usageAccessSnapshot,
                    usageAccessStatusMessage = usageAccessStatusMessage,
                    onCheckUsageAccessClick = onCheckUsageAccessClick,
                    onOpenUsageAccessSettingsClick = onOpenUsageAccessSettingsClick,
                    onScanScreenActivityClick = onScanScreenActivityClick,
                    onDismissUsageAccessStatus = onDismissUsageAccessStatus,
                )

                else -> TodayScreen(
                    settings = settings,
                    authSession = authSession,
                    taskCacheSnapshot = taskCacheSnapshot,
                    reminderStatusMessage = reminderStatusMessage,
                    areNotificationsAllowed = areNotificationsAllowed,
                    onDismissReminderStatus = onDismissReminderStatus,
                    onEnableNotificationsClick = onEnableNotificationsClick,
                    onOpenNotificationSettingsClick = onOpenNotificationSettingsClick,
                    onCheckReminderNowClick = onCheckReminderNowClick,
                    onReviewNowClick = onReviewNowClick,
                    onSnoozeReminderClick = onSnoozeReminderClick,
                    onOpenTasksClick = { selectedRoute = TASKS_ROUTE },
                    onOpenSettingsClick = { selectedRoute = SETTINGS_ROUTE },
                )
            }
        }
    }
}

@Composable
private fun TodayScreen(
    settings: TaskReminderSettings,
    authSession: AuthSession,
    taskCacheSnapshot: TaskCacheSnapshot,
    reminderStatusMessage: String?,
    areNotificationsAllowed: Boolean,
    onDismissReminderStatus: () -> Unit,
    onEnableNotificationsClick: () -> Unit,
    onOpenNotificationSettingsClick: () -> Unit,
    onCheckReminderNowClick: () -> Unit,
    onReviewNowClick: () -> Unit,
    onSnoozeReminderClick: () -> Unit,
    onOpenTasksClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
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
                        lastSyncedLabel(taskCacheSnapshot.lastSuccessfulSyncAtMillis)
                    } else {
                        "Sync Google Tasks to fill the local cache."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                if (taskCacheSnapshot.taskListCount > 0) {
                    Text(
                        text = taskListSelectionLabel(taskCacheSnapshot),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }

        TodayGuidanceCard(
            taskCacheSnapshot = taskCacheSnapshot,
            authSession = authSession,
            onOpenTasksClick = onOpenTasksClick,
            onOpenSettingsClick = onOpenSettingsClick,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = onReviewNowClick,
                label = { Text("Review now") },
            )
            AssistChip(
                onClick = onSnoozeReminderClick,
                label = { Text("Snooze ${settings.snoozeMinutes} min") },
            )
        }

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reminder notifications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = if (areNotificationsAllowed) {
                                "Notifications are enabled."
                            } else {
                                "Notifications are off."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                if (!reminderStatusMessage.isNullOrBlank()) {
                    ListItem(
                        headlineContent = { Text(reminderStatusMessage) },
                        trailingContent = {
                            OutlinedButton(onClick = onDismissReminderStatus) {
                                Text("Dismiss")
                            }
                        },
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!areNotificationsAllowed) {
                        OutlinedButton(
                            onClick = onEnableNotificationsClick,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Enable")
                        }
                        OutlinedButton(
                            onClick = onOpenNotificationSettingsClick,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Open settings")
                        }
                    }
                    Button(
                        onClick = onCheckReminderNowClick,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Check now")
                    }
                }
            }
        }
    }
}

@Composable
private fun TasksScreen(
    taskCacheSnapshot: TaskCacheSnapshot,
    authSession: AuthSession,
    syncStatusMessage: String?,
    isTaskSyncInProgress: Boolean,
    onGoogleTasksSyncClick: () -> Unit,
    onTaskListSelectionChange: (String, Boolean) -> Unit,
    onDismissSyncStatus: () -> Unit,
    onOpenSettingsClick: () -> Unit,
) {
    AppScreenScaffold(section = appSectionForRoute(TASKS_ROUTE)) {
        GoogleTasksSyncCard(
            taskCacheSnapshot = taskCacheSnapshot,
            authSession = authSession,
            syncStatusMessage = syncStatusMessage,
            isTaskSyncInProgress = isTaskSyncInProgress,
            onGoogleTasksSyncClick = onGoogleTasksSyncClick,
            onDismissSyncStatus = onDismissSyncStatus,
            onOpenSettingsClick = onOpenSettingsClick,
        )

        TaskListFilterCard(
            taskCacheSnapshot = taskCacheSnapshot,
            onTaskListSelectionChange = onTaskListSelectionChange,
        )

        if (taskCacheSnapshot.pendingTasks.isEmpty()) {
            TaskListEmptyStateCard(
                taskCacheSnapshot = taskCacheSnapshot,
                authSession = authSession,
                isTaskSyncInProgress = isTaskSyncInProgress,
                onGoogleTasksSyncClick = onGoogleTasksSyncClick,
                onOpenSettingsClick = onOpenSettingsClick,
            )
        } else {
            taskCacheSnapshot.pendingTasks.forEach { task ->
                CachedTaskCard(task = task)
            }
        }
    }
}

@Composable
private fun GoogleTasksSyncCard(
    taskCacheSnapshot: TaskCacheSnapshot,
    authSession: AuthSession,
    syncStatusMessage: String?,
    isTaskSyncInProgress: Boolean,
    onGoogleTasksSyncClick: () -> Unit,
    onDismissSyncStatus: () -> Unit,
    onOpenSettingsClick: () -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Google Tasks sync",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = if (authSession.isSignedIn) {
                    "Signed in as ${authSession.displayLabel}. Sync uses read-only Google Tasks access."
                } else {
                    "Sign in with Google in Settings before syncing tasks."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = lastSyncedLabel(taskCacheSnapshot.lastSuccessfulSyncAtMillis),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (taskCacheSnapshot.taskListCount > 0) {
                Text(
                    text = taskListSelectionLabel(taskCacheSnapshot),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (!syncStatusMessage.isNullOrBlank()) {
                ListItem(
                    headlineContent = { Text(syncStatusMessage) },
                    trailingContent = {
                        OutlinedButton(onClick = onDismissSyncStatus) {
                            Text("Dismiss")
                        }
                    },
                )
            } else if (!taskCacheSnapshot.lastError.isNullOrBlank()) {
                ListItem(
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                        )
                    },
                    headlineContent = { Text("Sync did not finish") },
                    supportingContent = {
                        Text("Check your connection, then try syncing Google Tasks again.")
                    },
                    trailingContent = {
                        OutlinedButton(
                            onClick = onGoogleTasksSyncClick,
                            enabled = authSession.isSignedIn && !isTaskSyncInProgress,
                        ) {
                            Text("Try again")
                        }
                    },
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = onGoogleTasksSyncClick,
                    enabled = authSession.isSignedIn && !isTaskSyncInProgress,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Sync,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sync now")
                }
                if (!authSession.isSignedIn) {
                    OutlinedButton(onClick = onOpenSettingsClick) {
                        Text("Sign in")
                    }
                }

                if (isTaskSyncInProgress) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun TaskListFilterCard(
    taskCacheSnapshot: TaskCacheSnapshot,
    onTaskListSelectionChange: (String, Boolean) -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Watched task lists",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = if (taskCacheSnapshot.taskLists.isEmpty()) {
                    "Task lists will appear here after the first successful sync."
                } else {
                    "Choose which Google Task lists feed Today and reminder checks."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (taskCacheSnapshot.taskLists.isEmpty()) {
                ListItem(
                    headlineContent = { Text("No synced task lists yet") },
                    supportingContent = { Text("Sync Google Tasks to choose watched lists.") },
                )
            } else {
                taskCacheSnapshot.taskLists.forEach { taskList ->
                    TaskListSelectionRow(
                        taskList = taskList,
                        selectedTaskListCount = taskCacheSnapshot.selectedTaskListCount,
                        onTaskListSelectionChange = onTaskListSelectionChange,
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskListSelectionRow(
    taskList: CachedTaskList,
    selectedTaskListCount: Int,
    onTaskListSelectionChange: (String, Boolean) -> Unit,
) {
    val canChangeSelection = !taskList.isSelected || selectedTaskListCount > 1

    ListItem(
        headlineContent = { Text(taskList.title) },
        supportingContent = {
            Text(
                if (taskList.isSelected) {
                    "Included in Today and reminders"
                } else {
                    "Hidden from Today and reminders"
                },
            )
        },
        trailingContent = {
            Switch(
                checked = taskList.isSelected,
                enabled = canChangeSelection,
                onCheckedChange = { isSelected ->
                    onTaskListSelectionChange(taskList.id, isSelected)
                },
            )
        },
    )
}

@Composable
private fun TodayGuidanceCard(
    taskCacheSnapshot: TaskCacheSnapshot,
    authSession: AuthSession,
    onOpenTasksClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
) {
    when {
        !authSession.isSignedIn -> GuidanceCard(
            icon = Icons.Outlined.Settings,
            title = "Start with Google Tasks",
            body = "Connect your Google account so reminders can watch your pending tasks.",
            primaryActionLabel = "Go to Settings",
            onPrimaryActionClick = onOpenSettingsClick,
        )

        !taskCacheSnapshot.lastError.isNullOrBlank() -> RecoverableErrorCard(
            title = "Using saved tasks",
            body = "The last sync did not finish, so Today is showing the latest saved task cache.",
            detail = taskCacheSnapshot.lastError,
            primaryActionLabel = "Open Tasks",
            onPrimaryActionClick = onOpenTasksClick,
        )

        !taskCacheSnapshot.hasSyncedData -> GuidanceCard(
            icon = Icons.Outlined.Sync,
            title = "Bring in your tasks",
            body = "Sync once to fill the local task cache. After that, reminders can work from the saved task list.",
            primaryActionLabel = "Open Tasks",
            onPrimaryActionClick = onOpenTasksClick,
        )

        taskCacheSnapshot.pendingTasks.isEmpty() -> GuidanceCard(
            icon = Icons.Outlined.CheckCircle,
            title = "No pending tasks",
            body = "You are clear for now. New pending Google Tasks will appear here after the next sync.",
            primaryActionLabel = "View Tasks",
            onPrimaryActionClick = onOpenTasksClick,
        )
    }
}

@Composable
private fun TaskListEmptyStateCard(
    taskCacheSnapshot: TaskCacheSnapshot,
    authSession: AuthSession,
    isTaskSyncInProgress: Boolean,
    onGoogleTasksSyncClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
) {
    when {
        !authSession.isSignedIn -> GuidanceCard(
            icon = Icons.Outlined.Settings,
            title = "Sign in to load Google Tasks",
            body = "The app only reads your Google Tasks after you connect an account.",
            primaryActionLabel = "Go to Settings",
            onPrimaryActionClick = onOpenSettingsClick,
        )

        !taskCacheSnapshot.lastError.isNullOrBlank() -> RecoverableErrorCard(
            title = "No tasks shown after sync issue",
            body = "The last sync did not finish. Try again when your connection is stable.",
            detail = taskCacheSnapshot.lastError,
            primaryActionLabel = "Try again",
            onPrimaryActionClick = onGoogleTasksSyncClick,
            primaryActionEnabled = !isTaskSyncInProgress,
        )

        !taskCacheSnapshot.hasSyncedData -> GuidanceCard(
            icon = Icons.Outlined.Sync,
            title = "Ready for first sync",
            body = "Sync now to bring pending tasks into the on-device cache.",
            primaryActionLabel = "Sync now",
            onPrimaryActionClick = onGoogleTasksSyncClick,
            primaryActionEnabled = !isTaskSyncInProgress,
        )

        else -> GuidanceCard(
            icon = Icons.Outlined.CheckCircle,
            title = "No pending tasks",
            body = "Your synced Google Tasks do not have pending items right now.",
            primaryActionLabel = "Sync again",
            onPrimaryActionClick = onGoogleTasksSyncClick,
            primaryActionEnabled = !isTaskSyncInProgress,
        )
    }
}

@Composable
private fun GuidanceCard(
    icon: ImageVector,
    title: String,
    body: String,
    primaryActionLabel: String,
    onPrimaryActionClick: () -> Unit,
    primaryActionEnabled: Boolean = true,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Button(
                onClick = onPrimaryActionClick,
                enabled = primaryActionEnabled,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(primaryActionLabel)
            }
        }
    }
}

@Composable
private fun RecoverableErrorCard(
    title: String,
    body: String,
    detail: String?,
    primaryActionLabel: String,
    onPrimaryActionClick: () -> Unit,
    primaryActionEnabled: Boolean = true,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                    if (!detail.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = detail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
            Button(
                onClick = onPrimaryActionClick,
                enabled = primaryActionEnabled,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(primaryActionLabel)
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
    onQuietHoursEnabledChange: (Boolean) -> Unit,
    onQuietHoursStartChange: (Int) -> Unit,
    onQuietHoursEndChange: (Int) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    reminderStatusMessage: String?,
    areNotificationsAllowed: Boolean,
    onDismissReminderStatus: () -> Unit,
    onEnableNotificationsClick: () -> Unit,
    onOpenNotificationSettingsClick: () -> Unit,
    onCheckNotificationStatusClick: () -> Unit,
    usageAccessSnapshot: UsageAccessDiagnosticSnapshot,
    usageAccessStatusMessage: String?,
    onCheckUsageAccessClick: () -> Unit,
    onOpenUsageAccessSettingsClick: () -> Unit,
    onScanScreenActivityClick: () -> Unit,
    onDismissUsageAccessStatus: () -> Unit,
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
                QuietHoursSetting(
                    settings = settings,
                    onQuietHoursEnabledChange = onQuietHoursEnabledChange,
                    onQuietHoursStartChange = onQuietHoursStartChange,
                    onQuietHoursEndChange = onQuietHoursEndChange,
                )
                ThemeSetting(
                    selectedThemeMode = settings.themeMode,
                    onThemeModeChange = onThemeModeChange,
                )
            }
        }

        NotificationRecoveryCard(
            reminderStatusMessage = reminderStatusMessage,
            areNotificationsAllowed = areNotificationsAllowed,
            onDismissReminderStatus = onDismissReminderStatus,
            onEnableNotificationsClick = onEnableNotificationsClick,
            onOpenNotificationSettingsClick = onOpenNotificationSettingsClick,
            onCheckNotificationStatusClick = onCheckNotificationStatusClick,
        )

        ScreenActivityDiagnosticsCard(
            usageAccessSnapshot = usageAccessSnapshot,
            usageAccessStatusMessage = usageAccessStatusMessage,
            onCheckUsageAccessClick = onCheckUsageAccessClick,
            onOpenUsageAccessSettingsClick = onOpenUsageAccessSettingsClick,
            onScanScreenActivityClick = onScanScreenActivityClick,
            onDismissUsageAccessStatus = onDismissUsageAccessStatus,
        )
    }
}

@Composable
private fun NotificationRecoveryCard(
    reminderStatusMessage: String?,
    areNotificationsAllowed: Boolean,
    onDismissReminderStatus: () -> Unit,
    onEnableNotificationsClick: () -> Unit,
    onOpenNotificationSettingsClick: () -> Unit,
    onCheckNotificationStatusClick: () -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = if (areNotificationsAllowed) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Notification recovery",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = if (areNotificationsAllowed) {
                            "Notifications are enabled. Reminder checks can post standard Android notifications."
                        } else {
                            "Notifications are off. Android will block reminders until notifications are allowed."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (!areNotificationsAllowed) {
                ListItem(
                    headlineContent = { Text("How to recover") },
                    supportingContent = {
                        Text(
                            "Tap Enable first. If Android does not show a prompt, open Android settings and turn on notifications for this app.",
                        )
                    },
                )
            }

            if (!reminderStatusMessage.isNullOrBlank()) {
                ListItem(
                    headlineContent = { Text(reminderStatusMessage) },
                    trailingContent = {
                        OutlinedButton(onClick = onDismissReminderStatus) {
                            Text("Dismiss")
                        }
                    },
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onEnableNotificationsClick,
                    enabled = !areNotificationsAllowed,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Enable notifications")
                }
                OutlinedButton(
                    onClick = onOpenNotificationSettingsClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Open Android settings")
                }
                OutlinedButton(
                    onClick = onCheckNotificationStatusClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Check status")
                }
            }
        }
    }
}

@Composable
private fun ScreenActivityDiagnosticsCard(
    usageAccessSnapshot: UsageAccessDiagnosticSnapshot,
    usageAccessStatusMessage: String?,
    onCheckUsageAccessClick: () -> Unit,
    onOpenUsageAccessSettingsClick: () -> Unit,
    onScanScreenActivityClick: () -> Unit,
    onDismissUsageAccessStatus: () -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Screen activity diagnostics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = if (usageAccessSnapshot.hasUsageAccess) {
                    "Usage Access is enabled. Diagnostics can scan recent Android usage events."
                } else {
                    "Usage Access is off. Reminders still work without this optional diagnostic."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (!usageAccessStatusMessage.isNullOrBlank()) {
                ListItem(
                    headlineContent = { Text(usageAccessStatusMessage) },
                    trailingContent = {
                        OutlinedButton(onClick = onDismissUsageAccessStatus) {
                            Text("Dismiss")
                        }
                    },
                )
            }

            if (usageAccessSnapshot.scannedAtMillis != null || usageAccessSnapshot.issue != null) {
                Text(
                    text = "Last scan: ${usageAccessSnapshot.totalEventCount} events in " +
                        "${usageAccessSnapshot.queryWindowMinutes} minutes.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                usageAccessSnapshot.targetEventCounts.forEach { eventCount ->
                    ListItem(
                        headlineContent = { Text(eventCount.type.label) },
                        trailingContent = { Text(eventCount.count.toString()) },
                    )
                }
            }

            Button(
                onClick = onScanScreenActivityClick,
                enabled = usageAccessSnapshot.hasUsageAccess,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Scan recent activity")
            }
            OutlinedButton(
                onClick = onCheckUsageAccessClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Check access")
            }
            OutlinedButton(
                onClick = onOpenUsageAccessSettingsClick,
                enabled = usageAccessSnapshot.canOpenUsageAccessSettings,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Open Usage Access settings")
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
private fun QuietHoursSetting(
    settings: TaskReminderSettings,
    onQuietHoursEnabledChange: (Boolean) -> Unit,
    onQuietHoursStartChange: (Int) -> Unit,
    onQuietHoursEndChange: (Int) -> Unit,
) {
    ListItem(
        headlineContent = { Text("Quiet hours") },
        supportingContent = {
            Text(
                if (settings.quietHoursEnabled) {
                    "${formatMinuteOfDay(settings.quietHoursStartMinuteOfDay)} to " +
                        formatMinuteOfDay(settings.quietHoursEndMinuteOfDay)
                } else {
                    "Off"
                },
            )
        },
        trailingContent = {
            Switch(
                checked = settings.quietHoursEnabled,
                onCheckedChange = onQuietHoursEnabledChange,
            )
        },
    )
    StepperSetting(
        title = "Quiet hours start",
        valueText = formatMinuteOfDay(settings.quietHoursStartMinuteOfDay),
        canDecrease = true,
        canIncrease = true,
        onDecrease = {
            onQuietHoursStartChange(settings.quietHoursStartMinuteOfDay - QUIET_HOURS_STEP_MINUTES)
        },
        onIncrease = {
            onQuietHoursStartChange(settings.quietHoursStartMinuteOfDay + QUIET_HOURS_STEP_MINUTES)
        },
    )
    StepperSetting(
        title = "Quiet hours end",
        valueText = formatMinuteOfDay(settings.quietHoursEndMinuteOfDay),
        canDecrease = true,
        canIncrease = true,
        onDecrease = {
            onQuietHoursEndChange(settings.quietHoursEndMinuteOfDay - QUIET_HOURS_STEP_MINUTES)
        },
        onIncrease = {
            onQuietHoursEndChange(settings.quietHoursEndMinuteOfDay + QUIET_HOURS_STEP_MINUTES)
        },
    )
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

private fun usageAccessScanStatusMessage(
    snapshot: UsageAccessDiagnosticSnapshot,
): String = when {
    !snapshot.issue.isNullOrBlank() -> snapshot.issue
    snapshot.scannedAtMillis != null ->
        "Scanned ${snapshot.totalEventCount} usage events from the last ${snapshot.queryWindowMinutes} minutes."
    else -> "No usage events scanned."
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
