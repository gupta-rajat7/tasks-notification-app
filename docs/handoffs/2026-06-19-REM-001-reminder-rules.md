# REM-001 Reminder Rules

## Status

Complete on branch `codex/rem-001-reminder-rules`.

## What Changed

- Added a pure Kotlin reminder-rule engine in `reminders/ReminderRules.kt`.
- Added rule inputs for pending task count, current time, local minute of day, reminder interval, last review time, snooze expiry, and quiet hours.
- Added decision output with `shouldRemind`, suppression reason, and next eligible time when applicable.
- Added support for same-day quiet hours, overnight quiet hours, and all-day quiet hours when start and end are equal.
- Added unit tests covering pending tasks, no pending tasks, quiet hours, snooze, recent review, expired snooze, expired review interval, and suppression priority.

## Product Decisions

- Reminder rules are intentionally framework-free and do not post notifications yet.
- Quiet-hour time-zone conversion is left to the future Android caller; the rule engine receives a local minute of day.
- Suppression priority is no pending tasks, quiet hours, snooze, then recent review.

## Verification

- `rg "android\." app\src\main\java\com\guptarajat\screenactivetaskreminder\reminders app\src\test\java\com\guptarajat\screenactivetaskreminder\reminders`: no Android framework imports found.
- `git diff --check`: passed.
- `.\gradlew.bat --offline --no-daemon --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:testDebugUnitTest`: passed.

## Notes For Next Session

- Next high-priority slice is `REM-002`: wire the rule decision into Android notification scheduling and notification actions.
- `REM-003` should later add user-facing quiet-hours settings and feed them into this rule model.
