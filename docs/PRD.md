# Product Requirements

## Product Objective

Build a lightweight Android app that helps users review pending Google Tasks after a configurable period of phone activity or elapsed review time.

## V1 Scope

### Onboarding

The first-run flow must be short:

1. Welcome screen with clear value proposition.
2. Sign in with Google.
3. Grant notification permission.
4. Select task list or use default.
5. Choose reminder interval.
6. Land on the Today screen.

Avoid advanced permissions in first-run onboarding unless a V1 feature cannot work without them.

### Core Screens

#### Today

- Show pending task count.
- Show top pending tasks.
- Show next reminder status.
- Provide quick actions: Review now, Snooze, Sync.

#### Tasks

- List pending Google Tasks.
- Support list filter.
- Show due date when available.
- Show completed state if synced.

#### Settings

- Reminder interval.
- Snooze duration.
- Quiet hours.
- Notification style.
- Theme.
- Task list selection.
- Sync frequency.
- Account sign-out.

### Reminder Behavior

The app should remind only when:

- User is onboarded.
- Notifications are allowed.
- At least one pending task exists.
- Current time is outside quiet hours.
- Reminder cooldown has elapsed.
- User has not recently opened the task review screen.

V1 should use Android notifications as the reminder mechanism.

Notification actions:

- Review.
- Snooze.
- Done for now.

### Google Tasks Sync

The app should:

- Authenticate with the user's Google account.
- Read task lists and tasks.
- Cache relevant task data locally.
- Sync periodically in the background.
- Let the user manually refresh.
- Handle offline mode by showing cached data.

Write operations are optional for V1. If implemented, they must use Google Tasks as the source of truth.

### Settings

Default settings:

- Reminder interval: 10 minutes.
- Snooze duration: 30 minutes.
- Quiet hours: off.
- Sync frequency: conservative background sync plus manual refresh.
- Theme: system default.

### Monetization

V1 should launch free and without ads.

Paid features should be hidden behind a future Pro unlock only after the free experience proves useful.

## V1 Acceptance Criteria

- A new user can install, open, sign in, grant notification permission, and see Google Tasks.
- Pending tasks load from local cache after first sync.
- Reminder notification appears when the reminder rules are satisfied.
- Notification actions work.
- Settings persist after app restart.
- The app remains usable offline after at least one successful sync.
- No full-screen ad, interstitial ad, or forced overlay exists in V1.

## Out Of Scope Until V2

- AccessibilityService.
- Usage Access.
- Overlay permission.
- Paid Pro implementation.
- Ads.
- Widgets.
- Wear OS.
- Multi-account support.

