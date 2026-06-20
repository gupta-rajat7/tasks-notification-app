# REM-003 Quiet Hours Settings

## Backlog Item

`REM-003`

## Branch

`codex/rem-003-quiet-hours-settings`

## Summary

Added user-facing quiet-hours settings and wired them into reminder notification evaluation.

What changed:

- Added quiet-hours enabled/start/end values to `TaskReminderSettings`.
- Persisted quiet-hours settings in DataStore.
- Added quiet-hours toggle plus start/end hourly steppers to Settings.
- Wired quiet-hours settings into `ReminderNotificationCoordinator`.
- Added tests for quiet-hours defaults, minute wrapping, and time formatting.
- Updated owner testing and QA docs.

## Files Changed

- `app/src/main/java/com/guptarajat/screenactivetaskreminder/MainActivity.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/reminders/ReminderNotificationCoordinator.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/settings/SettingsStore.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/settings/TaskReminderSettings.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/ui/app/TaskReminderApp.kt`
- `app/src/test/java/com/guptarajat/screenactivetaskreminder/settings/TaskReminderSettingsTest.kt`
- `docs/ARCHITECTURE.md`
- `docs/PO_WINDOWS_TEST_GUIDE.md`
- `docs/QA_PLAN.md`

## Verification

Commands run:

```powershell
$env:JAVA_HOME='C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10'
$env:ANDROID_SDK_ROOT='C:\tmp\task-reminder-dev\android-sdk'
$env:GRADLE_USER_HOME='C:\tmp\task-reminder-dev\gradle-home'
.\gradlew.bat --offline --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:compileDebugKotlin
.\gradlew.bat --offline --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:testDebugUnitTest
.\gradlew.bat --offline --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug
```

Result:

- `:app:compileDebugKotlin`: passed.
- `:app:testDebugUnitTest`: passed.
- `:app:assembleDebug`: passed.

## Open Questions

- None for this slice.

## Known Limitations

- Quiet-hours controls use hourly steps for V1 simplicity.
- Automatic background reminder scheduling is still future `REM-004` work.

## Recommended Next Step

Continue with `REM-004` automatic reminder scheduling.
