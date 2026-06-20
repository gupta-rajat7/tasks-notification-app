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

## Current Blockers

### Google Sign-In

Real Google sign-in still needs product-owner Google Cloud OAuth setup:

- Enable Google Tasks API.
- Configure OAuth consent screen.
- Create Android OAuth client.
- Add this Windows machine debug SHA-1.
- Create Web OAuth client.
- Put the Web Client ID in local `local.properties`.

Use `docs/PO_GOOGLE_OAUTH_SETUP_GUIDE.md`.

### GitHub Sync

This Codex session has local commits that are not pushed because Git/GitHub credentials are unavailable in the session.

The current local branch is:

`codex-open-readiness-docs-monetization-oauth`

The latest local commits include:

- `c80ac4e Add OAuth and monetization readiness docs`
- `75175c3 Add Windows run app guide`
- `2d3ee3c Polish empty and error states`
- `87543dc Add optional screen activity reminder mode`
- `75f57ee Update project status tracking`

Run this in PowerShell to refresh GitHub auth:

```powershell
gh auth logout -h github.com -u gupta-rajat7
gh auth login -h github.com
```

After auth is fixed, Codex should push the branch, open a PR, merge to `main`, and confirm GitHub is in sync.

## Next Product Owner Tests

Use `docs/WINDOWS_RUN_APP_GUIDE.md` to run the app on the emulator.

Then use `docs/PO_WINDOWS_TEST_GUIDE.md` and focus on:

- App opens and navigation works.
- Settings controls respond quickly.
- Google sign-in setup message is understood until OAuth is configured.
- Notification permission recovery works.
- Today > Check now gives understandable results.
- Optional screen activity mode can be toggled from Settings.

## Beta Readiness Gaps

Before external testers:

- Complete Google Cloud OAuth setup.
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
