# SCR-001 Usage Access Feasibility Spike

## Backlog Item

`SCR-001`

## Branch

`codex/scr-001-usage-access-spike`

## Summary

Implemented a Settings-only Usage Access diagnostics spike for real screen-activity feasibility. The app can now check whether Android Usage Access is enabled, open the Android Usage Access settings screen when available, and scan recent local `UsageStatsManager` event counts after the user taps Scan.

This slice intentionally keeps screen activity out of first-run onboarding and out of the reminder engine. It does not store raw per-app usage history.

## Files Changed

- `README.md`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/MainActivity.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/screenactivity/UsageAccessDiagnostics.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/ui/app/TaskReminderApp.kt`
- `app/src/test/java/com/guptarajat/screenactivetaskreminder/screenactivity/UsageAccessDiagnosticsTest.kt`
- `docs/ARCHITECTURE.md`
- `docs/PO_WINDOWS_TEST_GUIDE.md`
- `docs/QA_PLAN.md`
- `docs/SCREEN_ACTIVITY_FEASIBILITY.md`
- `docs/handoffs/2026-06-20-SCR-001-usage-access-feasibility-spike.md`

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
adb devices
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell appops set com.guptarajat.screenactivetaskreminder GET_USAGE_STATS allow
```

Result:

- `:app:compileDebugKotlin`: passed. The shell command timed out after Gradle had already recorded `BUILD SUCCESSFUL` in the daemon log.
- `:app:testDebugUnitTest`: passed.
- `:app:assembleDebug`: passed; debug APK was built at `app/build/outputs/apk/debug/app-debug.apk`.
- `git diff --check`: passed.
- Windows emulator `TaskReminder_API35` booted successfully.
- Debug APK installed and launched on the emulator.
- Installed package targets SDK 36 and declares `android.permission.PACKAGE_USAGE_STATS`.
- Usage Access was enabled by emulator app-op for smoke testing.
- Settings > Screen activity diagnostics showed enabled access and returned 198 usage events from the last 60 minutes.
- Target event counts rendered on the emulator: `SCREEN_INTERACTIVE=1`, `SCREEN_NON_INTERACTIVE=0`, `ACTIVITY_RESUMED=4`, `ACTIVITY_PAUSED=4`.

## Open Questions

- Product approval is needed before building `SCR-002` and connecting screen-activity signals to reminder timing.
- Physical-device validation is still required before treating Usage Access as reliable enough for beta.

## Known Limitations

- The diagnostics card is a feasibility tool, not a user-facing V1 reminder feature.
- Device manufacturers may expose Usage Access settings differently.
- The app remains usable without Usage Access.

## Recommended Next Step

Run the Windows emulator test steps in `docs/SCREEN_ACTIVITY_FEASIBILITY.md`. If target events appear reliably, decide whether to implement `SCR-002: Optional Screen Activity Mode`.
