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

### Tasks

- Pending tasks display after sync.
- Empty state displays when there are no pending tasks.
- Offline cached tasks display after app restart.
- Manual refresh works.
- Sync error does not crash the app.

### Settings

- Reminder interval saves.
- Snooze duration saves.
- Quiet hours save.
- Theme saves.
- Sign-out works.

### Reminders

- Reminder appears only when pending tasks exist.
- Reminder does not appear during quiet hours.
- Snooze suppresses reminders until snooze expires.
- Review action opens the app.
- Done for now updates review state.

## Automated Tests

Prioritize unit tests for:

- Reminder decision engine.
- Quiet hours boundary handling.
- Snooze expiry.
- Task filtering.
- Repository mapping from remote DTO to Room entity.

Prioritize UI tests later for:

- Onboarding happy path.
- Settings edits.
- Empty state rendering.

## Device Coverage

Minimum manual coverage before beta:

- One Android 13+ phone.
- One Android 11 or 12 phone if available.
- Dark mode.
- Light mode.
- No network after initial sync.

