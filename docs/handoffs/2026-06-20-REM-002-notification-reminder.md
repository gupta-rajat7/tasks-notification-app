# REM-002 Notification Reminder

## Backlog Item

`REM-002`

## Branch

`codex/rem-002-notification-reminder`

## Summary

Implemented the first Android notification reminder slice:

- Adds notification permission to the Android manifest.
- Creates a reminder notification channel.
- Evaluates cached pending-task count and existing reminder rules before posting.
- Posts a reminder notification from the Today screen Check now action when rules pass.
- Adds Review, Snooze, and Done for now notification actions.
- Persists review and snooze state in DataStore.
- Adds owner-test checklist updates for the notification flow.

## Files Changed

- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/MainActivity.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/data/local/TaskDao.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/reminders/ReminderNotificationActionReceiver.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/reminders/ReminderNotificationCoordinator.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/settings/SettingsStore.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/settings/TaskReminderSettings.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/ui/app/TaskReminderApp.kt`
- `app/src/main/res/drawable/ic_notification.xml`
- `app/src/test/java/com/guptarajat/screenactivetaskreminder/reminders/ReminderNotificationCoordinatorTest.kt`
- `app/src/test/java/com/guptarajat/screenactivetaskreminder/settings/TaskReminderSettingsTest.kt`
- `docs/ARCHITECTURE.md`
- `docs/BACKLOG.md`
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
git diff --check
& 'C:\tmp\task-reminder-dev\android-sdk\build-tools\36.1.0\aapt.exe' dump permissions 'app\build\outputs\apk\debug\app-debug.apk'
```

Result:

- `:app:compileDebugKotlin`: passed.
- `:app:testDebugUnitTest`: passed.
- `:app:assembleDebug`: passed.
- `git diff --check`: passed.
- APK permissions include `android.permission.POST_NOTIFICATIONS`.

## Open Questions

- None for this slice.

## Known Limitations

- Automatic background scheduling is not implemented in this slice. `REM-004` was added to the backlog for conservative scheduling without exact-alarm permission.
- Full notification smoke testing still requires running the debug APK on emulator or phone, granting notification permission, and having cached pending tasks.
- Quiet-hours UI remains future `REM-003` work.

## Recommended Next Step

Run the PO Windows smoke test for Today > Enable and Today > Check now, then implement `REM-003` quiet-hours settings or `REM-004` automatic scheduling depending on product priority.
