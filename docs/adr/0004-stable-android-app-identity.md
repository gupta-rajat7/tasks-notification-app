# ADR-0004: Use Stable Android App Identity Before Google Sign-In

## Status

Accepted.

## Context

Google Sign-In, Google Tasks API access, Play Store setup, and installed-app updates all depend on a stable Android application ID. The scaffold used the temporary package `com.example.taskreminder`, which is not suitable for OAuth configuration or public distribution.

## Decision

Use the following Android identity for V1 development:

- Display name: `Screen Active Task Reminder`
- Android namespace: `com.guptarajat.screenactivetaskreminder`
- Android application ID: `com.guptarajat.screenactivetaskreminder`

## Consequences

Positive:

- Google OAuth configuration can be created against the intended package.
- Future Play Store setup can use the same application ID.
- Development builds no longer carry a scaffold placeholder identity.

Tradeoffs:

- If the product name changes materially before launch, the installed package identity should still remain stable unless there is a strong reason to break update continuity.
