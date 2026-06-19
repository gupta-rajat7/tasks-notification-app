# ADR-0001: Use Native Android With Kotlin And Compose

## Status

Accepted.

## Context

The app is Android-only and needs lightweight background behavior, notifications, local storage, and modern native UI.

## Decision

Use native Android with Kotlin, Jetpack Compose, and Material 3.

## Consequences

Positive:

- Best fit for Android permissions and background APIs.
- Modern UI with less boilerplate.
- No cross-platform framework dependency.
- Strong alignment with Android documentation.

Tradeoffs:

- No iOS reuse.
- Developer must learn Android-native project structure.

