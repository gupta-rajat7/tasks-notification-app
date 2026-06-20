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

### REM-004: Automatic Reminder Scheduling

Schedule reminder checks without requiring the user to press Check now.

Acceptance criteria:

- App schedules the next reminder check after startup, settings changes, review, and snooze.
- Scheduled checks reuse `ReminderRules` and `ReminderNotificationCoordinator`.
- Scheduling remains conservative and avoids exact-alarm permission unless explicitly approved.
- Scheduled checks are best-effort under Android background limits; V1 does not guarantee exact delivery at the configured minute.

## P2: Screen Activity Detection

### SCR-001: Usage Access Feasibility Spike

Validate whether optional Android Usage Access can support the product promise of reminders after real phone activity.

Acceptance criteria:

- No Usage Access permission is requested during first-run onboarding.
- Spike checks whether Usage Access is granted.
- Spike can open Android Usage Access settings with a safe fallback if no matching settings activity exists.
- Spike reads recent `UsageStatsManager` events locally on-device.
- Spike verifies whether `SCREEN_INTERACTIVE`, `SCREEN_NON_INTERACTIVE`, `ACTIVITY_RESUMED`, and `ACTIVITY_PAUSED` events are available on the Windows emulator.
- Results are documented with a recommendation: proceed, limit to physical-device testing, or reject.

### SCR-002: Optional Screen Activity Mode

Add user-facing optional screen-activity reminders only if `SCR-001` proves the approach is reliable enough.

Acceptance criteria:

- User can enable or skip the optional mode from Settings.
- The permission explanation appears before Android settings opens.
- The app remains usable without Usage Access.
- Raw per-app usage history is not persisted.
- Derived active-session state feeds the existing reminder engine.
- Play Store disclosure and privacy docs are updated before release.

## P2: Product Polish

### UX-001: Onboarding Flow

Build guided first-run onboarding.

Status: Complete in `docs/handoffs/2026-06-20-UX-001-guided-onboarding.md`.

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

### UX-003: Notification Permission Recovery

Add a Settings recovery surface for users who skipped or denied notification permission.

Status: Complete in `docs/handoffs/2026-06-20-UX-003-notification-permission-recovery.md`.

Acceptance criteria:

- Settings explains whether reminder notifications are enabled.
- User can request notification permission again when Android allows it.
- User can open Android app notification settings when the prompt is unavailable.
- User can recheck notification status after returning to the app.

### UX-004: Manual Sync And Last-Synced Feedback

Make manual Google Tasks sync status clear to users.

Status: Complete in `docs/handoffs/2026-06-20-UX-004-005-sync-feedback-task-list-filtering.md`.

Acceptance criteria:

- Tasks screen shows the manual `Sync now` action.
- Tasks screen shows when Google Tasks last synced successfully.
- Today screen shows cache freshness after a successful sync.
- Sync progress and errors remain non-blocking.

### UX-005: Task-List Filtering

Allow users to choose which synced Google Task lists feed Today and reminders.

Status: Complete in `docs/handoffs/2026-06-20-UX-004-005-sync-feedback-task-list-filtering.md`.

Acceptance criteria:

- Tasks screen shows synced Google Task lists.
- User can include or exclude a synced task list.
- Today and reminder checks use selected lists only.
- Existing task-list selections are preserved across future syncs.

### MON-001: Pro Unlock Design

Document and design the Pro feature gate. Do not implement billing yet.

Acceptance criteria:

- Free and Pro feature matrix exists.
- Upgrade screen mock copy exists.
- No paid code path is added before approval.
