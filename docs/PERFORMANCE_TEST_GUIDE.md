# Performance Test Guide

This guide explains how to judge whether the app is slow, or whether the Windows Android emulator is slow.

## Current Interpretation

If the app feels slow on the Windows emulator, do not treat that as product performance proof yet. The emulator can be slow because it is running Android inside Windows, while Gradle, PowerShell, and Codex may also be using CPU, memory, and disk.

The app must still feel responsive on a real Android phone before beta.

## Quick Emulator Tips

- Let the emulator fully boot before opening the app.
- Close extra browser tabs and heavy apps while testing.
- Keep only one emulator running.
- Wait 30 to 60 seconds after installing before judging smoothness.
- Prefer testing by clicking inside the app, not while Gradle is still building.
- Restart the emulator if Android home screen or Settings is also slow.

## Recommended Test Split

Use the Windows emulator for:

- Install smoke tests.
- Basic navigation.
- Permission prompt checks.
- Screenshots.
- First-pass debugging.

Use a real Android phone for:

- Real responsiveness.
- Notification behavior.
- Background reminder timing.
- Google sign-in reliability.
- Battery and permission behavior.
- Any future screen-activity testing.

## Simple Responsiveness Checklist

Record whether each item feels instant, slightly delayed, or slow:

- App launches from icon.
- Bottom navigation switches between Today, Tasks, and Settings.
- Settings plus/minus controls respond.
- Toggles move immediately.
- Tasks screen opens.
- Sync button shows progress quickly.
- Notification recovery status refreshes.

If the emulator is slow everywhere, including Android Settings and the home screen, report it as emulator slowness. If only this app is slow while Android itself is fast, report it as app slowness.

## Product Owner Report Format

Send Codex:

- Device used: Windows emulator or real phone model.
- What felt slow.
- Whether Android home screen also felt slow.
- Whether Gradle/build was running at the same time.
- Screenshot or short screen recording if possible.

## Performance Guardrails For Developers

- Keep V1 lightweight.
- Do not add ads SDKs.
- Do not add analytics SDKs without approval.
- Keep task sync manual and explicit until background sync is approved.
- Prefer local Room/DataStore reads for UI.
- Avoid exact alarms unless product owner approves the permission and policy tradeoff.
