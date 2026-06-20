# Session Resume Handoff

Date: 2026-06-20
Branch: `codex/final-session-handoff`

## Backlog Item

`SESSION-RESUME`

## Summary

This handoff captures the state after the V1 Android app foundation, E2E owner testing tools, and repo-side Google OAuth setup helper were completed and merged.

## Current Repo State

- Local branch before this handoff: `main`.
- Latest merged main commit before this handoff: `ed954a4 Merge pull request #21 from gupta-rajat7/codex/google-auth-setup-helper`.
- PR #20 merged the E2E owner-testing readiness work.
- PR #21 merged the Google OAuth local setup helper.
- GitHub `main` and local `main` were in sync before this handoff branch was created.

## App Status

Ready for local product-owner testing:

- App install and launch on Windows emulator or connected Android phone.
- Guided onboarding.
- Today, Tasks, and Settings navigation.
- Settings persistence.
- Reminder interval, snooze, quiet hours, and theme controls.
- Notification permission recovery.
- Reminder decision engine and notification actions.
- Manual sync UI and task-list filtering.
- Optional screen-activity reminder mode from Settings.
- Windows readiness checker and one-command app runner.

Implemented but waiting on owner-controlled external setup:

- Google sign-in app wiring.
- Google Tasks read-only authorization and sync client.
- Local OAuth setup helper.

## Remaining External Actions

Codex cannot complete these without product-owner Google Cloud access:

1. Create or select the Google Cloud project for this app.
2. Enable Google Tasks API.
3. Configure the OAuth consent screen.
4. Add the Google Tasks read-only scope:
   `https://www.googleapis.com/auth/tasks.readonly`
5. Create the Android OAuth client for package:
   `com.guptarajat.screenactivetaskreminder`
6. Add this Windows debug SHA-1:
   `11:4A:C5:3F:D8:2B:89:69:EC:A1:B5:45:67:B1:73:1F:46:62:DF:98`
7. Create the Web OAuth client.
8. Run:

```powershell
cd 'C:\Users\Tanu Gupta\Documents\Tasks Notification App'
powershell -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -WebClientId 'YOUR_WEB_CLIENT_ID.apps.googleusercontent.com'
```

9. Rebuild, install, and launch:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\run_app_windows.ps1 -StartEmulator
```

## Best Next Session Prompt

```text
Resume from docs/handoffs/2026-06-20-SESSION-RESUME-HANDOFF.md.
First confirm GitHub main is current and local working tree is clean.
If I have completed Google Cloud OAuth setup, help me add the Web Client ID locally, rebuild, install, and test Google sign-in plus Google Tasks sync end to end.
If OAuth is still not done, do not add new app features. Help me complete or verify the Google Cloud setup steps.
```

## Useful Commands

Check Windows readiness:

```powershell
cd 'C:\Users\Tanu Gupta\Documents\Tasks Notification App'
powershell -ExecutionPolicy Bypass -File .\tools\check_windows_readiness.ps1
```

Print debug SHA-1:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -PrintDebugSha1
```

Run app:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\run_app_windows.ps1 -StartEmulator
```

Run build and tests:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\check_windows_readiness.ps1 -SkipGitHub -RunBuild -RunTests
```

## Verification From Prior Session

- `powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -WebClientId '1234567890-example.apps.googleusercontent.com' -ValidateOnly`: passed.
- `powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -PrintDebugSha1`: passed.
- Invalid Web Client ID smoke test rejected `bad-client-id` as expected.
- `powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\check_windows_readiness.ps1 -SkipGitHub -RunBuild -RunTests`: passed.
  - `:app:assembleDebug`: passed.
  - `:app:testDebugUnitTest`: passed.
- `GoogleSignInConfigTest`: 4 tests passed.

## Known Limitations

- Real Google sign-in will continue to show the OAuth setup message until a valid Web OAuth Client ID is saved locally.
- `local.properties` is intentionally ignored by Git; do not commit OAuth IDs or secrets.
- Physical Android phone validation is still needed before beta.
- Privacy policy draft still needs product-owner review and a public URL before Play testing.

## Recommended Next Step

Complete Google Cloud OAuth setup, then use the helper script to save the Web Client ID locally and test Google sign-in plus Google Tasks sync end to end.
