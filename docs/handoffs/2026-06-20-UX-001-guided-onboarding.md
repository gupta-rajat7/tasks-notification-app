# UX-001 Guided Onboarding

## Backlog Item

`UX-001`

## Branch

`codex/ux-001-guided-onboarding`

## Summary

Implemented a guided, skippable first-run onboarding flow. New users now see four setup steps before the main app: product promise, Google Tasks sign-in, reminder notifications, and default reminder settings. Completing or skipping onboarding writes `hasCompletedOnboarding` to DataStore and lands the user on Today.

This slice intentionally avoids advanced permissions during onboarding. Usage Access remains Settings-only diagnostics.

## Files Changed

- `app/src/main/java/com/guptarajat/screenactivetaskreminder/settings/SettingsStore.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/settings/TaskReminderSettings.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/ui/app/TaskReminderApp.kt`
- `app/src/test/java/com/guptarajat/screenactivetaskreminder/settings/TaskReminderSettingsTest.kt`
- `docs/ARCHITECTURE.md`
- `docs/BACKLOG.md`
- `docs/PO_WINDOWS_TEST_GUIDE.md`
- `docs/QA_PLAN.md`
- `docs/UI_UX_PLAN.md`
- `docs/handoffs/2026-06-20-UX-001-guided-onboarding.md`

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
- Emulator smoke test on `TaskReminder_API35`: passed.
  - Fresh app data opened guided onboarding at `Step 1 of 4`.
  - `Set up later` landed on Today and persisted across app relaunch.
  - Continue path advanced through Google Tasks, notifications, and default settings, then `Start using app` landed on Today.

## Open Questions

- None.

## Known Limitations

- Onboarding does not select a default Google Tasks list yet. That belongs with task-list filtering.
- Onboarding does not force a first sync because OAuth setup may be unavailable on a tester's machine or phone.

## Recommended Next Step

Proceed to OAuth/privacy documentation or notification permission recovery polish.
