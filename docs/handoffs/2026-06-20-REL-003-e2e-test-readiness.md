# REL-003: E2E Test Readiness

Date: 2026-06-20
Branch: `codex-open-readiness-docs-monetization-oauth`

## Summary

Added the final product-owner testing packet for local end-to-end testing.

## What Changed

- Added `tools/run_app_windows.ps1`.
- Added `docs/E2E_TEST_READINESS.md`.
- Linked E2E readiness from `README.md`, `docs/WINDOWS_RUN_APP_GUIDE.md`, and `docs/PO_WINDOWS_TEST_GUIDE.md`.
- Added completed backlog item `REL-003`.
- Updated `docs/PROJECT_STATUS.md`.

## Product Verdict

The app is ready for local Android end-to-end testing on the Windows emulator or a connected Android phone.

Live Google Tasks end-to-end testing still requires product-owner OAuth setup before it can pass.

## Verification

- `powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\run_app_windows.ps1 -CheckOnly`: passed.
- `powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\check_windows_readiness.ps1 -SkipGitHub`: passed with expected warnings.
- Elevated `powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\check_windows_readiness.ps1 -SkipGitHub -RunBuild -RunTests`: passed.
  - `:app:assembleDebug`: passed.
  - `:app:testDebugUnitTest`: passed.
- `git diff --check`: passed.

## Known Blockers

- GitHub push and PR creation remain blocked until Git/GitHub credentials are refreshed in this Codex session.
- Real Google sign-in remains blocked until Google Cloud OAuth setup and local `google.web.client.id` configuration are complete.
- Physical Android phone validation remains required before beta.
