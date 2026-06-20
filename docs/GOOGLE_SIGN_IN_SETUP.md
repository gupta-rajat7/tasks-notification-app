# Google Sign-In Setup

The app code is wired for Android Credential Manager Sign in with Google. Real account sign-in requires a Google Cloud OAuth client configuration before testing with live Google accounts.

## Current App Identity

- Android package: `com.guptarajat.screenactivetaskreminder`
- Display name: `Screen Active Task Reminder`

## Required OAuth Configuration

Create OAuth clients in Google Cloud for this app:

- Android client for package `com.guptarajat.screenactivetaskreminder`
- Web client used as the `serverClientId` for Credential Manager
- Enable the Google Tasks API in the same Google Cloud project.
- Add the Google Tasks read-only OAuth scope to the consent screen:
  `https://www.googleapis.com/auth/tasks.readonly`

The app receives the Web Client ID through Gradle-generated Android resources. Do not commit a real Web Client ID into `strings.xml`.

For local testing, put the Web Client ID into ignored `local.properties`:

```properties
google.web.client.id=YOUR_WEB_CLIENT_ID.apps.googleusercontent.com
```

Gradle generates the runtime string resource used by the app:

```xml
<string name="google_web_client_id" translatable="false">YOUR_WEB_CLIENT_ID.apps.googleusercontent.com</string>
```

The committed default is blank so no developer credentials are stored in the repository. Until this value is configured, the app shows a non-blocking setup message when the user taps `Sign in with Google`.

You can also set it for one PowerShell session:

```powershell
$env:GOOGLE_WEB_CLIENT_ID='YOUR_WEB_CLIENT_ID.apps.googleusercontent.com'
```

See `docs/PO_GOOGLE_OAUTH_SETUP_GUIDE.md` for the product-owner setup path.

## Debug SHA-1

For local debug OAuth setup, the Android client needs the debug signing certificate SHA-1. On the development machine, run:

```powershell
& "$env:JAVA_HOME\bin\keytool.exe" -list -v -alias androiddebugkey -keystore "$env:USERPROFILE\.android\debug.keystore" -storepass android -keypass android
```

## Next Slice

`SYNC-002` adds Google Tasks authorization and read-only task-list reads. Sign-in authenticates the user's identity; task data sync asks for the Google Tasks read-only scope when the user taps `Sync now`.

The app does not store Google access tokens. It asks Google Play services for a short-lived token during sync and stores only the normalized task cache and sync status locally.

## Official Reference

- Android Credential Manager Sign in with Google: https://developer.android.com/identity/sign-in/credential-manager-siwg
