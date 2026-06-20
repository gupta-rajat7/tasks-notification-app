# Risk Register

## RISK-001: AccessibilityService Policy Risk

Status: Open.

Description: AccessibilityService use for general productivity monitoring can trigger Google Play policy review and user trust concerns.

Mitigation:

- Do not use AccessibilityService in V1.
- Use notification-first reminders.
- Revisit only if the product truly requires stronger usage signals.
- Prefer optional Usage Access over AccessibilityService if real screen activity is approved later.

## RISK-002: Intrusive Overlay Experience

Status: Open.

Description: Full-screen overlays can feel aggressive and require extra permissions.

Mitigation:

- Use standard Android notifications in V1.
- Add overlay only as opt-in advanced mode after beta feedback.

## RISK-003: Google OAuth Verification

Status: Mitigated by documentation; open until Google Cloud OAuth is configured and verified.

Description: Public apps using Google user data may require consent-screen verification and clear privacy disclosures.

Mitigation:

- Request the narrowest Google Tasks scope possible.
- Use the owner setup guide in `docs/PO_GOOGLE_OAUTH_SETUP_GUIDE.md`.
- Prepare and review `docs/PRIVACY_POLICY_DRAFT.md` before external beta.
- Keep data local and document that clearly.

## RISK-004: Background Reliability

Status: Open.

Description: Android background execution limits may affect sync and reminder timing.

Mitigation:

- Use WorkManager for reliable background sync.
- Keep reminder logic conservative.
- Avoid claiming exact real-time behavior.

## RISK-008: Usage Access Onboarding And Privacy Risk

Status: Open.

Description: Real cross-app screen-activity detection likely requires Usage Access. This adds setup friction and gives the app access to sensitive device usage signals.

Mitigation:

- Keep Usage Access out of V1 first-run onboarding.
- Add it only as an optional advanced mode after product approval.
- Explain the permission before opening Android settings.
- Store derived reminder state instead of raw per-app usage history.
- Keep the app useful when the user declines the permission.

## RISK-005: First-App Complexity

Status: Open.

Description: Google sign-in, Play publishing, billing, and permissions can overwhelm a first project.

Mitigation:

- Build in phases.
- Do not implement monetization until the free product works.
- Keep V1 free of ads and billing.
- Keep owner-facing setup guides in `docs/`.

## RISK-007: Android Platform Drift

Status: Open.

Description: Android SDK, Play target API, and Gradle requirements change over time. Falling behind can block builds, testing, or Play Store releases.

Mitigation:

- Keep `compileSdk` and `targetSdk` aligned with the current supported Android SDK when practical.
- Update Gradle and Android Gradle Plugin together.
- Re-run local build and unit tests after each platform upgrade.

## RISK-006: Monetization Misalignment

Status: Mitigated for V1; open until paid features are approved.

Description: Ads can contradict the distraction-reduction promise.

Mitigation:

- Avoid ads in V1.
- Prefer one-time Pro unlock later.
- Use `docs/PRO_UNLOCK_DESIGN.md` before implementing billing.
