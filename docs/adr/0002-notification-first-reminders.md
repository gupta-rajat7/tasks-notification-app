# ADR-0002: Use Notification-First Reminders For V1

## Status

Accepted.

## Context

The proposal originally emphasized overlays and accessibility monitoring. Those options add onboarding friction, user trust concerns, and Google Play policy risk.

## Decision

V1 will use standard Android notifications for reminders. AccessibilityService, Usage Access, overlay permission, and battery-optimization exemptions are out of scope unless explicitly approved later.

## Consequences

Positive:

- Easier onboarding.
- Lower policy risk.
- Lower implementation complexity.
- Better alignment with a calm productivity app.

Tradeoffs:

- V1 may not perfectly detect all screen-active behavior.
- Stronger monitoring may require future optional permissions.

