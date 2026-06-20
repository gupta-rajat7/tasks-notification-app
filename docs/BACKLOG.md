# Backlog

Backlog IDs are stable handles for Codex sessions. A session should claim one small item, implement it, verify it, and leave a handoff note.

## P0: Project Foundation

### INFRA-001: Android 16 Readiness And PO Test Guides

Prepare the app and project documents for Android 16 local development and first-owner testing.

Status: Complete in `docs/handoffs/2026-06-19-INFRA-001-android-16-readiness.md`.

Acceptance criteria:

- App build config targets Android SDK 36.
- Gradle and Android Gradle Plugin versions are compatible with SDK 36.
- Local Windows build commands are documented.
- Product owner can follow a step-by-step Windows/emulator test guide.
- Google Play Store release preparation steps are documented.
- Lightweight UI/UX planning is documented.

### APP-001: Scaffold Android Project

Create a native Android project using Kotlin, Compose, and Material 3.

Status: Complete in `docs/handoffs/2026-06-19-APP-001-android-scaffold.md`.

Acceptance criteria:

- Gradle project exists.
- App module builds.
- MainActivity launches a Compose screen.
- README includes build commands.

### APP-002: Compose App Shell

Create bottom navigation for Today, Tasks, and Settings.

Status: Complete in `docs/handoffs/2026-06-19-APP-002-compose-shell.md`.

Acceptance criteria:

- Three screens exist.
- Navigation works.
- Theme uses Material 3.
- UI is clean on phone-sized screen.

### APP-003: Settings Store

Add DataStore-backed settings.

Status: Complete in `docs/handoffs/2026-06-19-APP-003-settings-store.md`.

Acceptance criteria:

- Reminder interval persists.
- Snooze duration persists.
- Theme setting persists.
- Settings screen can read and write values.

### APP-004: Final App Identity

Replace scaffold placeholder identity with the stable Android app identity needed before Google OAuth and Play Store setup.

Status: Complete in `docs/handoffs/2026-06-19-APP-004-final-app-identity.md`.

Acceptance criteria:

- Android namespace and application ID use the approved production-style package.
- Kotlin source and test packages match the new app identity.
- Display name remains user-facing and clear.
- Architecture docs record the stable package name.

## P1: Task Sync

### SYNC-001: Google Sign-In

Add Google sign-in through Credential Manager.

Status: Complete in `docs/handoffs/2026-06-19-SYNC-001-google-sign-in.md`.

Acceptance criteria:

- User can start sign-in.
- Auth state is stored safely.
- Sign-out works.
- Error state is visible and non-blocking.

### SYNC-002: Google Tasks API Client

Fetch task lists and pending tasks.

Status: Complete in `docs/handoffs/2026-06-19-SYNC-002-google-tasks-client.md`.

Acceptance criteria:

- App can fetch task lists.
- App can fetch pending tasks.
- API failures are handled gracefully.

### SYNC-003: Room Cache

Persist task lists, tasks, and sync state.

Status: Complete in `docs/handoffs/2026-06-19-SYNC-003-room-cache.md`.

Acceptance criteria:

- Room entities and DAOs exist.
- Repository writes fetched data.
- UI can observe cached tasks.

## P1: Reminder Engine

### REM-001: Reminder Rules

Implement pure Kotlin reminder decision logic.

Status: Complete in `docs/handoffs/2026-06-19-REM-001-reminder-rules.md`.

Acceptance criteria:

- Unit tests cover pending tasks, quiet hours, snooze, and recent review.
- Logic has no Android framework dependency.

### REM-002: Notification Reminder

Post reminder notification with actions.

Status: Complete in `docs/handoffs/2026-06-20-REM-002-notification-reminder.md`.

Acceptance criteria:

- Notification channel exists.
- Notification appears when rule passes.
- Actions include Review, Snooze, and Done for now.

### REM-003: Quiet Hours

Add quiet hours settings and suppression.

Status: Complete in `docs/handoffs/2026-06-20-REM-003-quiet-hours-settings.md`.

Acceptance criteria:

- User can turn quiet hours on and off.
- User can configure start and end.
- Reminder engine respects the setting.

### REM-004: Automatic Reminder Scheduling

Schedule reminder checks without requiring the user to press Check now.

Status: Complete in `docs/handoffs/2026-06-20-REM-004-automatic-reminder-scheduling.md`.

Acceptance criteria:

- App schedules the next reminder check after startup, settings changes, review, and snooze.
- Scheduled checks reuse `ReminderRules` and `ReminderNotificationCoordinator`.
- Scheduling remains conservative and avoids exact-alarm permission unless explicitly approved.
- Scheduled checks are best-effort under Android background limits; V1 does not guarantee exact delivery at the configured minute.

## P2: Screen Activity Detection

### SCR-001: Usage Access Feasibility Spike

Validate whether optional Android Usage Access can support the product promise of reminders after real phone activity.

Status: Complete in `docs/handoffs/2026-06-20-SCR-001-usage-access-feasibility-spike.md`.

Acceptance criteria:

- No Usage Access permission is requested during first-run onboarding.
- Spike checks whether Usage Access is granted.
- Spike can open Android Usage Access settings with a safe fallback if no matching settings activity exists.
- Spike reads recent `UsageStatsManager` events locally on-device.
- Spike verifies whether `SCREEN_INTERACTIVE`, `SCREEN_NON_INTERACTIVE`, `ACTIVITY_RESUMED`, and `ACTIVITY_PAUSED` events are available on the Windows emulator.
- Results are documented with a recommendation: proceed, limit to physical-device testing, or reject.

### SCR-002: Optional Screen Activity Mode

Add user-facing optional screen-activity reminders only if `SCR-001` proves the approach is reliable enough.

Status: Complete in `docs/handoffs/2026-06-20-SCR-002-optional-screen-activity-mode.md`.

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

Status: Complete in `docs/handoffs/2026-06-20-UX-002-empty-error-states.md`.

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

Status: Complete in `docs/handoffs/2026-06-20-MON-001-oauth-privacy-store-readiness.md`.

Acceptance criteria:

- Free and Pro feature matrix exists.
- Upgrade screen mock copy exists.
- No paid code path is added before approval.

### REL-001: OAuth, Privacy, And Store Readiness Docs

Document the owner-facing setup and release-readiness items needed before internal and closed testing.

Status: Complete in `docs/handoffs/2026-06-20-MON-001-oauth-privacy-store-readiness.md`.

Acceptance criteria:

- Product owner has a plain-English Google OAuth setup guide.
- Privacy policy draft exists for review.
- Store listing draft exists for internal testing.
- Performance test guidance separates emulator slowness from app slowness.
- Play Store guide links to the supporting release documents.
