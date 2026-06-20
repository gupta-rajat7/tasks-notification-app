# Handoff: MON-001 OAuth, Privacy, Store, And Monetization Readiness

Date: 2026-06-20

Branch: `codex-open-readiness-docs-monetization-oauth`

## Summary

Completed the `MON-001` design slice and added owner-facing readiness documents for the Google sign-in blocker, privacy policy draft, store listing draft, and performance testing.

Also changed the Android build so `google_web_client_id` is generated from local-only configuration instead of a committed string resource.

## Product Decisions

- Keep V1 free.
- Do not add Google ads in V1.
- Do not implement subscriptions in the first monetization experiment.
- Prefer a future one-time Pro unlock only after tester feedback proves the core app is useful.
- Keep Google OAuth IDs out of GitHub.

## Files Added

- `docs/PO_GOOGLE_OAUTH_SETUP_GUIDE.md`
- `docs/PRIVACY_POLICY_DRAFT.md`
- `docs/STORE_LISTING_DRAFT.md`
- `docs/PRO_UNLOCK_DESIGN.md`
- `docs/PERFORMANCE_TEST_GUIDE.md`

## Files Updated

- `app/build.gradle.kts`
- `app/src/main/res/values/strings.xml`
- `docs/BACKLOG.md`
- `docs/GOOGLE_SIGN_IN_SETUP.md`
- `docs/PLAY_STORE_RELEASE_GUIDE.md`
- `docs/PO_WINDOWS_TEST_GUIDE.md`
- `docs/RISKS.md`
- `docs/ROADMAP.md`

## Google Sign-In Blocker

The app is not broken. Real sign-in is blocked until Google Cloud OAuth setup is completed.

Required owner/developer setup:

- Enable Google Tasks API.
- Create OAuth consent screen.
- Add Google Tasks read-only scope.
- Create Android OAuth client for `com.guptarajat.screenactivetaskreminder`.
- Add this Windows machine's debug SHA-1.
- Create Web OAuth client.
- Add `google.web.client.id=...apps.googleusercontent.com` to local `local.properties`.
- Rebuild and reinstall the app.

## Verification

- `git diff --check`: passed.
- `.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:testDebugUnitTest`: passed.
- `.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug`: passed.

Notes:

- The first Gradle test attempt was blocked by Windows cache permissions; the escalated rerun exposed and then verified the Gradle script import fix.
- The Google sign-in flow is still expected to show the setup message until the product owner creates Google Cloud OAuth clients and the Web Client ID is configured locally.

## Next Recommended Work

1. Product owner completes Google Cloud OAuth setup.
2. Developer configures `local.properties` with the Web Client ID.
3. Rebuild, reinstall, and test Google sign-in on emulator.
4. Repeat sign-in and responsiveness testing on a real Android phone.
5. Continue remaining UX polish only after OAuth testing is unblocked.
