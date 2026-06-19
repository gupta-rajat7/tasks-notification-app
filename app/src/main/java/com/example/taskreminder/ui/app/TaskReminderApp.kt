package com.example.taskreminder.ui.app

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskreminder.AppSectionCopy
import com.example.taskreminder.SETTINGS_ROUTE
import com.example.taskreminder.TASKS_ROUTE
import com.example.taskreminder.TODAY_ROUTE
import com.example.taskreminder.appName
import com.example.taskreminder.appSectionForRoute

private data class AppDestination(
    val section: AppSectionCopy,
    val icon: ImageVector,
)

private val AppDestinations = listOf(
    AppDestination(appSectionForRoute(TODAY_ROUTE), Icons.Outlined.CheckCircle),
    AppDestination(appSectionForRoute(TASKS_ROUTE), Icons.AutoMirrored.Outlined.List),
    AppDestination(appSectionForRoute(SETTINGS_ROUTE), Icons.Outlined.Settings),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskReminderApp(modifier: Modifier = Modifier) {
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
                TODAY_ROUTE -> TodayScreen()
                TASKS_ROUTE -> TasksScreen()
                SETTINGS_ROUTE -> SettingsScreen()
                else -> TodayScreen()
            }
        }
    }
}

@Composable
private fun TodayScreen() {
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
                    text = "0 pending tasks",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Connect Google Tasks in a later step to show live task counts.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = {}, label = { Text("Review") })
            AssistChip(onClick = {}, label = { Text("Snooze") })
        }
    }
}

@Composable
private fun TasksScreen() {
    AppScreenScaffold(section = appSectionForRoute(TASKS_ROUTE)) {
        listOf(
            "Google sign-in is coming next",
            "Task lists will sync into the local cache",
            "Offline task viewing will use saved data",
        ).forEach { item ->
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = item,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
private fun SettingsScreen() {
    val rows = listOf(
        "Reminder interval" to "Default: 10 minutes",
        "Snooze duration" to "Default: 30 minutes",
        "Quiet hours" to "Off",
        "Theme" to "System default",
    )

    AppScreenScaffold(section = appSectionForRoute(SETTINGS_ROUTE)) {
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                rows.forEach { (title, supportingText) ->
                    ListItem(
                        headlineContent = { Text(title) },
                        supportingContent = { Text(supportingText) },
                    )
                }
            }
        }
    }
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
    }
}
