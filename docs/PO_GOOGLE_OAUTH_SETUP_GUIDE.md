# Product Owner Google OAuth Setup Guide

This guide explains why Google sign-in currently shows a setup message and what must be done before real Google accounts can connect.

## Current Status

The app code is already wired for Android Credential Manager Sign in with Google. Real sign-in is blocked because the Google Cloud OAuth clients and Web Client ID have not been configured for this app yet.

This is intentional. Google IDs and credentials should not be committed to GitHub.

## What You Need To Create

In Google Cloud Console, create or select one project for this app.

Required configuration:

- Enable the Google Tasks API.
- Configure the OAuth consent screen.
- Add the Google Tasks read-only scope: `https://www.googleapis.com/auth/tasks.readonly`
- Create an Android OAuth client.
- Create a Web OAuth client.

Current app identity:

- App name: `Screen Active Task Reminder`
- Android package: `com.guptarajat.screenactivetaskreminder`

## Information The Developer Needs

Give Codex or a developer these values after the Google Cloud project is ready:

- Google Cloud project name.
- Confirmation that Google Tasks API is enabled.
- Confirmation that the Android OAuth client uses package `com.guptarajat.screenactivetaskreminder`.
- Debug SHA-1 used for local Windows testing.
- Web Client ID ending in `.apps.googleusercontent.com`.

Do not share OAuth client secrets. This Android app needs the Web Client ID, not a Web Client Secret.

## Debug SHA-1 For This Windows Machine

Run this in PowerShell:

```powershell
cd 'C:\Users\Tanu Gupta\Documents\Tasks Notification App'
powershell -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -PrintDebugSha1
```

Copy the printed SHA-1 value into the Android OAuth client in Google Cloud Console.

## Local App Configuration

The app reads the Google Web Client ID from local-only configuration.

After creating the Web OAuth client, run this in PowerShell:

```powershell
cd 'C:\Users\Tanu Gupta\Documents\Tasks Notification App'
powershell -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -WebClientId 'YOUR_WEB_CLIENT_ID.apps.googleusercontent.com'
```

The setup helper validates the ID format and saves this local-only line to `local.properties`:

```properties
google.web.client.id=YOUR_WEB_CLIENT_ID.apps.googleusercontent.com
```

`local.properties` is ignored by Git, so this value will stay local and will not be pushed to GitHub.

Alternative for one PowerShell session:

```powershell
$env:GOOGLE_WEB_CLIENT_ID='YOUR_WEB_CLIENT_ID.apps.googleusercontent.com'
```

After adding the value, rebuild and reinstall the app:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\run_app_windows.ps1 -StartEmulator
```

## What The App Should Do After Setup

- Tapping `Sign in with Google` should open the Google sign-in flow.
- The user should be able to choose a Google account.
- The app should return to Settings with the signed-in account shown.
- Tasks sync can then request Google Tasks read-only access when the user taps `Sync now`.

## If It Still Fails

Check these first:

- The package name in Google Cloud exactly matches `com.guptarajat.screenactivetaskreminder`.
- The debug SHA-1 in Google Cloud matches this Windows machine.
- The Web Client ID was added to `local.properties`.
- The Google Tasks API is enabled in the same Google Cloud project.
- The test Google account is allowed on the OAuth consent screen if the app is still in testing mode.

## Official Reference

- Android Credential Manager Sign in with Google: https://developer.android.com/identity/sign-in/credential-manager-siwg
