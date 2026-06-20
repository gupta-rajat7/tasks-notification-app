# Privacy Policy Draft

Status: Draft for internal and closed testing. Product owner review is required before publishing.

App name: Screen Active Task Reminder

## Plain-English Summary

Screen Active Task Reminder helps users remember unfinished Google Tasks after active phone use. The V1 app is designed to keep task data on the user's device and does not use a custom backend.

## Data The App Uses

The app may access:

- The Google account selected by the user for sign-in.
- Google Tasks task lists and task details needed to show pending tasks.
- Local reminder settings such as reminder interval, snooze duration, quiet hours, theme, and selected task lists.
- Local notification state needed to show reminder notifications.
- Optional screen-activity diagnostic results if the user opens the diagnostics area.
- Optional derived recent screen-activity state when the user enables screen activity reminders.

## How Data Is Used

Data is used to:

- Show pending tasks in the app.
- Let the user choose which task lists are watched.
- Decide whether a reminder notification should be shown.
- Keep reminder settings after the app closes.
- Help the user test whether optional screen-activity signals may work on the device.
- Suppress or allow reminder notifications based on recent derived screen-activity state if the user enables that optional mode.

## Data Storage

The V1 app stores task cache, sync status, selected task lists, and app settings locally on the Android device.

The V1 app does not send task data to a custom server.

If optional screen activity reminders are enabled, the app checks Android usage events at reminder-check time and keeps only derived reminder state. It should not store raw per-app usage history.

## Google Account And Google Tasks

The app uses Google sign-in so the user can connect their own Google account. Google Tasks access should use the narrow read-only scope needed for task list and pending task sync:

`https://www.googleapis.com/auth/tasks.readonly`

The app should not request broader Google permissions unless the product owner explicitly approves a future feature.

## Notifications

The app may ask for Android notification permission. Notifications are used to remind the user to review unfinished tasks.

## Ads And Analytics

V1 should not include:

- Ads SDKs.
- Third-party analytics SDKs.
- Sale of user data.
- Custom backend data collection.

Any future analytics, ads, or paid feature implementation must update this policy and the Google Play Data safety answers before release.

## User Controls

Users can:

- Skip Google sign-in.
- Sign out from the app.
- Turn reminder settings on or off.
- Keep screen activity reminders off.
- Change quiet hours and reminder timing.
- Turn notification permission off in Android settings.
- Turn Usage Access off in Android settings.
- Clear app data or uninstall the app to remove local app data from the device.

## Children And Sensitive Use

The app is intended as a general productivity tool. It is not designed for children, medical use, financial advice, or workplace surveillance.

## Contact

Product owner must provide a public support email before Play Store submission.

Support email: `TODO`

## Pre-Submission Review Checklist

- Confirm the final app behavior matches this policy.
- Confirm Google Play Data safety answers match this policy.
- Confirm all requested Android permissions are disclosed.
- Confirm OAuth consent-screen disclosures match this policy.
- Replace `TODO` support email.
- Obtain legal review if publishing publicly.

## Official References

- Google Play Data safety: https://support.google.com/googleplay/android-developer/answer/10787469
- Prepare your app for Google Play review: https://support.google.com/googleplay/android-developer/answer/9859455
