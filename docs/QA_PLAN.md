# QA Plan

## V1 Quality Goals

- Onboarding is understandable for a first-time user.
- App feels fast on ordinary Android phones.
- Reminder behavior is predictable.
- Notifications are not spammy.
- App is usable offline after first sync.

## Manual Test Checklist

### Onboarding

- Fresh install opens welcome flow.
- Google sign-in starts correctly.
- Notification permission is requested at the right moment.
- User lands on Today after setup.
- User can recover from skipping notification permission.
- User can tap Set up later and still land on Today.
- Relaunch after onboarding completion skips the welcome flow.

### Tasks

- Pending tasks display after sync.
- Empty state displays when there are no pending tasks.
- Offline cached tasks display after app restart.
- Manual refresh works.
- Sync error does not crash the app.

### Settings

- Reminder interval saves.
- Snooze duration saves.
- Quiet hours can be turned on and off.
- Quiet hours start and end times save.
- Theme saves.
- Sign-out works.

### Reminders

- Reminder appears only when pending tasks exist.
- Reminder does not appear during quiet hours.
- Today > Check now reports quiet-hours suppression when quiet hours are active.
- Today > Enable requests or confirms notification permission.
- Today > Check now posts a reminder notification when cached pending tasks are eligible.
- App schedules automatic reminder checks after startup, reminder setting changes, review, and snooze.
- Automatic reminder checks reuse the same quiet-hours, snooze, review, and pending-task rules as Today > Check now.
- Automatic reminder timing is best-effort; Android may delay checks while the app is idle, battery saver is active, or the app was force-stopped.
- V1 reminders do not measure real cross-app screen activity yet.
- Settings > Screen activity diagnostics can check Usage Access and scan recent target event counts.
- Notification contains Review, Snooze, and Done for now actions.
- Snooze suppresses reminders until snooze expires.
- Review action opens the app.
- Done for now updates review state.

## Automated Tests

Prioritize unit tests for:

- Reminder decision engine.
- Automatic reminder delay calculation.
- Quiet hours boundary handling.
- Snooze expiry.
- Task filtering.
- Repository mapping from remote DTO to Room entity.

Prioritize UI tests later for:

- Onboarding happy path.
- Settings edits.
- Empty state rendering.
- Optional Usage Access explanation and decline path if screen-activity mode is approved later.
- `SCR-001` emulator scan from `docs/SCREEN_ACTIVITY_FEASIBILITY.md`.

## UX Empty And Error States

Manual checks for `UX-002`:

- Fresh install, not signed in: Today shows the Google Tasks setup card; Tasks shows the sign-in empty state.
- Signed in, never synced: Today points to Tasks; Tasks shows the first-sync empty state.
- Signed in, sync failed: Tasks shows a recoverable sync-error card with Try again.
- Synced with zero pending tasks: Today and Tasks show no-pending task copy instead of a blank list.
- Synced with pending tasks: task cards render without the empty-state cards.

## Device Coverage

Minimum manual coverage before beta:

- One Android 16 emulator or phone.
- One Android 13+ phone.
- One Android 11 or 12 phone if available.
- Dark mode.
- Light mode.
- No network after initial sync.

For product-owner local testing on Windows, use `docs/PO_WINDOWS_TEST_GUIDE.md`.
