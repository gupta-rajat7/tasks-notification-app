# REM-004 Automatic Reminder Scheduling

## Backlog Item

`REM-004`

## Branch

`codex/rem-004-automatic-reminder-scheduling`

## Summary

Implemented conservative automatic reminder scheduling with Android WorkManager. The app now schedules the next reminder check after startup, settings changes, Google Tasks sync, manual reminder check, review, snooze, and notification actions. Scheduled checks reuse `ReminderNotificationCoordinator`, which already applies `ReminderRules`, DataStore settings, and Room pending-task state.

This slice intentionally avoids exact alarms, foreground services, Usage Access, AccessibilityService, overlays, and battery-exemption prompts.

## Files Changed

- `app/build.gradle.kts`
- `gradle/libs.versions.toml`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/reminders/ReminderCheckWorker.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/reminders/ReminderScheduleCalculator.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/reminders/ReminderScheduler.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/reminders/ReminderNotificationActionReceiver.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/reminders/ReminderNotificationCoordinator.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/reminders/ReminderRules.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/ui/app/TaskReminderApp.kt`
- `app/src/test/java/com/guptarajat/screenactivetaskreminder/reminders/ReminderScheduleCalculatorTest.kt`
- `docs/ARCHITECTURE.md`
- `docs/BACKLOG.md`
- `docs/PO_WINDOWS_TEST_GUIDE.md`
- `docs/QA_PLAN.md`
- `README.md`

## Verification

Commands run:

```powershell
$env:JAVA_HOME='C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10'
$env:ANDROID_SDK_ROOT='C:\tmp\task-reminder-dev\android-sdk'
$env:GRADLE_USER_HOME='C:\tmp\task-reminder-dev\gradle-home'
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:compileDebugKotlin
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:testDebugUnitTest
.\gradlew.bat --offline --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:testDebugUnitTest
.\gradlew.bat --offline --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug
.\gradlew.bat --offline --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' --rerun-tasks :app:assembleDebug
git diff --check
```

Result:

- `git diff --check`: passed.
- `:app:compileDebugKotlin`: passed after Gradle cache access was elevated for the local wrapper lock.
- First offline `:app:testDebugUnitTest`: failed because `androidx.concurrent:concurrent-futures-ktx:1.1.0` was not cached.
- Online `:app:testDebugUnitTest`: passed and populated the missing WorkManager transitive dependency.
- Offline `:app:testDebugUnitTest`: passed.
- First offline `:app:assembleDebug`: Gradle daemon log recorded `BUILD SUCCESSFUL in 1m 54s`; the shell command timed out while waiting for process cleanup, and two orphaned Java build processes were stopped afterward.
- Forced offline `:app:assembleDebug --rerun-tasks`: passed; debug APK was rebuilt at `app/build/outputs/apk/debug/app-debug.apk`.

## Open Questions

- None.

## Known Limitations

- WorkManager timing is best-effort. Android may delay work during Doze, app standby, battery saver, or after force-stop.
- V1 still does not detect real active screen time. It schedules reminder checks around task-review state, quiet hours, snooze, and pending cached tasks.

## Recommended Next Step

Continue with the next reminder or task-management backlog item after this branch is merged.
