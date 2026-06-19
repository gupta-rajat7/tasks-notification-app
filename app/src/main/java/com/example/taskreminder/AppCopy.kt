package com.example.taskreminder

fun appName(): String = "Screen Active Task Reminder"

const val TODAY_ROUTE = "today"
const val TASKS_ROUTE = "tasks"
const val SETTINGS_ROUTE = "settings"

data class AppSectionCopy(
    val route: String,
    val label: String,
    val headline: String,
    val body: String,
)

fun appSections(): List<AppSectionCopy> = listOf(
    AppSectionCopy(
        route = TODAY_ROUTE,
        label = "Today",
        headline = "Review what matters now",
        body = "Pending tasks and reminder status will appear here.",
    ),
    AppSectionCopy(
        route = TASKS_ROUTE,
        label = "Tasks",
        headline = "Pending Google Tasks",
        body = "Synced task lists will appear here after Google Tasks is connected.",
    ),
    AppSectionCopy(
        route = SETTINGS_ROUTE,
        label = "Settings",
        headline = "Tune the reminder rhythm",
        body = "Reminder intervals, quiet hours, snooze, and theme controls will live here.",
    ),
)

fun appSectionForRoute(route: String): AppSectionCopy =
    appSections().firstOrNull { it.route == route } ?: appSections().first()
