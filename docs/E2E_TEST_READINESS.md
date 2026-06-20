# End-To-End Test Readiness

This is the product-owner readiness view for testing the app on Windows.

## Current Verdict

The app is ready for local end-to-end testing of the Android experience:

- Install and launch.
- Guided onboarding.
- Today, Tasks, and Settings navigation.
- Settings persistence.
- Notification permission recovery.
- Reminder settings.
- Quiet hours.
- Manual reminder check behavior.
- Optional screen-activity settings and diagnostics.

Live Google Tasks end-to-end testing is still blocked until Google Cloud OAuth is configured and the local Web Client ID is added.

## One-Command Setup Check

Open PowerShell and run:

```powershell
cd 'C:\Users\Tanu Gupta\Documents\Tasks Notification App'
powershell -ExecutionPolicy Bypass -File .\tools\check_windows_readiness.ps1 -RunBuild -RunTests
```

Passing result means the local machine can build and test the app.

Expected warnings before full Google testing:

- No connected Android device if the emulator is not already running.
- Missing Google Web Client ID until OAuth setup is complete.
- GitHub auth warning if Codex cannot push from this session.

## One-Command App Launch

If the emulator is not open, run:

```powershell
cd 'C:\Users\Tanu Gupta\Documents\Tasks Notification App'
powershell -ExecutionPolicy Bypass -File .\tools\run_app_windows.ps1 -StartEmulator
```

If the emulator or a phone is already connected, run:

```powershell
cd 'C:\Users\Tanu Gupta\Documents\Tasks Notification App'
powershell -ExecutionPolicy Bypass -File .\tools\run_app_windows.ps1
```

For a fresh app install that clears old local data:

```powershell
cd 'C:\Users\Tanu Gupta\Documents\Tasks Notification App'
powershell -ExecutionPolicy Bypass -File .\tools\run_app_windows.ps1 -StartEmulator -ResetAppData
```

## What To Test First

Use `docs\PO_WINDOWS_TEST_GUIDE.md` as the full checklist.

Start with:

1. App opens without crashing.
2. Guided setup appears on fresh install.
3. Continue through onboarding.
4. Today, Tasks, and Settings tabs work.
5. Settings controls respond quickly.
6. Notification recovery screen is understandable.
7. Today `Check now` gives a clear result.
8. Optional screen-activity mode can be toggled from Settings.

## What Is Not Yet Ready

These are not app-code blockers, but they block complete external beta:

- Real Google sign-in and Google Tasks sync need Google Cloud OAuth setup.
- Google OAuth needs a local `google.web.client.id` value in `local.properties`.
- GitHub push/PR requires refreshed GitHub credentials in the Codex session.
- Physical Android phone validation is still needed before beta decisions.
- Privacy policy draft needs product-owner review and a public URL before Play testing.

## Ready / Not Ready Summary

Ready now:

- Local Android app test on emulator or phone.
- UI and settings test.
- Notification/reminder behavior test with cached/local state.
- Screen-activity settings test.

Not ready until owner setup:

- Real Google account sign-in.
- Live Google Tasks sync.
- External tester beta through Google Play.
