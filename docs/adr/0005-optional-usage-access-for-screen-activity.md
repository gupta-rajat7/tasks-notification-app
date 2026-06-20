# ADR-0005: Treat Real Screen Activity As Optional Usage Access Mode

## Status

Accepted.

## Context

The product idea includes reminders after the user has been active on the phone for a configurable period. The original V1 implementation used pending tasks, review cooldown, snooze, quiet hours, notifications, and WorkManager scheduling without detecting real cross-app screen activity. `SCR-002` later added optional Settings-only screen-activity reminders using derived Usage Access evidence.

Android does not provide a low-friction normal permission for an app to continuously monitor cross-app activity. The realistic platform option is Usage Access through `PACKAGE_USAGE_STATS` and `UsageStatsManager`. Android documents that most `UsageStatsManager` methods require `PACKAGE_USAGE_STATS`, and the user must still grant access through Settings. Google Play treats restricted and special permissions as sensitive and expects clear user benefit, minimum scope, and consented use. AccessibilityService is higher-risk for this product because Google Play says apps that are not accessibility tools need prominent disclosure, consent, and declaration handling, and should use narrower APIs when possible.

References:

- Android `UsageStatsManager`: https://developer.android.com/reference/android/app/usage/UsageStatsManager
- Android `PACKAGE_USAGE_STATS`: https://developer.android.com/reference/android/Manifest.permission#PACKAGE_USAGE_STATS
- Android Usage Access settings action: https://developer.android.com/reference/android/provider/Settings#ACTION_USAGE_ACCESS_SETTINGS
- Google Play permissions and sensitive APIs policy: https://support.google.com/googleplay/android-developer/answer/16558241
- Google Play AccessibilityService policy: https://support.google.com/googleplay/android-developer/answer/10964491

## Decision

V1 remains notification-first and elapsed-review-interval based by default. It must not request Usage Access during first-run onboarding.

Real screen-activity detection is allowed only as an optional advanced mode using Usage Access. The user must be able to keep using the app without granting Usage Access.

The Usage Access mode should:

- Ask for permission only from an explicit screen-activity feature screen, not at first launch.
- Explain that Android will open a system settings page.
- Use `UsageStatsManager` locally on-device.
- Prefer device interactive/session signals over app-inventory or per-app behavior tracking.
- Avoid storing package names unless a future user-facing feature explicitly requires them.
- Store only derived reminder state, such as last active-session threshold crossing.
- Reuse existing `ReminderRules`, `ReminderNotificationCoordinator`, and `ReminderScheduler`.

AccessibilityService is rejected as a default path. It can be reconsidered only if the app becomes a true accessibility tool or if a later approved feature cannot be built with narrower APIs.

## Implementation Update

`SCR-002` implements the approved path as an optional Settings-only mode. The setting is off by default, the user can open Android Usage Access settings from the app, and reminders use only derived recent-activity state. Physical-device validation is still required before external beta because emulator behavior can differ from real phones.

## Consequences

Positive:

- Preserves easy onboarding for the first release.
- Keeps V1 aligned with Play Store privacy expectations.
- Avoids adding a high-friction permission before the product value is proven.
- Gives future Codex sessions a clear technical boundary for optional screen-activity detection.

Tradeoffs:

- Standard V1 reminders cannot honestly claim exact "screen active for 10 minutes" behavior.
- Optional Usage Access will add setup friction for users who want stronger detection.
- Device manufacturers may vary in how reliably usage events are retained or exposed.
- The optional implementation must include physical-device testing before beta.
