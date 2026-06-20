# Screen Active Task Reminder

Android productivity app that reminds users to review pending tasks after a configurable review interval. Standard reminders work without advanced permissions. An optional Settings-only screen-activity mode can add recent Android activity evidence after the user grants Usage Access.

The project is intentionally local-first, lightweight, and beginner-friendly:

- Native Android app using Kotlin, Jetpack Compose, and Material 3.
- Google Tasks as the task source of truth.
- Room cache for fast local reads.
- DataStore for user settings.
- Notification-first reminder experience for V1.
- No paid backend, subscriptions, licenses, or hosted infrastructure required for development.

## Current Product Direction

Build a free V1 that proves users want the behavior, then add a one-time Pro upgrade for advanced settings. Do not start with ads. Ads conflict with the product promise of reducing distraction.

V1 must not make Usage Access, AccessibilityService, or forced overlays part of default onboarding. Usage Access is available only as an optional Settings feature because it adds setup friction and sensitive-permission obligations. AccessibilityService should remain a last-resort path because it creates higher user-trust and Play Store policy risk for a general productivity app.

## Project Docs

- [Project Brief](docs/PROJECT_BRIEF.md)
- [Product Requirements](docs/PRD.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Project Status](docs/PROJECT_STATUS.md)
- [Screen Activity Feasibility](docs/SCREEN_ACTIVITY_FEASIBILITY.md)
- [Product Owner Google OAuth Setup Guide](docs/PO_GOOGLE_OAUTH_SETUP_GUIDE.md)
- [Google Sign-In Setup](docs/GOOGLE_SIGN_IN_SETUP.md)
- [Roadmap](docs/ROADMAP.md)
- [Backlog](docs/BACKLOG.md)
- [Development Workflow](docs/DEVELOPMENT_WORKFLOW.md)
- [Local Android Build Setup](docs/LOCAL_ANDROID_SETUP.md)
- [Windows Readiness Check](docs/WINDOWS_READINESS_CHECK.md)
- [End-To-End Test Readiness](docs/E2E_TEST_READINESS.md)
- [Windows Run App Guide](docs/WINDOWS_RUN_APP_GUIDE.md)
- [PO Windows Test Guide](docs/PO_WINDOWS_TEST_GUIDE.md)
- [Google Play Store Release Guide](docs/PLAY_STORE_RELEASE_GUIDE.md)
- [Lightweight UI/UX Plan](docs/UI_UX_PLAN.md)
- [QA Plan](docs/QA_PLAN.md)
- [Risk Register](docs/RISKS.md)
- [Session Handoff Template](docs/SESSION_HANDOFF_TEMPLATE.md)
- [Architecture Decision Records](docs/adr/)

## Build The Android App

Prerequisites:

- JDK 17 available through `JAVA_HOME` or `PATH`.
- Android SDK Platform 36 installed.
- `local.properties` present locally when Android Studio has not already configured the SDK path:

```properties
sdk.dir=C:\\Users\\<you>\\AppData\\Local\\Android\\Sdk
```

Build the debug APK:

```powershell
.\gradlew.bat :app:assembleDebug
```

Run JVM unit tests:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

## How To Use Multiple Codex Sessions

Use one session per bounded slice:

- Session A: Android scaffold and build system.
- Session B: Product docs and onboarding UX copy.
- Session C: Google Tasks sync and local data model.
- Session D: Reminder engine, notifications, and settings.

Each session should read `docs/DEVELOPMENT_WORKFLOW.md`, pick a backlog item from `docs/BACKLOG.md`, work in a separate branch, and leave a handoff note using `docs/SESSION_HANDOFF_TEMPLATE.md`.

## First Build Milestone

The first real milestone is not monetization or advanced monitoring. It is a working Android app that:

1. Opens to a simple task dashboard.
2. Lets the user sign in with Google.
3. Reads Google Tasks.
4. Caches tasks locally.
5. Sends a reminder notification after a configured review interval.
6. Lets the user snooze or mark the reminder as reviewed.
