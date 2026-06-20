# UX-003 Notification Permission Recovery

## Backlog Item

`UX-003`

## Branch

`codex/ux-003-notification-permission-recovery`

## Summary

Added a notification recovery flow for users who skip or deny reminder notifications. Today now exposes an `Open settings` path when notifications are off, and Settings now has a dedicated Notification recovery card with status, recovery instructions, `Enable notifications`, `Open Android settings`, and `Check status` actions.

The implementation stays within standard Android notification permission behavior. It does not add Usage Access, AccessibilityService, overlay permission, exact-alarm permission, battery-exemption permission, ad SDKs, analytics, or monetization code.

Also corrected the PO Windows guide to use the actual user-profile AVD path for `TaskReminder_API35`.

## Files Changed

- `app/src/main/java/com/guptarajat/screenactivetaskreminder/MainActivity.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/ui/app/TaskReminderApp.kt`
- `docs/ARCHITECTURE.md`
- `docs/BACKLOG.md`
- `docs/PO_WINDOWS_TEST_GUIDE.md`
- `docs/QA_PLAN.md`
- `docs/UI_UX_PLAN.md`
- `docs/handoffs/2026-06-20-UX-003-notification-permission-recovery.md`

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
- Emulator smoke test: attempted on `TaskReminder_API35`, but the headless emulator exited before ADB reported a device. No emulator process remained afterward.

## Open Questions

- None.

## Known Limitations

- Full visual verification of the Android settings handoff should still be repeated on an emulator or physical Android phone.
- Android may not show the runtime notification prompt again after some denial states; the Settings recovery card now provides the Android settings fallback for that case.

## Recommended Next Step

Repeat emulator or physical-phone visual smoke testing for notification recovery, then continue with `UX-004`: manual sync and last-synced feedback.
