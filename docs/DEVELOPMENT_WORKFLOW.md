# Development Workflow

## Working Style

This project should be built in small slices. Each slice should leave the app buildable and the docs clearer than before.

## Multi-Session Rules

Use separate Codex sessions for separate backlog items.

Each session should:

1. Read `README.md`, `docs/PRD.md`, `docs/ARCHITECTURE.md`, and `docs/BACKLOG.md`.
2. Pick one backlog item.
3. State the chosen backlog ID in its first response.
4. Work on a branch named `codex/<backlog-id-short-description>`.
5. Avoid changing unrelated files.
6. Run the relevant verification commands.
7. Leave a handoff note using `docs/SESSION_HANDOFF_TEMPLATE.md`.

## Branching

Suggested branch names:

- `codex/app-001-android-scaffold`
- `codex/app-002-compose-shell`
- `codex/sync-001-google-sign-in`
- `codex/rem-001-reminder-rules`

## Definition Of Done

A slice is done when:

- Code builds.
- Relevant tests pass.
- User-visible behavior matches the PRD.
- Docs are updated if behavior or architecture changed.
- Any limitations are written in the handoff.

## Preferred Order

1. APP-001.
2. APP-002.
3. APP-003.
4. SYNC-003.
5. SYNC-001.
6. SYNC-002.
7. REM-001.
8. INFRA-001.
9. REM-002.
10. UX-001.

Reason: build the local app shell and state model before adding Google API complexity.

## Code Principles

- Prefer simple Kotlin and Compose patterns.
- Keep business logic testable outside Android framework classes.
- Render from local state.
- Treat network sync as a background update.
- Avoid adding analytics, ad SDKs, or paid SDKs in V1.
- Avoid advanced permissions until explicitly approved.

## Documentation Principles

- Update ADRs for major decisions.
- Update `docs/BACKLOG.md` when adding or completing planned work.
- Update `docs/RISKS.md` when a policy, store, or technical risk changes.
- Do not bury important decisions only in chat history.

## Android Build Commands

Run these commands from the repository root after installing JDK 17 and Android SDK Platform 36.

Build the debug APK:

```powershell
.\gradlew.bat --no-daemon --max-workers=1 :app:assembleDebug
```

Run JVM unit tests:

```powershell
.\gradlew.bat --no-daemon --max-workers=1 :app:testDebugUnitTest
```

On this Windows machine, use the local portable toolchain documented in `docs/LOCAL_ANDROID_SETUP.md`.
