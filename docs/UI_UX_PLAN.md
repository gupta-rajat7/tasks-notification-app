# Lightweight UI/UX Plan

The app should feel calm, fast, and modern without becoming visually heavy.

## Product Experience Principles

- Reminder-first, not dashboard-first.
- Minimal setup before first value.
- Android-native patterns through Compose and Material 3.
- Clear settings with sensible defaults.
- No ad surfaces in the core flow.
- No aggressive overlays in V1.

## Primary Screens

### Today

Purpose: show the user what needs attention now.

Planned elements:

- Pending task summary.
- Next reminder status.
- Review action.
- Empty state when no pending tasks exist.
- Sync status only when useful.

### Tasks

Purpose: let the user scan Google Tasks content.

Planned elements:

- Task list selector.
- Pending task list.
- Due date or overdue indicator.
- Manual refresh.
- Recoverable sync error state.

### Settings

Purpose: make reminder behavior configurable without clutter.

Planned elements:

- Reminder interval.
- Snooze duration.
- Quiet hours.
- Theme mode.
- Google account status.
- Notification permission recovery.

## Onboarding Plan

Target: user completes setup in under 2 minutes.

Steps:

1. Welcome and product promise.
2. Google sign-in.
3. Notification permission.
4. Default reminder settings confirmation.
5. Land on Today.

Current implementation: `UX-001` uses a skippable first-run flow gated by DataStore. Google sign-in and notifications are encouraged but not forced, so beta testers can still enter the app before OAuth is fully configured.

## Visual Direction

- Use Material 3 components.
- Prefer clear spacing and readable type.
- Keep cards for repeated task items or contained settings groups only.
- Avoid marketing-style hero pages inside the app.
- Avoid decorative gradients or heavy illustration in the core product.
- Use icons in navigation and action buttons where they improve scanning.

## UX Backlog

- `UX-001`: Build guided first-run onboarding. Implemented as a skippable first-run flow with Google Tasks setup, notification setup, and default reminder confirmation.
- `UX-002`: Add polished empty and error states. Implemented for Today and Tasks with sign-in, first-sync, all-caught-up, and recoverable sync-error states.
- `UX-003`: Add notification permission recovery screen. Implemented in Settings with status, Enable, Open Android settings, and Check status actions.
- `UX-004`: Add manual sync and last synced feedback.
- `UX-005`: Add task-list filtering.

## UX QA Checklist

- Text remains readable on small phone screens.
- App works in light and dark mode.
- Empty states tell the user what happened and what to do next.
- Settings changes are easy to understand.
- Loading and sync states do not block navigation.
- Error messages avoid technical terms unless the user can act on them.
