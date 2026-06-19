# Google Play Store Release Guide

This guide explains what must be ready before the app can be hosted and managed on Google Play.

## Current Release Position

The app is not ready for public Play Store release yet. The right next release target is internal testing, then closed testing, then production after real tester feedback.

## Accounts And Ownership

The product owner needs:

- A Google Play Console developer account.
- Access to the Google Cloud project used for OAuth.
- A public support email address.
- A privacy policy URL before external testing.

## Technical Release Checklist

- App package remains stable: `com.guptarajat.screenactivetaskreminder`.
- `versionCode` is increased for every Play upload.
- `versionName` matches the planned release label.
- Release build signs an Android App Bundle.
- Google Play App Signing is enabled.
- App targets the current supported Android SDK.
- Privacy policy and Data safety answers match the actual app behavior.

## Build Artifact For Play

Play Store uploads should use an Android App Bundle, not the debug APK.

Developer command:

```powershell
$env:JAVA_HOME='C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10'
$env:ANDROID_SDK_ROOT='C:\tmp\task-reminder-dev\android-sdk'
$env:GRADLE_USER_HOME='C:\tmp\task-reminder-dev\gradle-home'
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:bundleRelease
```

This command will require release signing configuration before it can produce a Play-ready bundle.

## Play Console Setup Steps

1. Create the app in Play Console.
2. Choose app name, default language, app/game category, and free/paid status.
3. Complete App content sections:
   - Privacy policy.
   - Data safety.
   - Ads declaration.
   - App access.
   - Target audience.
   - Content rating.
4. Upload screenshots and store listing copy.
5. Upload the signed app bundle to Internal testing.
6. Add internal testers by email.
7. Validate install, sign-in, notification permission, and reminder behavior.
8. Move to Closed testing after internal testing is stable.
9. Move to Production only after onboarding, reminders, and Google Tasks sync are reliable.

## Data Safety Draft Direction

Expected direction for V1:

- No custom backend.
- Google Tasks data is read for app functionality.
- Data is cached locally on the device.
- No ads SDK.
- No analytics SDK unless separately approved.
- No sale of user data.

Final Data safety answers must be reviewed against the exact app implementation before submission.

## Monetization Direction

Do not add ads in V1. The app promise is to reduce distraction, so ads create product trust risk.

Preferred path:

- Free V1 for validation.
- Later one-time Pro unlock for advanced reminder controls.
- Implement Play Billing only after users confirm the free app is useful.

## Release Governance

Every release should have:

- Release branch.
- Version bump.
- Build and unit test evidence.
- Manual smoke test evidence on emulator or phone.
- Handoff note under `docs/handoffs/`.
- Product owner approval before public release.
