# Handoff: SCR-002 Optional Screen Activity Mode

Date: 2026-06-20

Branch: `codex-open-readiness-docs-monetization-oauth`

## Summary

Implemented `SCR-002` as a conservative optional mode.

The app now lets users turn on screen activity reminders from Settings. The mode is not part of first-run onboarding. If enabled, reminder checks require derived recent Android activity evidence before posting a reminder.

## Product Behavior

- Screen activity mode is off by default.
- Users can enable or skip it from Settings.
- The app explains Usage Access before opening Android settings.
- If mode is enabled and Usage Access is off, reminders are suppressed with a clear reason.
- If mode is enabled and no recent activity evidence is detected, reminders are suppressed with a clear reason.
- The app remains usable with standard reminders when the mode is off.

## Privacy Guardrails

- Raw per-app usage history is not persisted.
- Usage events are queried transiently at reminder-check time.
- Only derived recent-activity state feeds `ReminderRules`.
- Privacy and Play Store draft docs were updated.

## Files Changed

- `app/src/main/java/com/guptarajat/screenactivetaskreminder/settings/TaskReminderSettings.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/settings/SettingsStore.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/screenactivity/UsageAccessDiagnostics.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/reminders/ReminderRules.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/reminders/ReminderNotificationCoordinator.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/ui/app/TaskReminderApp.kt`
- `app/src/main/java/com/guptarajat/screenactivetaskreminder/MainActivity.kt`
- `app/src/test/java/com/guptarajat/screenactivetaskreminder/reminders/ReminderRulesTest.kt`
- `app/src/test/java/com/guptarajat/screenactivetaskreminder/screenactivity/UsageAccessDiagnosticsTest.kt`
- `app/src/test/java/com/guptarajat/screenactivetaskreminder/settings/TaskReminderSettingsTest.kt`
- `docs/BACKLOG.md`
- `docs/PRIVACY_POLICY_DRAFT.md`
- `docs/PLAY_STORE_RELEASE_GUIDE.md`
- `docs/RISKS.md`
- `docs/SCREEN_ACTIVITY_FEASIBILITY.md`

## Verification

- `git diff --check`: passed.
- `.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:testDebugUnitTest --tests com.guptarajat.screenactivetaskreminder.reminders.ReminderRulesTest --tests com.guptarajat.screenactivetaskreminder.screenactivity.UsageAccessDiagnosticsTest --tests com.guptarajat.screenactivetaskreminder.settings.TaskReminderSettingsTest`: passed.
- `.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug`: passed.

## Remaining QA

- Test Settings > Screen activity reminders on the Windows emulator.
- Test on one physical Android phone before beta.
- Confirm Usage Access disclosure remains acceptable before Play Store submission.
