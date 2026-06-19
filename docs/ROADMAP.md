# Roadmap

## Phase 0: Project Setup

Goal: Make the project easy to run, easy to understand, and easy to split across sessions.

Deliverables:

- Project documents.
- Android project scaffold.
- Build command documented.
- Basic CI or local quality commands documented.

Exit criteria:

- A fresh Codex session can read the docs and know what to build next.
- Android project builds.

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
- Error states.
- Permission recovery UI.
- Basic manual QA pass.
- Privacy policy draft.
- Store listing draft.

Exit criteria:

- 20 to 50 testers can install and use the app.
- Known issues are tracked in the backlog.

## Phase 5: Monetization Experiment

Goal: Add monetization only after product usefulness is validated.

Deliverables:

- One-time Pro unlock design.
- Pro feature list.
- Play Billing integration.
- Purchase restore.

Exit criteria:

- Free app remains useful.
- Pro features are clearly incremental.
- No ad SDK is introduced.

