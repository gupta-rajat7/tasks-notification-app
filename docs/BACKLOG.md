# Backlog

Backlog IDs are stable handles for Codex sessions. A session should claim one small item, implement it, verify it, and leave a handoff note.

## P0: Project Foundation

### INFRA-001: Android 16 Readiness And PO Test Guides

Prepare the app and project documents for Android 16 local development and first-owner testing.

Acceptance criteria:

- App build config targets Android SDK 36.
- Gradle and Android Gradle Plugin versions are compatible with SDK 36.
- Local Windows build commands are documented.
- Product owner can follow a step-by-step Windows/emulator test guide.
- Google Play Store release preparation steps are documented.
- Lightweight UI/UX planning is documented.

### APP-001: Scaffold Android Project

Create a native Android project using Kotlin, Compose, and Material 3.

Acceptance criteria:

- Gradle project exists.
- App module builds.
- MainActivity launches a Compose screen.
- README includes build commands.

### APP-002: Compose App Shell

Create bottom navigation for Today, Tasks, and Settings.

Acceptance criteria:

- Three screens exist.
- Navigation works.
- Theme uses Material 3.
- UI is clean on phone-sized screen.

### APP-003: Settings Store

Add DataStore-backed settings.

Acceptance criteria:

- Reminder interval persists.
- Snooze duration persists.
- Theme setting persists.
- Settings screen can read and write values.

### APP-004: Final App Identity

Replace scaffold placeholder identity with the stable Android app identity needed before Google OAuth and Play Store setup.

Acceptance criteria:

- Android namespace and application ID use the approved production-style package.
- Kotlin source and test packages match the new app identity.
- Display name remains user-facing and clear.
- Architecture docs record the stable package name.

## P1: Task Sync

### SYNC-001: Google Sign-In

Add Google sign-in through Credential Manager.

Acceptance criteria:

- User can start sign-in.
- Auth state is stored safely.
- Sign-out works.
- Error state is visible and non-blocking.

### SYNC-002: Google Tasks API Client

Fetch task lists and pending tasks.

Acceptance criteria:

- App can fetch task lists.
- App can fetch pending tasks.
- API failures are handled gracefully.

### SYNC-003: Room Cache

Persist task lists, tasks, and sync state.

Acceptance criteria:

- Room entities and DAOs exist.
- Repository writes fetched data.
- UI can observe cached tasks.

## P1: Reminder Engine

### REM-001: Reminder Rules

Implement pure Kotlin reminder decision logic.

Acceptance criteria:

- Unit tests cover pending tasks, quiet hours, snooze, and recent review.
- Logic has no Android framework dependency.

### REM-002: Notification Reminder

Post reminder notification with actions.

Acceptance criteria:

- Notification channel exists.
- Notification appears when rule passes.
- Actions include Review, Snooze, and Done for now.

### REM-003: Quiet Hours

Add quiet hours settings and suppression.

Acceptance criteria:

- User can turn quiet hours on and off.
- User can configure start and end.
- Reminder engine respects the setting.

## P2: Product Polish

### UX-001: Onboarding Flow

Build guided first-run onboarding.

Acceptance criteria:

- User can complete setup in under 2 minutes.
- The flow avoids advanced Android permissions.
- Final step lands on Today.

### UX-002: Empty And Error States

Add polished empty states and sync error messaging.

Acceptance criteria:

- No tasks state is clear.
- Offline state is clear.
- Auth failure state is recoverable.

### MON-001: Pro Unlock Design

Document and design the Pro feature gate. Do not implement billing yet.

Acceptance criteria:

- Free and Pro feature matrix exists.
- Upgrade screen mock copy exists.
- No paid code path is added before approval.
