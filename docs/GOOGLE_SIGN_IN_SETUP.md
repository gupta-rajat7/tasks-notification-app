# Google Sign-In Setup

The app code is wired for Android Credential Manager Sign in with Google. Real account sign-in requires a Google Cloud OAuth client configuration before testing with live Google accounts.

## Current App Identity

- Android package: `com.guptarajat.screenactivetaskreminder`
- Display name: `Screen Active Task Reminder`

## Required OAuth Configuration

Create OAuth clients in Google Cloud for this app:

- Android client for package `com.guptarajat.screenactivetaskreminder`
- Web client used as the `serverClientId` for Credential Manager

Put the Web Client ID into:

```xml
<string name="google_web_client_id" translatable="false">YOUR_WEB_CLIENT_ID.apps.googleusercontent.com</string>
```

The committed default is blank so no developer credentials are stored in the repository. Until this value is configured, the app shows a non-blocking setup message when the user taps `Sign in with Google`.

## Debug SHA-1

For local debug OAuth setup, the Android client needs the debug signing certificate SHA-1. On the development machine, run:

```powershell
& "$env:JAVA_HOME\bin\keytool.exe" -list -v -alias androiddebugkey -keystore "$env:USERPROFILE\.android\debug.keystore" -storepass android -keypass android
```

## Next Slice

`SYNC-002` should add Google Tasks authorization and task-list reads. Sign-in authenticates the user's identity; task data access still needs the Google Tasks scope.
