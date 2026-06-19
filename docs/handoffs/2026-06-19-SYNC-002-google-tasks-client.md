# SYNC-002 Google Tasks API Client

## Status

Complete on branch `codex/sync-002-google-tasks-client`.

## What Changed

- Added Google Play Services Auth as an explicit app dependency for Google Tasks authorization.
- Added a read-only Google Tasks authorization wrapper for `https://www.googleapis.com/auth/tasks.readonly`.
- Added a lightweight Google Tasks REST client that fetches task lists and pending tasks.
- Normalized Google task-list and task data into the existing Room cache models.
- Added Tasks screen `Sync now` UI with signed-in gating, progress state, recoverable status messages, and persisted last-sync error display.
- Added repository support for recording sync errors without deleting the last successful cache.
- Updated Google setup documentation with Google Tasks API and OAuth scope requirements.

## Product Decisions

- The first sync implementation requests read-only Google Tasks access only.
- The app does not store Google access tokens; it requests a short-lived token when the user taps `Sync now`.
- Completed and deleted Google Tasks are filtered out because the current product goal is active-task reminders, not full task history.

## Verification

- `git diff --check`: passed.
- `.\gradlew.bat --offline --no-daemon --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:compileDebugKotlin`: passed.
- `.\gradlew.bat --offline --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:testDebugUnitTest`: passed.
- `.\gradlew.bat --offline --no-daemon --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug`: passed.

## Notes For Next Session

- Live sync still requires Google Cloud setup: Web Client ID in `strings.xml`, Android OAuth client SHA-1, Google Tasks API enabled, and OAuth consent scope approval.
- Next high-priority product slice is `REM-001`: pure Kotlin reminder rules for when the app should remind the user about pending tasks.
