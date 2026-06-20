# Roadmap

## Phase 0: Project Setup

Goal: Make the project easy to run, easy to understand, and easy to split across sessions.

Deliverables:

- Project documents.
- Android project scaffold.
- Build command documented.
- Basic CI or local quality commands documented.
- Windows owner testing guide.

Exit criteria:

- A fresh Codex session can read the docs and know what to build next.
- Android project builds.
- Product owner can run the app locally with documented steps.

## Phase 1: App Shell

Goal: Create the native Android UI skeleton.

Deliverables:

- Compose app shell.
- Material 3 theme.
- Navigation for Today, Tasks, Settings.
- Empty states.
- Basic settings persistence.

Exit criteria:

- App opens on emulator or device.
- Navigation works.
- Settings survive app restart.

## Phase 2: Google Tasks Read Sync

Goal: Read user tasks and cache them locally.

Deliverables:

- Credential Manager sign-in.
- Google Tasks API client.
- Room database.
- Repository layer.
- Manual sync.
- Background sync.

Exit criteria:

- User can sign in.
- Tasks appear in the app.
- Cached tasks render offline.

## Phase 3: Reminder Engine

Goal: Deliver useful reminders without intrusive permissions.

Deliverables:

- Reminder decision engine.
- Notification channel.
- Notification actions.
- Snooze and review state.
- Quiet hours.

Exit criteria:

- Reminder appears only when pending tasks exist.
- Snooze and Review actions work.
- Quiet hours suppress reminders.

## Phase 4: Polish And Beta

Goal: Prepare for real-user testing.

Deliverables:

- Onboarding copy.
- Lightweight UI/UX plan.
- Error states.
- Permission recovery UI.
- Optional Settings-only screen-activity mode with remaining physical-device validation.
- Basic manual QA pass.
- Privacy policy draft in `docs/PRIVACY_POLICY_DRAFT.md`.
- Store listing draft in `docs/STORE_LISTING_DRAFT.md`.
- OAuth owner setup guide in `docs/PO_GOOGLE_OAUTH_SETUP_GUIDE.md`.
- Performance test guide in `docs/PERFORMANCE_TEST_GUIDE.md`.

Exit criteria:

- 20 to 50 testers can install and use the app.
- Known issues are tracked in the backlog.

## Phase 5: Monetization Experiment

Goal: Add monetization only after product usefulness is validated.

Deliverables:

- One-time Pro unlock design in `docs/PRO_UNLOCK_DESIGN.md`.
- Pro feature list.
- Play Billing integration after product-owner approval.
- Purchase restore after billing approval.

Exit criteria:

- Free app remains useful.
- Pro features are clearly incremental.
- No ad SDK is introduced.
