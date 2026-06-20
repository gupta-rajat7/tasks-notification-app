# Store Listing Draft

Status: Draft for internal testing and future closed testing. Do not publish publicly without product owner approval.

## App Name

Screen Active Task Reminder

## Short Description

Gentle reminders to review unfinished Google Tasks after active phone use.

## Full Description

Screen Active Task Reminder helps you return to unfinished tasks when your phone use starts to pull attention away from the work you meant to complete.

Connect Google Tasks, choose which task lists matter, and let the app remind you to review pending tasks with simple Android notifications. The app is designed to be lightweight, calm, and privacy-conscious.

V1 focuses on:

- Google Tasks sign-in and read-only task sync.
- Watched task-list selection.
- Reminder interval, snooze, and quiet-hours controls.
- Notification permission recovery from Settings.
- A clean Today, Tasks, and Settings experience.

The app does not include ads in V1.

## Feature Bullets

- Review pending Google Tasks from a focused Today screen.
- Manually sync tasks when you want fresh data.
- Choose which Google Task lists are watched.
- Configure reminder interval, snooze duration, and quiet hours.
- Use notification recovery if Android notification permission was skipped or denied.
- Keep data cached locally on the device.

## What To Avoid Claiming In V1

Do not claim:

- Exact real-time reminders.
- Guaranteed background delivery at the exact configured minute.
- Full screen-time monitoring.
- Cross-app activity blocking.
- Task editing in Google Tasks.
- Cloud backup by this app.
- Ads-free forever.

## Screenshots Needed

Prepare screenshots for:

- Guided setup.
- Today screen with pending tasks.
- Tasks screen with watched task-list toggles.
- Settings screen with reminder controls.
- Notification permission recovery.
- Example reminder notification.

## App Access Notes For Play Review

If Google sign-in is required for review, provide a tester Google account or clear review instructions in Play Console.

Internal testing can proceed before public OAuth verification, but external testers must be covered by the OAuth consent screen setup and privacy disclosures.

## Current Release Recommendation

Use this copy for internal testing only after:

- Google OAuth is configured.
- Privacy policy URL is available.
- A signed Android App Bundle can be uploaded.
- Manual smoke testing passes on emulator and one real Android phone.

## Official References

- Prepare your app for Google Play review: https://support.google.com/googleplay/android-developer/answer/9859455
- Google Play Data safety: https://support.google.com/googleplay/android-developer/answer/10787469
