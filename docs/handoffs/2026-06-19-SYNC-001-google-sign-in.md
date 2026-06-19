# Session Handoff: SYNC-001 Google Sign-In

## Backlog Item

`SYNC-001`

## Branch

`codex/sync-001-google-sign-in`

## Summary

Added the Google Sign-In foundation using Android Credential Manager. The app now has:

- Google Sign-In dependencies.
- `INTERNET` permission.
- Persisted auth session state through DataStore.
- A Credential Manager sign-in/sign-out client.
- Settings-screen UI for signed-out, signed-in, busy, and non-blocking error/setup states.
- Unit tests for auth session labeling and OAuth configuration readiness.
- Setup documentation for the required Google OAuth Web Client ID.

The committed default Web Client ID is blank so no developer credentials are stored in the repo. Until OAuth is configured, tapping `Sign in with Google` shows a setup-needed message instead of crashing.

## Files Changed

- `app/build.gradle.kts`
- `gradle/libs.versions.toml`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/auth/*`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/ui/app/TaskReminderApp.kt`
- `app/src/test/java/com/guptarajat/screenactivetaskreminder/auth/*`
- `docs/GOOGLE_SIGN_IN_SETUP.md`
- `docs/handoffs/2026-06-19-SYNC-001-google-sign-in.md`

## Verification

Commands run:

```powershell
git diff --check
.\gradlew.bat --offline --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:compileDebugKotlin
.\gradlew.bat --offline --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:testDebugUnitTest
.\gradlew.bat --offline --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug
& 'C:\tmp\task-reminder-dev\android-sdk\build-tools\35.0.0\aapt.exe' dump badging 'app\build\outputs\apk\debug\app-debug.apk'
```

Result:

- `git diff --check`: passed.
- `:app:compileDebugKotlin`: passed.
- `:app:testDebugUnitTest`: passed.
- `:app:assembleDebug`: passed.
- APK badging check confirmed package `com.guptarajat.screenactivetaskreminder` and label `Screen Active Task Reminder`.

## Open Questions

- Google OAuth must be configured before real account sign-in can succeed. See `docs/GOOGLE_SIGN_IN_SETUP.md`.

## Known Limitations

- This slice authenticates identity only.
- It does not request Google Tasks scopes or fetch task data. That belongs to `SYNC-002`.

## Recommended Next Step

Take `SYNC-002` for Google Tasks authorization and read-only task-list/task fetch.
