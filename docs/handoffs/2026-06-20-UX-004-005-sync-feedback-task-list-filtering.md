# UX-004 And UX-005 Sync Feedback And Task-List Filtering

## Backlog Items

`UX-004`, `UX-005`

## Branch

`codex/ux-004-005-sync-feedback-task-list-filtering`

## Summary

Implemented the remaining Tasks-screen UX polish items:

- Added last successful sync feedback on Today and Tasks.
- Kept the existing manual `Sync now` action visible and paired it with cache freshness.
- Added a Watched task lists card on Tasks.
- Added per-list switches so users can choose which synced Google Task lists feed Today and reminder checks.
- Preserved existing task-list selections across future syncs for matching Google task-list IDs.

This slice does not add a backend, analytics, ads, billing, advanced permissions, or a new reminder engine.

## Files Changed

- `app/src/main/java/com/guptarajat/screenactivetaskreminder/MainActivity.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/data/local/TaskListDao.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/data/repository/TaskCacheMappers.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/data/repository/TaskCacheModels.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/data/repository/TaskCacheRepository.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/ui/app/TaskReminderApp.kt`
- `app/src/test/java/com/guptarajat/screenactivetaskreminder/data/repository/TaskCacheMappersTest.kt`
- `docs/ARCHITECTURE.md`
- `docs/BACKLOG.md`
- `docs/PO_WINDOWS_TEST_GUIDE.md`
- `docs/QA_PLAN.md`
- `docs/UI_UX_PLAN.md`
- `docs/handoffs/2026-06-20-UX-004-005-sync-feedback-task-list-filtering.md`

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
```

Result:

- `:app:compileDebugKotlin`: passed.
- `:app:testDebugUnitTest`: passed.
- `:app:assembleDebug`: passed.
- `git diff --check`: passed.

## Open Questions

- None.

## Known Limitations

- Visual QA should still be repeated on emulator or phone with real synced Google Tasks data.
- The UI prevents deselecting the final watched list to avoid accidentally disabling all reminders.

## Recommended Next Step

Move to `MON-001`: Pro unlock design only, without billing integration.
