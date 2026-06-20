# Screen Activity Feasibility

## Current Recommendation

Proceed with `SCR-001` as a diagnostic spike only. Do not promote screen-activity detection into the normal reminder engine until the diagnostic has been tested on the Windows emulator and at least one physical Android phone.

Reason: Android Usage Access is the right narrow platform path, but it adds setup friction and may vary by device. The app should remain useful without it.

## What This Spike Adds

- Declares `android.permission.PACKAGE_USAGE_STATS` so Android can show the app in Usage Access settings.
- Adds a Settings-only diagnostics card.
- Checks whether Usage Access is currently granted.
- Opens Android Usage Access settings with a safe fallback if Android has no matching settings screen.
- Reads recent `UsageStatsManager` events locally on-device only after the user taps Scan.
- Counts only the event types needed for feasibility:
  - `SCREEN_INTERACTIVE`
  - `SCREEN_NON_INTERACTIVE`
  - `ACTIVITY_RESUMED`
  - `ACTIVITY_PAUSED`

The spike does not store raw per-app usage history, does not add first-run onboarding prompts, and does not feed screen activity into reminders yet.

## Windows Emulator Test Steps

1. Build and install the debug APK on the emulator.
2. Open the app.
3. Go to Settings.
4. In Screen activity diagnostics, tap Check access.
5. Tap Open Usage Access settings.
6. In Android settings, enable Usage Access for Screen Active Task Reminder.
7. Return to the app.
8. Tap Check access again.
9. Use the emulator normally for a few minutes.
10. Tap Scan recent activity.
11. Record whether the four target event counts are non-zero.

## Result Recording Template

```text
Device:
Android version:
Usage Access enabled: yes/no
Total events:
SCREEN_INTERACTIVE:
SCREEN_NON_INTERACTIVE:
ACTIVITY_RESUMED:
ACTIVITY_PAUSED:
Notes:
Recommendation: proceed / physical-device-only / reject
```

## Emulator Smoke Test Result

Date: 2026-06-20

```text
Device: TaskReminder_API35 emulator, Android SDK built for x86_64
Android version: 15 / API 35
Usage Access enabled: yes, via emulator app-op smoke test
Total events: 198
SCREEN_INTERACTIVE: 1
SCREEN_NON_INTERACTIVE: 0
ACTIVITY_RESUMED: 4
ACTIVITY_PAUSED: 4
Notes: The Settings diagnostics card refreshed from off to enabled after Check access. Scan recent activity returned local UsageStatsManager events and displayed all four target rows. SCREEN_NON_INTERACTIVE stayed at 0 during this short headless emulator run because the screen was not turned off during the test.
Recommendation: proceed to physical-device validation before building SCR-002
```

## Decision Gate For SCR-002

Build `SCR-002: Optional Screen Activity Mode` only if:

- Usage Access can be enabled by a nontechnical tester.
- The target events appear reliably after normal phone use.
- Battery impact remains low.
- Privacy and Play Store disclosure docs are updated before external release.
