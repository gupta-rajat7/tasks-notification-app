# Architecture

## Architecture Style

Local-first native Android app with Google Tasks as the cloud source of truth.

The app should render from local state first, then sync in the background. The user experience should not block on network calls after first setup.

## Recommended Stack

- Kotlin.
- Jetpack Compose.
- Material 3.
- Coroutines and Flow.
- Room for cached task data.
- DataStore for settings.
- WorkManager for periodic sync.
- Credential Manager for Google sign-in.
- Google Tasks API.
- Android notifications.

## Android Platform Target

- Minimum SDK: 26.
- Compile SDK: 36.
- Target SDK: 36.
- Build baseline: JDK 17, Gradle 8.13, Android Gradle Plugin 8.13.x.

The project should stay compatible with current Google Play target API expectations and should be rechecked whenever Android or Play Store requirements change.

## Package Layout

Recommended starting layout inside the Android app module:

```text
com.guptarajat.screenactivetaskreminder
  app
  auth
  data
    local
    remote
    repository
  reminders
  settings
  sync
  ui
    onboarding
    today
    tasks
    settings
  util
```

## Data Model

### Room Entities

`TaskListEntity`

- `id`
- `title`
- `updatedAt`
- `isSelected`

`TaskEntity`

- `id`
- `taskListId`
- `title`
- `notes`
- `status`
- `dueAt`
- `completedAt`
- `updatedAt`
- `position`

`SyncStateEntity`

- `accountId`
- `lastFullSyncAt`
- `lastSuccessfulSyncAt`
- `lastError`

### DataStore Settings

- `reminderIntervalMinutes`
- `snoozeMinutes`
- `quietHoursEnabled`
- `quietHoursStart`
- `quietHoursEnd`
- `themeMode`
- `selectedTaskListIds`
- `notificationsEnabledByApp`
- `lastReviewAt`
- `lastSnoozeUntil`

## Core Flows

### Onboarding Flow

1. User opens app.
2. App checks auth state.
3. User signs in with Google.
4. App requests notification permission.
5. App performs first task sync.
6. App writes default settings.
7. User lands on Today.

### Reminder Decision Flow

1. App startup, reminder settings changes, task sync, review, snooze, and notification actions ask `ReminderScheduler` to schedule the next check.
2. `ReminderScheduler` calculates a conservative delay from DataStore settings, quiet hours, snooze state, and last review time.
3. Android WorkManager runs `ReminderCheckWorker` as best-effort background work.
4. `ReminderCheckWorker` calls `ReminderNotificationCoordinator.evaluateAndNotify`.
5. App reads settings from DataStore.
6. App checks quiet hours and snooze state.
7. App queries Room for pending tasks.
8. App checks last review time.
9. App posts notification if all rules pass.
10. Worker schedules the next follow-up check.

`ReminderRules` stays pure Kotlin. Android-specific notification work lives in `ReminderNotificationCoordinator`, and notification actions are handled by `ReminderNotificationActionReceiver`.

Current V1 notification actions:

- Review opens the app.
- Snooze stores `snoozedUntilMillis` in DataStore and cancels the visible notification.
- Done for now stores `lastReviewedAtMillis` in DataStore and cancels the visible notification.

Quiet-hours settings are stored in DataStore as:

- `quietHoursEnabled`.
- `quietHoursStartMinuteOfDay`.
- `quietHoursEndMinuteOfDay`.

The Settings screen configures quiet-hours start and end in hourly steps. The reminder coordinator maps these values into the pure Kotlin `QuietHours` rule model before posting a notification.

Automatic reminder scheduling uses unique one-time WorkManager work rather than exact alarms. The app does not request exact-alarm permission, battery-exemption permission, Usage Access, AccessibilityService, or overlay permission for V1 reminder scheduling. Timing is best-effort under Android background limits and may be delayed by Doze, app standby, battery saver, or force-stop.

V1 reminder timing is based on elapsed review state, pending cached tasks, snooze, quiet hours, and Android background scheduling. It does not measure real cross-app screen activity.

### Sync Flow

1. User starts sync from the Tasks screen.
2. Remote Google Tasks API client fetches task lists and tasks.
3. Repository maps remote data into Room entities.
4. UI observes Room and updates automatically.
5. Sync errors are stored and shown non-blockingly.

## Permissions

V1 should require only:

- Internet.
- Notification permission on supported Android versions.

Avoid V1 use of:

- AccessibilityService.
- Usage Access.
- Overlay permission.
- Ignore battery optimizations.

These permissions increase setup friction and store-policy risk.

## Performance Principles

- Render UI from Room and DataStore flows.
- Do network work off the main thread.
- Use conservative sync intervals.
- Avoid foreground service unless there is a clear user-visible active operation.
- Keep notification logic simple and testable.

## Privacy Principles

- Store task cache locally.
- Do not run a custom backend.
- Do not sell or share task data.
- Do not collect analytics in V1.
- Do not add ad SDKs in V1.

## Future Architecture Options

V2 may evaluate optional Usage Access for stronger screen-use detection. If approved, the implementation should live behind an explicit Settings or onboarding-later screen and use `UsageStatsManager` locally on-device. It should reuse `ReminderRules`, `ReminderNotificationCoordinator`, and `ReminderScheduler` rather than creating a parallel reminder engine.

AccessibilityService should be a last resort. If added, it needs prominent disclosure, explicit consent, a narrow purpose, and Play Console declaration handling. See `docs/adr/0005-optional-usage-access-for-screen-activity.md`.

