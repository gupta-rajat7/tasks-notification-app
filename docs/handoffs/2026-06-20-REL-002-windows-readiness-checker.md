# REL-002: Windows Readiness Checker

Date: 2026-06-20
Branch: `codex-open-readiness-docs-monetization-oauth`

## Summary

Added a product-owner-friendly Windows readiness checker and documentation.

## What Changed

- Added `tools/check_windows_readiness.ps1`.
- Added `docs/WINDOWS_READINESS_CHECK.md`.
- Linked the checker from `README.md`, `docs/WINDOWS_RUN_APP_GUIDE.md`, and `docs/PO_WINDOWS_TEST_GUIDE.md`.
- Updated `docs/BACKLOG.md` with completed `REL-002`.
- Updated `docs/PROJECT_STATUS.md` to mention the checker and latest local documentation commit.
- Corrected stale PO test-guide wording about optional screen-activity reminders.

## Checker Coverage

- Gradle wrapper.
- Portable Gradle cache with repo-local fallback.
- JDK 17.
- Android SDK.
- Android SDK Platform 36.
- ADB.
- Android emulator tools.
- `TaskReminder_API35` AVD.
- Connected emulator or phone.
- Google Web Client ID setup.
- GitHub CLI auth.
- Optional debug APK build.
- Optional JVM unit tests.

## Verification

- `powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\check_windows_readiness.ps1`: passed with 0 failures and expected warnings.
- Elevated `powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\check_windows_readiness.ps1 -SkipGitHub -RunBuild -RunTests`: passed.
  - `:app:assembleDebug`: passed.
  - `:app:testDebugUnitTest`: passed.
- `git diff --check`: passed.

## Known Blockers

GitHub push and PR creation are still blocked until Git/GitHub credentials are refreshed in this Codex session.

The readiness checker currently reports these warnings:

- No emulator or phone is connected unless one is running.
- Google Web Client ID is not configured, so real Google sign-in is blocked.
- GitHub CLI auth token is invalid, so push/PR is blocked.
- In the Codex sandbox only, the portable Gradle cache may be read-only; the checker falls back to repo-local `.gradle-home`.
