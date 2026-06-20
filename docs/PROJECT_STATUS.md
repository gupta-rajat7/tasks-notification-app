# Project Status

Last updated: 2026-06-20

This document is the product-owner view of where the app stands.

## Current Build State

The Android app has a working local V1 foundation:

- Native Android Kotlin app.
- Jetpack Compose and Material 3 UI.
- Today, Tasks, and Settings navigation.
- Local settings persistence.
- Google sign-in wiring.
- Google Tasks read-only sync client.
- Local Room task cache.
- Manual sync feedback.
- Watched task-list filtering.
- Reminder decision engine.
- Notification reminders with Review, Snooze, and Done for now actions.
- Quiet hours.
- Automatic best-effort reminder scheduling.
- Guided onboarding.
- Empty and error states.
- Notification permission recovery.
- Optional screen activity reminders from Settings.
- Owner run/test guides and Play Store preparation docs.
- Windows readiness checker for local setup diagnostics.
- One-command Windows run script and E2E readiness guide.
- Google OAuth local setup helper for debug SHA-1 and Web Client ID configuration.

## Current Blockers

### Google Sign-In

The app-side Google authentication process is implemented. Real Google sign-in still needs product-owner Google Cloud OAuth setup:

- Enable Google Tasks API.
- Configure OAuth consent screen.
- Create Android OAuth client.
- Add this Windows machine debug SHA-1.
- Create Web OAuth client.
- Save the Web Client ID locally with `tools/setup_google_oauth.ps1`.

Use `docs/PO_GOOGLE_OAUTH_SETUP_GUIDE.md`.

### GitHub Sync

GitHub `main` was synced through PR #21 on 2026-06-20.

Latest merged main commit:

- `ed954a4 Merge pull request #21 from gupta-rajat7/codex/google-auth-setup-helper`

Current new work, if any, should be pushed through a fresh `codex/` branch and PR.

## Next Product Owner Tests

Use `docs/WINDOWS_RUN_APP_GUIDE.md` to run the app on the emulator.

Use `docs/WINDOWS_READINESS_CHECK.md` first when you want one command to check Java, Android SDK, emulator, Google sign-in setup, and GitHub auth.

Use `docs/E2E_TEST_READINESS.md` for the current ready/not-ready verdict and the fastest app launch command.

Use `docs/PO_GOOGLE_OAUTH_SETUP_GUIDE.md` to finish Google sign-in setup.

Then use `docs/PO_WINDOWS_TEST_GUIDE.md` and focus on:

- App opens and navigation works.
- Settings controls respond quickly.
- Google sign-in setup helper prints the debug SHA-1 and saves the Web Client ID after Google Cloud setup.
- Notification permission recovery works.
- Today > Check now gives understandable results.
- Optional screen activity mode can be toggled from Settings.

## Beta Readiness Gaps

Before external testers:

- Complete Google Cloud OAuth setup.
- Run `tools/setup_google_oauth.ps1` with the Web OAuth Client ID.
- Retest Google sign-in and Google Tasks sync with a real account.
- Test on one physical Android phone.
- Review privacy policy draft.
- Prepare a public privacy policy URL.
- Upload an internal testing build through Google Play Console.

## Monetization Position

Do not add ads in V1.

Current plan:

- Free V1 for validation.
- Later one-time Pro unlock only after testers confirm the app is useful.
- No subscriptions until there is proven repeated user value.
